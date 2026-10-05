package co.fcv.citas.scheduling;

import static co.fcv.citas.scheduling.Access.badRequest;
import static co.fcv.citas.scheduling.Access.forbidden;
import static co.fcv.citas.scheduling.Access.notFound;
import static co.fcv.citas.scheduling.Access.requireRole;
import static co.fcv.citas.scheduling.Access.userId;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import co.fcv.citas.scheduling.domain.AppointmentRules;
import co.fcv.citas.scheduling.domain.AppointmentStatus;
import co.fcv.citas.scheduling.domain.SlotPolicy;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Ciclo de vida de la cita: reserva, decisión ADMIN, consulta, cancelación, agenda, cierre e historial (HU-016 a HU-020, HU-023 a HU-025). */
@Service
public class AppointmentService {
  private final JdbcTemplate jdbc;
  private final AppointmentReader reader;
  private final ProfessionalService professionals;
  private final StatusHistory history;
  private final Clock clock;

  public AppointmentService(JdbcTemplate jdbc, AppointmentReader reader, ProfessionalService professionals, StatusHistory history, Clock clock) {
    this.jdbc = jdbc;
    this.reader = reader;
    this.professionals = professionals;
    this.history = history;
    this.clock = clock;
  }

  /**
   * HU-016/HU-017: la especialidad decide el flujo. Medicina General (sin aprobación) nace APPROVED por el sistema (RN-02);
   * una especializada nace REQUESTED y retiene sus slots (RN-03). Ningún slot ocupado o retenido se reasigna (RN-01).
   */
  @Transactional
  public AppointmentItem book(AppointmentRequest request, Authentication auth) {
    requireRole(auth, Role.USER);
    long patient = userId(auth);
    List<Map<String, Object>> specialty = jdbc.queryForList(
        "select appointment_duration_minutes, requires_admin_approval from specialties where id = ? and active = true", request.specialtyId());
    if (specialty.isEmpty()) throw badRequest("La especialidad no existe o está inactiva");
    int duration = ((Number) specialty.getFirst().get("appointment_duration_minutes")).intValue();
    boolean requiresApproval = isTrue(specialty.getFirst().get("requires_admin_approval"));
    if (count("""
        select count(*) from professionals p
        join professional_specialties x on x.professional_id = p.id and x.specialty_id = ? and x.active = true
        join professional_locations pl on pl.professional_id = p.id and pl.location_id = ? and pl.active = true
        where p.id = ? and p.active = true""", request.specialtyId(), request.locationId(), request.professionalId()) == 0) {
      throw badRequest("El profesional no está habilitado para esa sede y especialidad");
    }
    LocalDateTime start = LocalDateTime.of(request.date(), request.startTime());
    LocalDateTime now = now();
    SlotPolicy.requireFutureStart(start, now);
    List<Long> slots = lockFreeSlots(request.professionalId(), request.locationId(), start, start.plusMinutes(duration));
    SlotPolicy.requireFullCoverage(slots.size(), duration);

    AppointmentStatus status = requiresApproval ? AppointmentStatus.REQUESTED : AppointmentStatus.APPROVED;
    Long affiliation = jdbc.query("select id from user_insurance_affiliations where user_id = ? and is_current = true order by id desc",
        rs -> rs.next() ? rs.getLong(1) : null, patient);
    String reason = request.reason() == null || request.reason().isBlank() ? null : request.reason().trim();
    KeyHolder key = new GeneratedKeyHolder();
    jdbc.update(connection -> {
      var statement = connection.prepareStatement("""
          insert into appointments(patient_user_id, professional_id, location_id, specialty_id, insurance_affiliation_id, status_id, reason,
                                   scheduled_start_at, scheduled_end_at, created_by_user_id, approved_at)
          values (?, ?, ?, ?, ?, (select id from appointment_statuses where code = ?), ?, ?, ?, ?, ?)""", new String[] {"id"});
      statement.setLong(1, patient);
      statement.setLong(2, request.professionalId());
      statement.setLong(3, request.locationId());
      statement.setLong(4, request.specialtyId());
      statement.setObject(5, affiliation);
      statement.setString(6, status.name());
      statement.setString(7, reason);
      statement.setObject(8, start);
      statement.setObject(9, start.plusMinutes(duration));
      statement.setLong(10, patient);
      statement.setObject(11, status == AppointmentStatus.APPROVED ? now : null);
      return statement;
    }, key);
    long id = ((Number) key.getKey()).longValue();
    assignSlots(id, slots);
    if (status == AppointmentStatus.APPROVED) {
      history.record(id, status, null, StatusHistory.Source.SYSTEM, "Aprobación automática de cita general");
    } else {
      history.record(id, status, patient, StatusHistory.Source.USER, reason);
    }
    return reader.appointment(id);
  }

