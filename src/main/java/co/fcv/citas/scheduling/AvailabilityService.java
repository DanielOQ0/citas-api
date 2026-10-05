package co.fcv.citas.scheduling;

import static co.fcv.citas.scheduling.Access.badRequest;
import static co.fcv.citas.scheduling.Access.conflict;
import static co.fcv.citas.scheduling.Access.notFound;

import co.fcv.citas.scheduling.SchedulingDtos.AvailabilityBlockItem;
import co.fcv.citas.scheduling.SchedulingDtos.AvailabilityBlockRequest;
import co.fcv.citas.scheduling.SchedulingDtos.AvailabilityItem;
import co.fcv.citas.scheduling.domain.SlotPolicy;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Bloques de disponibilidad del profesional (HU-013, HU-014) y búsqueda de franjas (HU-015). */
@Service
public class AvailabilityService {
  /** Slot retenido por una reprogramación PENDING (RF-15): no se ofrece ni se reserva. */
  static final String HELD_BY_PENDING_RESCHEDULE = """
      exists (select 1 from reschedule_request_slots h
              join reschedule_requests rr on rr.id = h.request_id
              join reschedule_request_statuses rrs on rrs.id = rr.status_id
              where h.slot_id = ps.id and rrs.code = 'PENDING')""";
  private static final String BLOCK_SELECT = """
      select b.id, b.location_id, l.name, b.available_date, b.start_time, b.end_time,
             (select count(*) from professional_slots ps where ps.availability_block_id = b.id),
             (select count(*) from professional_slots ps where ps.availability_block_id = b.id and (ps.appointment_id is not null or %s))
      from availability_blocks b join locations l on l.id = b.location_id
      """.formatted(HELD_BY_PENDING_RESCHEDULE);
  private static final RowMapper<AvailabilityBlockItem> BLOCK = (rs, n) -> new AvailabilityBlockItem(rs.getLong(1), rs.getLong(2),
      rs.getString(3), rs.getObject(4, LocalDate.class), rs.getObject(5, LocalTime.class), rs.getObject(6, LocalTime.class), rs.getInt(7),
      rs.getInt(8));

  private final JdbcTemplate jdbc;
  private final ProfessionalService professionals;
  private final Clock clock;

  public AvailabilityService(JdbcTemplate jdbc, ProfessionalService professionals, Clock clock) {
    this.jdbc = jdbc;
    this.professionals = professionals;
    this.clock = clock;
  }

  /** HU-014: solo los bloques propios, filtrables por fecha y sede. */
  public List<AvailabilityBlockItem> blocks(LocalDate date, Long locationId, Authentication auth) {
    long professional = professionals.professionalIdOf(auth, false);
    StringBuilder where = new StringBuilder(" where b.professional_id = ? and b.active = true");
    List<Object> args = new ArrayList<>(List.of(professional));
    if (date != null) {
      where.append(" and b.available_date = ?");
      args.add(date);
    }
    if (locationId != null) {
      where.append(" and b.location_id = ?");
      args.add(locationId);
    }
    return jdbc.query(BLOCK_SELECT + where + " order by b.available_date, b.start_time", BLOCK, args.toArray());
  }

  @Transactional
  public AvailabilityBlockItem addBlock(AvailabilityBlockRequest request, Authentication auth) {
    long professional = professionals.professionalIdOf(auth, true);
    List<LocalDateTime> slots = SlotPolicy.blockSlots(request.date(), request.startTime(), request.endTime(), now());
    requireAssignedLocation(professional, request.locationId());
    requireNoOverlap(professional, request, null);
    KeyHolder key = new GeneratedKeyHolder();
    jdbc.update(connection -> {
      var statement = connection.prepareStatement(
          "insert into availability_blocks(professional_id, location_id, available_date, start_time, end_time, active) values (?, ?, ?, ?, ?, true)",
          new String[] {"id"});
      statement.setLong(1, professional);
      statement.setLong(2, request.locationId());
      statement.setObject(3, request.date());
      statement.setObject(4, request.startTime());
      statement.setObject(5, request.endTime());
      return statement;
    }, key);
    long id = ((Number) key.getKey()).longValue();
    insertSlots(id, slots);
    return block(id, professional);
  }

  @Transactional
  public AvailabilityBlockItem updateBlock(long id, AvailabilityBlockRequest request, Authentication auth) {
    long professional = professionals.professionalIdOf(auth, true);
    requireOwnBlock(id, professional);
    requireNotCommitted(id, "No se puede editar un bloque con citas reservadas o retenidas");
    List<LocalDateTime> slots = SlotPolicy.blockSlots(request.date(), request.startTime(), request.endTime(), now());
    requireAssignedLocation(professional, request.locationId());
    requireNoOverlap(professional, request, id);
    jdbc.update("update availability_blocks set location_id = ?, available_date = ?, start_time = ?, end_time = ? where id = ?",
        request.locationId(), request.date(), request.startTime(), request.endTime(), id);
    jdbc.update("delete from professional_slots where availability_block_id = ?", id);
    insertSlots(id, slots);
    return block(id, professional);
  }

