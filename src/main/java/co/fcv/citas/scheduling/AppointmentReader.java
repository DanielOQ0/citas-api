package co.fcv.citas.scheduling;

import co.fcv.citas.scheduling.SchedulingDtos.AppointmentItem;
import co.fcv.citas.scheduling.SchedulingDtos.RescheduleItem;
import co.fcv.citas.scheduling.SchedulingDtos.RescheduleSummary;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/** Proyecciones de lectura de citas y reprogramaciones con nombres de catálogo (HU-019, HU-022, HU-023). */
@Component
class AppointmentReader {
  private static final String APPOINTMENT_SELECT = """
      select a.id, st.code, concat(pu.first_name, ' ', pu.last_name), a.professional_id, concat(du.first_name, ' ', du.last_name),
             a.location_id, l.name, a.specialty_id, s.name, s.requires_admin_approval, a.scheduled_start_at, a.scheduled_end_at, a.reason,
             (select h.reason from appointment_status_history h join appointment_statuses hs on hs.id = h.status_id
               where h.appointment_id = a.id and hs.code = 'REJECTED' order by h.changed_at desc, h.id desc limit 1)
      from appointments a
      join appointment_statuses st on st.id = a.status_id
      join users pu on pu.id = a.patient_user_id
      join professionals p on p.id = a.professional_id
      join users du on du.id = p.user_id
      join locations l on l.id = a.location_id
      join specialties s on s.id = a.specialty_id
      """;
  private static final String RESCHEDULE_SELECT = """
      select r.id, r.appointment_id, rs.code, concat(pu.first_name, ' ', pu.last_name), a.professional_id, concat(du.first_name, ' ', du.last_name),
             a.specialty_id, s.name, r.requested_location_id, l.name, r.previous_start_at, r.requested_start_at, r.requested_end_at,
             r.decision_reason, r.patient_action_after_rejection
      from reschedule_requests r
      join reschedule_request_statuses rs on rs.id = r.status_id
      join appointments a on a.id = r.appointment_id
      join users pu on pu.id = a.patient_user_id
      join professionals p on p.id = a.professional_id
      join users du on du.id = p.user_id
      join specialties s on s.id = a.specialty_id
      join locations l on l.id = r.requested_location_id
      """;
  private static final RowMapper<RescheduleItem> RESCHEDULE = (rs, n) -> {
    LocalDateTime previous = rs.getObject(11, LocalDateTime.class);
    LocalDateTime requested = rs.getObject(12, LocalDateTime.class);
    LocalDateTime requestedEnd = rs.getObject(13, LocalDateTime.class);
    return new RescheduleItem(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getLong(5), rs.getString(6), rs.getLong(7),
        rs.getString(8), rs.getLong(9), rs.getString(10), previous.toLocalDate(), previous.toLocalTime(), requested.toLocalDate(),
        requested.toLocalTime(), minutes(requested, requestedEnd), rs.getString(14), rs.getString(15));
  };

  private final JdbcTemplate jdbc;
  private final NamedParameterJdbcTemplate named;

  AppointmentReader(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
    this.named = new NamedParameterJdbcTemplate(jdbc);
  }

  /** Citas que cumplen el filtro (cláusula where sobre los alias de APPOINTMENT_SELECT), ordenadas por inicio. */
  List<AppointmentItem> appointments(String where, Object... args) {
    List<Row> rows = jdbc.query(APPOINTMENT_SELECT + where + " order by a.scheduled_start_at, a.id", (rs, n) -> new Row(
        rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getString(5), rs.getLong(6), rs.getString(7), rs.getLong(8),
        rs.getString(9), rs.getBoolean(10), rs.getObject(11, LocalDateTime.class), rs.getObject(12, LocalDateTime.class), rs.getString(13),
        rs.getString(14)), args);
    Map<Long, RescheduleSummary> reschedules = latestReschedules(rows.stream().map(Row::id).toList());
    return rows.stream().map(row -> row.toItem(reschedules.get(row.id()))).toList();
  }

  AppointmentItem appointment(long id) {
    List<AppointmentItem> items = appointments(" where a.id = ?", id);
    if (items.isEmpty()) throw Access.notFound("Cita no encontrada");
    return items.getFirst();
  }

  List<RescheduleItem> reschedules(String where, Object... args) {
    return jdbc.query(RESCHEDULE_SELECT + where + " order by r.requested_start_at, r.id", RESCHEDULE, args);
  }

  RescheduleItem reschedule(long id) {
    List<RescheduleItem> items = reschedules(" where r.id = ?", id);
    if (items.isEmpty()) throw Access.notFound("Solicitud de reprogramación no encontrada");
    return items.getFirst();
  }

  private Map<Long, RescheduleSummary> latestReschedules(List<Long> appointmentIds) {
    Map<Long, RescheduleSummary> latest = new HashMap<>();
    if (appointmentIds.isEmpty()) return latest;
    named.query("""
        select r.appointment_id, r.id, rs.code, r.requested_start_at, r.decision_reason, r.patient_action_after_rejection
        from reschedule_requests r join reschedule_request_statuses rs on rs.id = r.status_id
        where r.appointment_id in (:ids) order by r.id desc""",
        new MapSqlParameterSource("ids", appointmentIds), rs -> {
          LocalDateTime requested = rs.getObject(4, LocalDateTime.class);
          latest.putIfAbsent(rs.getLong(1), new RescheduleSummary(rs.getLong(2), rs.getString(3), requested.toLocalDate(),
              requested.toLocalTime(), rs.getString(5), rs.getString(6)));
        });
    return latest;
  }

  private static int minutes(LocalDateTime start, LocalDateTime end) {
    return (int) Duration.between(start, end).toMinutes();
  }

  private record Row(long id, String status, String patientName, long professionalId, String professionalName, long locationId,
                     String locationName, long specialtyId, String specialtyName, boolean requiresAdminApproval, LocalDateTime start,
                     LocalDateTime end, String reason, String rejectionReason) {
    AppointmentItem toItem(RescheduleSummary reschedule) {
      LocalTime endTime = end.toLocalTime();
      // La duración es la real de la cita, no la configurada hoy en la especialidad (HU-012).
      return new AppointmentItem(id, status, patientName, professionalId, professionalName, locationId, locationName, specialtyId,
          specialtyName, requiresAdminApproval, start.toLocalDate(), start.toLocalTime(), endTime, minutes(start, end), reason,
          rejectionReason, reschedule);
    }
  }
}
