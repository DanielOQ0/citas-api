package co.fcv.citas.scheduling;

import static co.fcv.citas.scheduling.Access.conflict;
import static co.fcv.citas.scheduling.Access.forbidden;
import static co.fcv.citas.scheduling.Access.notFound;
import static co.fcv.citas.scheduling.Access.requireRole;
import static co.fcv.citas.scheduling.Access.userId;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.RescheduleDecisionRequest;
import co.fcv.citas.scheduling.SchedulingDtos.RescheduleItem;
import co.fcv.citas.scheduling.SchedulingDtos.RescheduleRequest;
import co.fcv.citas.scheduling.domain.AppointmentRules;
import co.fcv.citas.scheduling.domain.AppointmentStatus;
import co.fcv.citas.scheduling.domain.RescheduleStatus;
import co.fcv.citas.scheduling.domain.SlotPolicy;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reprogramación (HU-021, HU-022): conserva profesional, especialidad y sede; la nueva franja se retiene mientras la
 * solicitud está PENDING y la cita original mantiene la suya hasta que ADMIN decida (RN-10).
 */
@Service
public class RescheduleService {
  private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private final JdbcTemplate jdbc;
  private final AppointmentService appointments;
  private final AppointmentReader reader;
  private final StatusHistory history;
  private final Clock clock;

  public RescheduleService(JdbcTemplate jdbc, AppointmentService appointments, AppointmentReader reader, StatusHistory history, Clock clock) {
    this.jdbc = jdbc;
    this.appointments = appointments;
    this.reader = reader;
    this.history = history;
    this.clock = clock;
  }

  @Transactional
  public RescheduleItem request(long appointmentId, RescheduleRequest request, Authentication auth) {
    requireRole(auth, Role.USER);
    long patient = userId(auth);
    AppointmentService.Locked appointment = appointments.lock(appointmentId);
    if (appointment.patientId() != patient) throw forbidden();
    LocalDateTime requested = LocalDateTime.of(request.date(), request.startTime());
    boolean hasPending = !jdbc.queryForList("""
        select r.id from reschedule_requests r join reschedule_request_statuses rs on rs.id = r.status_id
        where r.appointment_id = ? and rs.code = 'PENDING'""", Long.class, appointmentId).isEmpty();
    AppointmentRules.requireReschedulable(appointment.status(), appointment.start(), requested, hasPending, now());
    int duration = (int) Duration.between(appointment.start(), appointment.end()).toMinutes();
    List<Long> slots = appointments.lockFreeSlots(appointment.professionalId(), appointment.locationId(), requested, requested.plusMinutes(duration));
    SlotPolicy.requireFullCoverage(slots.size(), duration);

    KeyHolder key = new GeneratedKeyHolder();
    jdbc.update(connection -> {
      var statement = connection.prepareStatement("""
          insert into reschedule_requests(appointment_id, requested_by_user_id, requested_location_id, status_id, previous_start_at,
                                          previous_end_at, requested_start_at, requested_end_at)
          values (?, ?, ?, (select id from reschedule_request_statuses where code = 'PENDING'), ?, ?, ?, ?)""", new String[] {"id"});
      statement.setLong(1, appointmentId);
      statement.setLong(2, patient);
      statement.setLong(3, appointment.locationId());
      statement.setObject(4, appointment.start());
      statement.setObject(5, appointment.end());
      statement.setObject(6, requested);
      statement.setObject(7, requested.plusMinutes(duration));
      return statement;
    }, key);
    long id = ((Number) key.getKey()).longValue();
    for (Long slot : slots) jdbc.update("insert into reschedule_request_slots(request_id, slot_id) values (?, ?)", id, slot);
    return reader.reschedule(id);
  }

  /** HU-022 CA-03: bandeja PENDING filtrable por sede, profesional, especialidad y fecha propuesta. */
  public List<RescheduleItem> pending(Long locationId, Long professionalId, Long specialtyId, LocalDate date, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    StringBuilder where = new StringBuilder(" where rs.code = 'PENDING'");
    List<Object> args = new ArrayList<>();
    if (locationId != null) { where.append(" and r.requested_location_id = ?"); args.add(locationId); }
    if (professionalId != null) { where.append(" and a.professional_id = ?"); args.add(professionalId); }
    if (specialtyId != null) { where.append(" and a.specialty_id = ?"); args.add(specialtyId); }
    if (date != null) {
      where.append(" and r.requested_start_at >= ? and r.requested_start_at < ?");
      args.add(date.atStartOfDay());
      args.add(date.plusDays(1).atStartOfDay());
    }
    return reader.reschedules(where.toString(), args.toArray());
  }