  @Transactional
  public void deleteBlock(long id, Authentication auth) {
    long professional = professionals.professionalIdOf(auth, true);
    requireOwnBlock(id, professional);
    requireNotCommitted(id, "No se puede eliminar un bloque con citas reservadas o retenidas");
    jdbc.update("delete from professional_slots where availability_block_id = ?", id);
    jdbc.update("delete from availability_blocks where id = ?", id);
  }

  /**
   * HU-015: franjas que completan toda la duración (60 min = 2 slots consecutivos libres), solo de especialidad activa,
   * profesional activo con la especialidad y la sede asignadas, y sin slots ocupados, retenidos ni pasados.
   */
  public List<AvailabilityItem> availability(long locationId, long specialtyId, Long professionalId, LocalDate date) {
    List<Integer> durations = jdbc.queryForList("select appointment_duration_minutes from specialties where id = ? and active = true",
        Integer.class, specialtyId);
    if (durations.isEmpty()) return List.of();
    int duration = durations.getFirst();
    StringBuilder sql = new StringBuilder("""
        select p.id, concat(u.first_name, ' ', u.last_name), ps.start_at
        from professional_slots ps
        join availability_blocks b on b.id = ps.availability_block_id
        join professionals p on p.id = b.professional_id
        join users u on u.id = p.user_id
        join professional_specialties x on x.professional_id = p.id and x.specialty_id = ? and x.active = true
        join professional_locations pl on pl.professional_id = p.id and pl.location_id = b.location_id and pl.active = true
        where b.location_id = ? and b.active = true and p.active = true and ps.appointment_id is null
          and ps.start_at >= ? and ps.start_at < ? and not %s""".formatted(HELD_BY_PENDING_RESCHEDULE));
    List<Object> args = new ArrayList<>(List.of(specialtyId, locationId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
    if (professionalId != null) {
      sql.append(" and p.id = ?");
      args.add(professionalId);
    }
    Map<Long, String> names = new LinkedHashMap<>();
    Map<Long, List<LocalDateTime>> freeByProfessional = new LinkedHashMap<>();
    jdbc.query(sql + " order by p.id, ps.start_at", rs -> {
      names.put(rs.getLong(1), rs.getString(2));
      freeByProfessional.computeIfAbsent(rs.getLong(1), k -> new ArrayList<>()).add(rs.getObject(3, LocalDateTime.class));
    }, args.toArray());
    LocalDateTime now = now();
    List<AvailabilityItem> items = new ArrayList<>();
    freeByProfessional.forEach((professional, free) -> SlotPolicy.bookableStarts(free, duration, now).forEach(start -> items.add(
        new AvailabilityItem(professional, names.get(professional), locationId, specialtyId, start.toLocalDate(), start.toLocalTime(), duration))));
    items.sort((a, b) -> a.startTime().compareTo(b.startTime()));
    return items;
  }

  private AvailabilityBlockItem block(long id, long professional) {
    return jdbc.query(BLOCK_SELECT + " where b.id = ? and b.professional_id = ?", BLOCK, id, professional).getFirst();
  }

  private void insertSlots(long blockId, List<LocalDateTime> slots) {
    for (LocalDateTime start : slots) {
      jdbc.update("insert into professional_slots(availability_block_id, start_at, end_at) values (?, ?, ?)",
          blockId, start, start.plusMinutes(SlotPolicy.SLOT_MINUTES));
    }
  }

  /** RN-07: un profesional solo publica agenda en sedes asignadas. */
  private void requireAssignedLocation(long professional, long locationId) {
    if (count("select count(*) from professional_locations pl join locations l on l.id = pl.location_id "
        + "where pl.professional_id = ? and pl.location_id = ? and pl.active = true and l.active = true", professional, locationId) == 0) {
      throw badRequest("La sede no está asignada al profesional");
    }
  }

  private void requireNoOverlap(long professional, AvailabilityBlockRequest request, Long exceptId) {
    String sql = "select count(*) from availability_blocks where professional_id = ? and available_date = ? and active = true "
        + "and start_time < ? and end_time > ?" + (exceptId == null ? "" : " and id <> ?");
    Object[] args = exceptId == null
        ? new Object[] {professional, request.date(), request.endTime(), request.startTime()}
        : new Object[] {professional, request.date(), request.endTime(), request.startTime(), exceptId};
    if (count(sql, args) > 0) throw conflict("El bloque se solapa con otro existente");
  }

  private void requireOwnBlock(long id, long professional) {
    if (count("select count(*) from availability_blocks where id = ? and professional_id = ? and active = true", id, professional) == 0) {
      throw notFound("Bloque no encontrado");
    }
  }

  /** D-13: una cita APPROVED/REQUESTED o una retención de reprogramación PENDING protegen el bloque. */
  private void requireNotCommitted(long blockId, String message) {
    if (count("select count(*) from professional_slots ps where ps.availability_block_id = ? and (ps.appointment_id is not null or "
        + HELD_BY_PENDING_RESCHEDULE + ")", blockId) > 0) {
      throw conflict(message);
    }
  }

  private int count(String sql, Object... args) {
    Integer value = jdbc.queryForObject(sql, Integer.class, args);
    return value == null ? 0 : value;
  }

  private LocalDateTime now() {
    return LocalDateTime.now(clock);
  }
}
