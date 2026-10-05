package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** HU-021 y HU-022. */
class RescheduleIntegrationTest extends IntegrationTestBase {
  private long general;
  private long admin;
  private Pro pro;
  private long patient;
  private AppointmentItem original;

  @BeforeEach
  void approvedAppointment() {
    general = generalSpecialty();
    admin = newUser(Role.ADMIN);
    pro = professional(admin, List.of(general), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "08:00", "10:00");
    patient = newUser(Role.USER);
    original = book(patient, pro, location("HIC"), general, day(1), "08:00");
  }

  private RescheduleItem request(long appointmentId, LocalDate date, String start, long patientId) {
    return reschedules.request(appointmentId, new RescheduleRequest(date, LocalTime.parse(start)), auth(patientId, Role.USER));
  }

  private List<LocalTime> starts() {
    return availability.availability(location("HIC"), general, pro.id(), day(1)).stream().map(AvailabilityItem::startTime).toList();
  }

  private AppointmentItem current() {
    return appointments.mine(null, null, null, auth(patient, Role.USER)).stream().filter(a -> a.id().equals(original.id())).findFirst().orElseThrow();
  }

  @Test
  void hu021RequestHoldsNewSlotAndPreservesOriginal() {
    assertStatus(400, () -> request(original.id(), day(1), "08:00", patient));
    assertStatus(400, () -> request(original.id(), day(-1), "09:00", patient));
    assertStatus(409, () -> request(original.id(), day(2), "09:00", patient));
    assertStatus(403, () -> request(original.id(), day(1), "09:00", newUser(Role.USER)));

    RescheduleItem pending = request(original.id(), day(1), "09:00", patient);
    assertEquals("PENDING", pending.status());
    assertEquals(LocalTime.of(8, 0), pending.previousStart());
    assertEquals(LocalTime.of(8, 0), current().startTime(), "la cita original conserva su franja");
    assertEquals("PENDING", current().reschedule().status());
    assertEquals(List.of(LocalTime.of(8, 30), LocalTime.of(9, 30)), starts(), "la nueva franja queda retenida");
    assertStatus(409, () -> book(newUser(Role.USER), pro, location("HIC"), general, day(1), "09:00"));
    assertStatus(409, () -> request(original.id(), day(1), "09:30", patient));

    long cardio = specialty(30, true);
    Pro specialist = professional(admin, List.of(cardio), List.of(location("HIC")));
    block(specialist, location("HIC"), day(1), "08:00", "09:00");
    AppointmentItem requested = book(patient, specialist, location("HIC"), cardio, day(1), "08:00");
    assertStatus(409, () -> request(requested.id(), day(1), "08:30", patient));
  }