  /**
   * HU-022: aprobar libera los slots antiguos, asigna los retenidos y mueve la cita (queda auditado, D-20);
   * rechazar exige motivo, libera solo la retención y conserva la cita original.
   */
  @Transactional
  public RescheduleItem decide(long id, RescheduleDecisionRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    long admin = userId(auth);
    if (jdbc.queryForList("select id from reschedule_requests where id = ? for update", Long.class, id).isEmpty()) {
      throw notFound("Solicitud de reprogramación no encontrada");
    }
    RescheduleItem current = reader.reschedule(id);
    AppointmentService.Locked appointment = appointments.lock(current.appointmentId());
    LocalDateTime requested = LocalDateTime.of(current.requestedDate(), current.requestedStart());
    boolean approve = "APPROVE".equals(request.decision());
    RescheduleStatus next = AppointmentRules.decideReschedule(RescheduleStatus.valueOf(current.status()), approve, request.reason(),
        appointment.status(), requested, now());
    if (next == RescheduleStatus.APPROVED) {
      LocalDateTime requestedEnd = requested.plusMinutes(current.durationMinutes());
      List<Long> slots = jdbc.queryForList("select slot_id from reschedule_request_slots where request_id = ?", Long.class, id);
      if (slots.size() != SlotPolicy.requiredSlots(current.durationMinutes())) {
        // Solicitudes anteriores a la retención (V4) no tienen slots retenidos: se toman ahora si siguen libres.
        slots = appointments.lockFreeSlots(appointment.professionalId(), current.locationId(), requested, requestedEnd);
        SlotPolicy.requireFullCoverage(slots.size(), current.durationMinutes());
      }
      appointments.releaseSlots(appointment.id());
      appointments.assignSlots(appointment.id(), slots);
      jdbc.update("update appointments set scheduled_start_at = ?, scheduled_end_at = ?, location_id = ? where id = ?",
          requested, requestedEnd, current.locationId(), appointment.id());
      history.record(appointment.id(), AppointmentStatus.APPROVED, admin, StatusHistory.Source.ADMIN,
          "Reprogramada: " + WHEN.format(appointment.start()) + " → " + WHEN.format(requested));
    }
    // La retención solo vive mientras la solicitud está PENDING; sin borrarla, el slot no podría retenerse de nuevo (UNIQUE slot_id).
    jdbc.update("delete from reschedule_request_slots where request_id = ?", id);
    jdbc.update("""
        update reschedule_requests set status_id = (select id from reschedule_request_statuses where code = ?), decision_reason = ?,
               decided_by_user_id = ?, decided_at = ? where id = ?""",
        next.name(), request.reason() == null || request.reason().isBlank() ? null : request.reason().trim(), admin, now(), id);
    return reader.reschedule(id);
  }

  /** RF-15: tras un rechazo el paciente conserva su cita (o la cancela por el flujo normal, HU-020). */
  @Transactional
  public RescheduleItem keep(long appointmentId, long requestId, Authentication auth) {
    requireRole(auth, Role.USER);
    AppointmentService.Locked appointment = appointments.read(appointmentId);
    if (appointment.patientId() != userId(auth)) throw forbidden();
    RescheduleItem current = reader.reschedule(requestId);
    if (current.appointmentId() != appointmentId) throw notFound("Solicitud de reprogramación no encontrada");
    if (!RescheduleStatus.REJECTED.name().equals(current.status()) || current.patientAction() != null) {
      throw conflict("Solo una reprogramación rechazada y sin respuesta puede conservarse");
    }
    jdbc.update("update reschedule_requests set patient_action_after_rejection = 'KEEP_APPOINTMENT' where id = ?", requestId);
    return reader.reschedule(requestId);
  }

  private LocalDateTime now() {
    return LocalDateTime.now(clock);
  }
}