  /** HU-018 CA-01: bandeja REQUESTED filtrable por sede, profesional, especialidad y fecha. */
  public List<AppointmentItem> requested(Long locationId, Long professionalId, Long specialtyId, LocalDate date, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    Filter filter = new Filter(" where st.code = 'REQUESTED'");
    filter.add(locationId, " and a.location_id = ?");
    filter.add(professionalId, " and a.professional_id = ?");
    filter.add(specialtyId, " and a.specialty_id = ?");
    filter.dateRange(date, date);
    return reader.appointments(filter.where(), filter.args());
  }

  /** HU-018: aprobar conserva los slots; rechazar exige motivo y los libera (RN-04, RN-09). */
  @Transactional
  public AppointmentItem decide(long id, DecisionRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    long admin = userId(auth);
    Locked appointment = lock(id);
    boolean approve = "APPROVE".equals(request.decision());
    AppointmentStatus next = AppointmentRules.decide(appointment.status(), approve, request.reason(), appointment.start(), now());
    jdbc.update("update appointments set status_id = (select id from appointment_statuses where code = ?), approved_by_user_id = ?, approved_at = ? where id = ?",
        next.name(), admin, now(), id);
    if (next == AppointmentStatus.REJECTED) releaseSlots(id);
    history.record(id, next, admin, StatusHistory.Source.ADMIN, request.reason());
    return reader.appointment(id);
  }

  /** HU-019: citas propias filtrables por estado y rango de fechas. */
  public List<AppointmentItem> mine(String status, LocalDate from, LocalDate to, Authentication auth) {
    requireRole(auth, Role.USER);
    Filter filter = new Filter(" where a.patient_user_id = ?", userId(auth));
    if (status != null && !status.isBlank()) filter.add(AppointmentStatus.fromCode(status).name(), " and st.code = ?");
    filter.dateRange(from, to);
    return reader.appointments(filter.where(), filter.args());
  }

  /** HU-020: cancelar libera slots; su reprogramación PENDING se cancela y libera la retención (D-17). */
  @Transactional
  public void cancel(long id, Authentication auth) {
    requireRole(auth, Role.USER);
    long patient = userId(auth);
    Locked appointment = lock(id);
    if (appointment.patientId() != patient) throw forbidden();
    AppointmentRules.requireCancellable(appointment.status(), appointment.start(), now());
    jdbc.update("update appointments set status_id = (select id from appointment_statuses where code = 'CANCELLED') where id = ?", id);
    releaseSlots(id);
    jdbc.update("""
        update reschedule_requests set patient_action_after_rejection = 'CANCEL_APPOINTMENT'
        where appointment_id = ? and patient_action_after_rejection is null
          and status_id = (select id from reschedule_request_statuses where code = 'REJECTED')""", id);
    for (Long pending : jdbc.queryForList("""
        select r.id from reschedule_requests r join reschedule_request_statuses rs on rs.id = r.status_id
        where r.appointment_id = ? and rs.code = 'PENDING'""", Long.class, id)) {
      jdbc.update("delete from reschedule_request_slots where request_id = ?", pending);
      jdbc.update("update reschedule_requests set status_id = (select id from reschedule_request_statuses where code = 'CANCELLED'), decided_at = ? where id = ?",
          now(), pending);
    }
    history.record(id, AppointmentStatus.CANCELLED, patient, StatusHistory.Source.USER, null);
  }

  /** HU-025: lo leen el paciente dueño, el profesional de la cita y ADMIN (D-20). */
  public List<HistoryItem> history(long id, Authentication auth) {
    Locked appointment = read(id);
    boolean allowed = Access.hasRole(auth, Role.ADMIN)
        || (Access.hasRole(auth, Role.USER) && appointment.patientId() == userId(auth))
        || (Access.hasRole(auth, Role.PROFESSIONAL) && appointment.professionalUserId() == userId(auth));
    if (!allowed) throw forbidden();
    return history.of(id);
  }

  /** HU-023: solo citas APPROVED propias, por rango de fechas y sede; incluye solo el nombre del paciente (D-18). */
  public List<AppointmentItem> agenda(LocalDate from, LocalDate to, Long locationId, Authentication auth) {
    long professional = professionals.professionalIdOf(auth, false);
    Filter filter = new Filter(" where a.professional_id = ? and st.code = 'APPROVED'", professional);
    filter.dateRange(from, to);
    filter.add(locationId, " and a.location_id = ?");
    return reader.appointments(filter.where(), filter.args());
  }