  @Test
  void hu022ApprovalMovesAppointmentAndIsAudited() {
    RescheduleItem pending = request(original.id(), day(1), "09:00", patient);
    var adminAuth = auth(admin, Role.ADMIN);
    assertEquals(List.of(pending.id()), reschedules.pending(location("HIC"), pro.id(), general, day(1), adminAuth).stream()
        .map(RescheduleItem::id).toList());
    assertTrue(reschedules.pending(location("ICV"), null, null, null, adminAuth).stream().noneMatch(r -> r.id().equals(pending.id())));
    assertStatus(403, () -> reschedules.pending(null, null, null, null, auth(patient, Role.USER)));

    RescheduleItem approved = reschedules.decide(pending.id(), new RescheduleDecisionRequest("APPROVE", null), adminAuth);
    assertEquals("APPROVED", approved.status());
    assertEquals(LocalTime.of(9, 0), current().startTime());
    assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 30)), starts(), "libera la franja antigua y asigna la nueva");
    HistoryItem audit = appointments.history(original.id(), adminAuth).getLast();
    assertEquals("ADMIN", audit.source());
    assertTrue(audit.reason().startsWith("Reprogramada: 16/01/2030 08:00"), audit.reason());
    assertStatus(409, () -> reschedules.decide(pending.id(), new RescheduleDecisionRequest("REJECT", "tarde"), adminAuth));
  }

  @Test
  void hu022RejectionNeedsReasonReleasesHoldAndPatientDecides() {
    RescheduleItem pending = request(original.id(), day(1), "09:00", patient);
    var adminAuth = auth(admin, Role.ADMIN);
    assertStatus(400, () -> reschedules.decide(pending.id(), new RescheduleDecisionRequest("REJECT", null), adminAuth));
    RescheduleItem rejected = reschedules.decide(pending.id(), new RescheduleDecisionRequest("REJECT", "Agenda completa"), adminAuth);
    assertEquals("REJECTED", rejected.status());
    assertEquals(LocalTime.of(8, 0), current().startTime(), "conserva la cita original");
    assertEquals("Agenda completa", current().reschedule().decisionReason());
    assertTrue(starts().contains(LocalTime.of(9, 0)), "libera solo la retención");

    long otherPatient = newUser(Role.USER);
    AppointmentItem other = book(otherPatient, pro, location("HIC"), general, day(1), "08:30");
    assertEquals("PENDING", request(other.id(), day(1), "09:00", otherPatient).status(), "el slot liberado puede retenerse de nuevo");

    assertStatus(403, () -> reschedules.keep(original.id(), pending.id(), auth(otherPatient, Role.USER)));
    assertEquals("KEEP_APPOINTMENT", reschedules.keep(original.id(), pending.id(), auth(patient, Role.USER)).patientAction());
    assertStatus(409, () -> reschedules.keep(original.id(), pending.id(), auth(patient, Role.USER)));
  }

  @Test
  void hu020CancellingWithPendingRescheduleReleasesEverything() {
    RescheduleItem pending = request(original.id(), day(1), "09:00", patient);
    appointments.cancel(original.id(), auth(patient, Role.USER));
    assertEquals("CANCELLED", reader(pending.id()));
    assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30)), starts());
    assertStatus(409, () -> reschedules.decide(pending.id(), new RescheduleDecisionRequest("APPROVE", null), auth(admin, Role.ADMIN)));
  }

  @Test
  void hu020CancellingAfterRejectionIsRecordedAsPatientAction() {
    RescheduleItem pending = request(original.id(), day(1), "09:00", patient);
    reschedules.decide(pending.id(), new RescheduleDecisionRequest("REJECT", "Sin cupo"), auth(admin, Role.ADMIN));
    appointments.cancel(original.id(), auth(patient, Role.USER));
    assertEquals("CANCEL_APPOINTMENT", jdbc.queryForObject("select patient_action_after_rejection from reschedule_requests where id = ?",
        String.class, pending.id()));
  }

  @Test
  void hu022LegacyRequestWithoutHoldTakesSlotsAtDecisionTime() {
    long legacy = legacyPendingRequest(original.id(), day(1).atTime(9, 30));
    reschedules.decide(legacy, new RescheduleDecisionRequest("APPROVE", null), auth(admin, Role.ADMIN));
    assertEquals(LocalTime.of(9, 30), current().startTime());
    assertTrue(!starts().contains(LocalTime.of(9, 30)) && starts().contains(LocalTime.of(8, 0)));

    AppointmentItem other = book(newUser(Role.USER), pro, location("HIC"), general, day(1), "09:00");
    long blocked = legacyPendingRequest(other.id(), day(1).atTime(9, 30));
    assertStatus(409, () -> reschedules.decide(blocked, new RescheduleDecisionRequest("APPROVE", null), auth(admin, Role.ADMIN)));
  }

  /** Simula una solicitud PENDING creada antes de V4 (sin filas en reschedule_request_slots). */
  private long legacyPendingRequest(long appointmentId, java.time.LocalDateTime requested) {
    var row = jdbc.queryForMap("select patient_user_id, location_id, scheduled_start_at, scheduled_end_at from appointments where id = ?", appointmentId);
    jdbc.update("""
        insert into reschedule_requests(appointment_id, requested_by_user_id, requested_location_id, status_id, previous_start_at, previous_end_at,
                                        requested_start_at, requested_end_at)
        values (?, ?, ?, (select id from reschedule_request_statuses where code = 'PENDING'), ?, ?, ?, ?)""",
        appointmentId, row.get("patient_user_id"), row.get("location_id"), row.get("scheduled_start_at"), row.get("scheduled_end_at"),
        requested, requested.plusMinutes(30));
    return jdbc.queryForObject("select max(id) from reschedule_requests where appointment_id = ?", Long.class, appointmentId);
  }

  private String reader(long requestId) {
    return jdbc.queryForObject("select rs.code from reschedule_requests r join reschedule_request_statuses rs on rs.id = r.status_id where r.id = ?",
        String.class, requestId);
  }
}
