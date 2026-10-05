package co.fcv.citas.scheduling;

import co.fcv.citas.scheduling.SchedulingDtos.HistoryItem;
import co.fcv.citas.scheduling.domain.AppointmentStatus;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Auditoría de estados de cita (RF-19, HU-025): solo inserta y lee. No hay operaciones de edición ni borrado (RN-12).
 * La fuente admitida por el esquema es SYSTEM, USER o ADMIN; el profesional actúa como USER con su actor.
 */
@Component
class StatusHistory {
  enum Source { SYSTEM, USER, ADMIN }

  private final JdbcTemplate jdbc;
  private final Clock clock;

  StatusHistory(JdbcTemplate jdbc, Clock clock) {
    this.jdbc = jdbc;
    this.clock = clock;
  }

  void record(long appointmentId, AppointmentStatus status, Long actorId, Source source, String reason) {
    jdbc.update("""
        insert into appointment_status_history(appointment_id, status_id, changed_by_user_id, change_source, reason, changed_at)
        values (?, (select id from appointment_statuses where code = ?), ?, ?, ?, ?)""",
        appointmentId, status.name(), actorId, source.name(), reason == null || reason.isBlank() ? null : reason.trim(), LocalDateTime.now(clock));
  }

  List<HistoryItem> of(long appointmentId) {
    return jdbc.query("""
        select st.code, h.changed_by_user_id, case when u.id is null then null else concat(u.first_name, ' ', u.last_name) end,
               h.change_source, h.reason, h.changed_at
        from appointment_status_history h
        join appointment_statuses st on st.id = h.status_id
        left join users u on u.id = h.changed_by_user_id
        where h.appointment_id = ? order by h.changed_at, h.id""",
        (rs, n) -> new HistoryItem(rs.getString(1), (Long) rs.getObject(2, Long.class), rs.getString(3), rs.getString(4), rs.getString(5),
            rs.getObject(6, LocalDateTime.class)), appointmentId);
  }
}