  /** HU-024: el profesional de la cita la cierra como COMPLETED o NO_SHOW cuando ya terminó (D-19). */
  @Transactional
  public AppointmentItem close(long id, CloseAppointmentRequest request, Authentication auth) {
    long professional = professionals.professionalIdOf(auth, false);
    Locked appointment = lock(id);
    if (appointment.professionalId() != professional) throw forbidden();
    AppointmentStatus next = AppointmentRules.close(appointment.status(), AppointmentStatus.fromCode(request.status()), appointment.end(), now());
    jdbc.update("update appointments set status_id = (select id from appointment_statuses where code = ?) where id = ?", next.name(), id);
    history.record(id, next, userId(auth), StatusHistory.Source.USER, request.reason());
    return reader.appointment(id);
  }

  /** Slots libres y no retenidos del rango, bloqueados hasta el fin de la transacción para evitar doble reserva. */
  List<Long> lockFreeSlots(long professionalId, long locationId, LocalDateTime start, LocalDateTime end) {
    return jdbc.queryForList("""
        select ps.id from professional_slots ps join availability_blocks b on b.id = ps.availability_block_id
        where b.professional_id = ? and b.location_id = ? and b.active = true and ps.start_at >= ? and ps.end_at <= ?
          and ps.appointment_id is null and not %s
        order by ps.start_at for update""".formatted(AvailabilityService.HELD_BY_PENDING_RESCHEDULE),
        Long.class, professionalId, locationId, start, end);
  }

  void assignSlots(long appointmentId, List<Long> slots) {
    for (Long slot : slots) jdbc.update("update professional_slots set appointment_id = ? where id = ?", appointmentId, slot);
  }

  void releaseSlots(long appointmentId) {
    jdbc.update("update professional_slots set appointment_id = null where appointment_id = ?", appointmentId);
  }

  record Locked(long id, AppointmentStatus status, long patientId, long professionalId, long professionalUserId, long locationId,
                LocalDateTime start, LocalDateTime end) {}

  /** Bloquea solo la fila de la cita (no los catálogos del join) y devuelve su estado vigente. */
  Locked lock(long id) {
    if (jdbc.queryForList("select id from appointments where id = ? for update", Long.class, id).isEmpty()) throw notFound("Cita no encontrada");
    return read(id);
  }

  Locked read(long id) {
    List<Locked> rows = jdbc.query("""
        select a.id, st.code, a.patient_user_id, a.professional_id, p.user_id, a.location_id, a.scheduled_start_at, a.scheduled_end_at
        from appointments a join appointment_statuses st on st.id = a.status_id join professionals p on p.id = a.professional_id
        where a.id = ?""",
        (rs, n) -> new Locked(rs.getLong(1), AppointmentStatus.valueOf(rs.getString(2)), rs.getLong(3), rs.getLong(4), rs.getLong(5),
            rs.getLong(6), rs.getObject(7, LocalDateTime.class), rs.getObject(8, LocalDateTime.class)), id);
    if (rows.isEmpty()) throw notFound("Cita no encontrada");
    return rows.getFirst();
  }

  private LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  private int count(String sql, Object... args) {
    Integer value = jdbc.queryForObject(sql, Integer.class, args);
    return value == null ? 0 : value;
  }

  private static boolean isTrue(Object value) {
    return value instanceof Boolean b ? b : value instanceof Number n && n.intValue() != 0;
  }

  /** Construye cláusulas where con argumentos posicionales opcionales. */
  private static final class Filter {
    private final StringBuilder where;
    private final List<Object> args = new ArrayList<>();

    Filter(String base, Object... initial) {
      this.where = new StringBuilder(base);
      this.args.addAll(List.of(initial));
    }

    void add(Object value, String clause) {
      if (value == null) return;
      where.append(clause);
      args.add(value);
    }

    /** Rango de fechas por día completo sobre scheduled_start_at (usa el índice profesional/paciente + inicio). */
    void dateRange(LocalDate from, LocalDate to) {
      if (from != null) add(from.atStartOfDay(), " and a.scheduled_start_at >= ?");
      if (to != null) add(to.plusDays(1).atStartOfDay(), " and a.scheduled_start_at < ?");
    }

    String where() {
      return where.toString();
    }

    Object[] args() {
      return args.toArray();
    }
  }
}
