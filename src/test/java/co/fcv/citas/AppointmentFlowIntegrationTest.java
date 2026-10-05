package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** HU-015 a HU-020 y HU-023 a HU-025. */
class AppointmentFlowIntegrationTest extends IntegrationTestBase {

  private List<LocalTime> starts(long specialtyId, Pro pro, int plusDays) {
    return availability.availability(location("HIC"), specialtyId, pro.id(), day(plusDays)).stream().map(AvailabilityItem::startTime).toList();
  }

  @Test
  void hu015SixtyMinuteOptionsNeedTwoConsecutiveFreeSlots() {
    long sixty = specialty(60, true);
    Pro pro = professional(newUser(Role.ADMIN), List.of(sixty), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "09:00", "10:30");
    assertEquals(List.of(LocalTime.of(9, 0), LocalTime.of(9, 30)), starts(sixty, pro, 1));
    book(newUser(Role.USER), pro, location("HIC"), sixty, day(1), "09:30");
    assertTrue(starts(sixty, pro, 1).isEmpty(), "09:00 ya no tiene su segundo slot libre");
    assertEquals(2, jdbc.queryForObject("select count(*) from professional_slots where appointment_id is not null and availability_block_id in "
        + "(select id from availability_blocks where professional_id = ?)", Integer.class, pro.id()), "60 min ocupa dos slots");
  }

  @Test
  void hu015SearchFiltersAndExcludesPastAndIneligibleOptions() {
    long general = generalSpecialty();
    long other = specialty(30, true);
    Pro pro = professional(newUser(Role.ADMIN), List.of(general), List.of(location("HIC")));
    block(pro, location("HIC"), day(0), "08:30", "10:00");
    assertEquals(3, starts(general, pro, 0).size());
    clock.advance(Duration.ofMinutes(45));
    assertEquals(List.of(LocalTime.of(9, 0), LocalTime.of(9, 30)), starts(general, pro, 0), "08:30 ya pasó");
    assertTrue(starts(other, pro, 0).isEmpty(), "especialidad no asociada");
    assertTrue(availability.availability(location("ICV"), general, pro.id(), day(0)).isEmpty(), "otra sede");
    AvailabilityItem option = availability.availability(location("HIC"), general, null, day(0)).stream()
        .filter(a -> a.professionalId().equals(pro.id())).findFirst().orElseThrow();
    assertTrue(option.professionalName().startsWith("Pro"));
  }

  @Test
  void hu016GeneralAppointmentIsApprovedBySystemAndCannotBeDoubleBooked() {
    long general = generalSpecialty();
    Pro pro = professional(newUser(Role.ADMIN), List.of(general), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "08:00", "09:00");
    long patient = newUser(Role.USER);
    AppointmentItem booked = book(patient, pro, location("HIC"), general, day(1), "08:00");
    assertEquals("APPROVED", booked.status());
    assertEquals("Medicina General", booked.specialtyName());
    assertEquals(30, booked.durationMinutes());
    List<HistoryItem> history = appointments.history(booked.id(), auth(patient, Role.USER));
    assertEquals("SYSTEM", history.getFirst().source());
    assertNull(history.getFirst().actorId());
    assertStatus(409, () -> book(newUser(Role.USER), pro, location("HIC"), general, day(1), "08:00"));
    assertStatus(400, () -> book(newUser(Role.USER), pro, location("HIC"), general, day(-1), "08:00"));
  }

  @Test
  void hu017And018SpecializedRequestHoldsSlotsUntilAdminDecides() {
    long cardio = specialty(30, true);
    long admin = newUser(Role.ADMIN);
    Pro pro = professional(admin, List.of(cardio), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "08:00", "09:00");
    long patient = newUser(Role.USER);
    AppointmentItem requested = book(patient, pro, location("HIC"), cardio, day(1), "08:00");
    assertEquals("REQUESTED", requested.status());
    assertEquals(List.of(LocalTime.of(8, 30)), starts(cardio, pro, 1), "la solicitud retiene su franja");
    assertStatus(409, () -> book(newUser(Role.USER), pro, location("HIC"), cardio, day(1), "08:00"));
    assertStatus(400, () -> book(patient, pro, location("ICV"), cardio, day(1), "08:30"));

    var adminAuth = auth(admin, Role.ADMIN);
    assertEquals(List.of(requested.id()), appointments.requested(location("HIC"), pro.id(), cardio, day(1), adminAuth).stream()
        .map(AppointmentItem::id).toList());
    assertTrue(appointments.requested(location("ICV"), null, null, null, adminAuth).stream().noneMatch(a -> a.id().equals(requested.id())));
    assertStatus(400, () -> appointments.decide(requested.id(), new DecisionRequest("REJECT", " "), adminAuth));
    AppointmentItem rejected = appointments.decide(requested.id(), new DecisionRequest("REJECT", "Sin agenda disponible"), adminAuth);
    assertEquals("REJECTED", rejected.status());
    assertEquals("Sin agenda disponible", rejected.rejectionReason());
    assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30)), starts(cardio, pro, 1), "rechazo libera los slots");
    assertStatus(409, () -> appointments.decide(requested.id(), new DecisionRequest("APPROVE", null), adminAuth));

    AppointmentItem second = book(patient, pro, location("HIC"), cardio, day(1), "08:30");
    assertEquals("APPROVED", appointments.decide(second.id(), new DecisionRequest("APPROVE", null), adminAuth).status());
    assertEquals(List.of(LocalTime.of(8, 0)), starts(cardio, pro, 1), "aprobación conserva los slots");
    assertStatus(403, () -> appointments.decide(second.id(), new DecisionRequest("APPROVE", null), auth(patient, Role.USER)));

    AppointmentItem late = book(patient, pro, location("HIC"), cardio, day(1), "08:00");
    clock.advance(Duration.ofDays(2));
    assertStatus(409, () -> appointments.decide(late.id(), new DecisionRequest("APPROVE", null), adminAuth));
    assertEquals("REJECTED", appointments.decide(late.id(), new DecisionRequest("REJECT", "Vencida sin decisión"), adminAuth).status());
  }

  @Test
  void hu019And025MyAppointmentsHistoryAndPrivacy() {
    long general = generalSpecialty();
    long admin = newUser(Role.ADMIN);
    Pro pro = professional(admin, List.of(general), List.of(location("HIC")));
    Pro otherPro = professional(admin, List.of(general), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "08:00", "10:00");
    long patient = newUser(Role.USER);
    AppointmentItem first = book(patient, pro, location("HIC"), general, day(1), "08:00");
    AppointmentItem second = book(patient, pro, location("HIC"), general, day(1), "09:00");
    appointments.cancel(second.id(), auth(patient, Role.USER));

    var me = auth(patient, Role.USER);
    assertEquals(2, appointments.mine(null, null, null, me).size());
    assertEquals(List.of(first.id()), appointments.mine("APPROVED", null, null, me).stream().map(AppointmentItem::id).toList());
    assertEquals(List.of(second.id()), appointments.mine("CANCELLED", day(1), day(1), me).stream().map(AppointmentItem::id).toList());
    assertTrue(appointments.mine(null, day(2), null, me).isEmpty());
    assertStatus(400, () -> appointments.mine("PENDIENTE", null, null, me));
    assertTrue(appointments.mine(null, null, null, auth(newUser(Role.USER), Role.USER)).isEmpty(), "solo citas propias");

    AppointmentItem shown = appointments.mine("APPROVED", null, null, me).getFirst();
    assertEquals("Hospital Internacional de Colombia (HIC)", shown.locationName());
    assertTrue(shown.professionalName().startsWith("Pro"));
    assertEquals(LocalTime.of(8, 30), shown.endTime());

    List<HistoryItem> history = appointments.history(second.id(), me);
    assertEquals(List.of("APPROVED", "CANCELLED"), history.stream().map(HistoryItem::status).toList());
    assertEquals("USER", history.getLast().source());
    assertEquals(patient, history.getLast().actorId());
    assertTrue(history.getLast().actorName().startsWith("Nombre"));
    appointments.history(second.id(), auth(pro.userId(), Role.PROFESSIONAL));
    appointments.history(second.id(), auth(admin, Role.ADMIN));
    assertStatus(403, () -> appointments.history(second.id(), auth(newUser(Role.USER), Role.USER)));
    assertStatus(403, () -> appointments.history(second.id(), auth(otherPro.userId(), Role.PROFESSIONAL)));
    assertStatus(404, () -> appointments.history(-1, auth(admin, Role.ADMIN)));
  }

  @Test
  void hu020CancellationReleasesSlotsAndIsRestricted() {
    long general = generalSpecialty();
    Pro pro = professional(newUser(Role.ADMIN), List.of(general), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "08:00", "09:00");
    long patient = newUser(Role.USER);
    AppointmentItem booked = book(patient, pro, location("HIC"), general, day(1), "08:00");
    assertStatus(403, () -> appointments.cancel(booked.id(), auth(newUser(Role.USER), Role.USER)));
    appointments.cancel(booked.id(), auth(patient, Role.USER));
    assertEquals("CANCELLED", appointments.mine(null, null, null, auth(patient, Role.USER)).getFirst().status());
    assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30)), starts(general, pro, 1), "cancelar libera el slot");
    assertStatus(409, () -> appointments.cancel(booked.id(), auth(patient, Role.USER)));

    AppointmentItem soon = book(patient, pro, location("HIC"), general, day(1), "08:30");
    clock.advance(Duration.ofDays(2));
    assertStatus(409, () -> appointments.cancel(soon.id(), auth(patient, Role.USER)));
  }

  @Test
  void hu023And024ProfessionalAgendaAndClosing() {
    long general = generalSpecialty();
    long cardio = specialty(30, true);
    long admin = newUser(Role.ADMIN);
    Pro pro = professional(admin, List.of(general, cardio), List.of(location("HIC"), location("ICV")));
    Pro otherPro = professional(admin, List.of(general), List.of(location("HIC")));
    block(pro, location("HIC"), day(1), "08:00", "10:00");
    block(pro, location("ICV"), day(8), "08:00", "09:00");
    long patient = newUser(Role.USER);
    AppointmentItem approved = book(patient, pro, location("HIC"), general, day(1), "08:00");
    book(patient, pro, location("HIC"), cardio, day(1), "09:00");
    AppointmentItem nextWeek = book(patient, pro, location("ICV"), general, day(8), "08:00");

    var actor = auth(pro.userId(), Role.PROFESSIONAL);
    assertEquals(List.of(approved.id(), nextWeek.id()), appointments.agenda(null, null, null, actor).stream().map(AppointmentItem::id).toList(),
        "solo APPROVED (la REQUESTED no figura)");
    assertEquals(List.of(approved.id()), appointments.agenda(day(1), day(1), null, actor).stream().map(AppointmentItem::id).toList());
    assertEquals(List.of(nextWeek.id()), appointments.agenda(day(1), day(8), location("ICV"), actor).stream().map(AppointmentItem::id).toList());
    assertTrue(appointments.agenda(null, null, null, auth(otherPro.userId(), Role.PROFESSIONAL)).isEmpty());
    assertTrue(appointments.agenda(null, null, null, actor).getFirst().patientName().startsWith("Nombre"));

    assertStatus(409, () -> appointments.close(approved.id(), new CloseAppointmentRequest("COMPLETED", null), actor));
    clock.set(day(1).atTime(8, 45));
    assertStatus(403, () -> appointments.close(approved.id(), new CloseAppointmentRequest("COMPLETED", null), auth(otherPro.userId(), Role.PROFESSIONAL)));
    assertEquals("COMPLETED", appointments.close(approved.id(), new CloseAppointmentRequest("COMPLETED", "Atendida"), actor).status());
    assertStatus(409, () -> appointments.close(approved.id(), new CloseAppointmentRequest("NO_SHOW", null), actor));
    HistoryItem closing = appointments.history(approved.id(), actor).getLast();
    assertEquals("COMPLETED", closing.status());
    assertEquals("USER", closing.source());
    assertEquals(pro.userId(), closing.actorId());
    assertTrue(appointments.agenda(day(1), day(1), null, actor).isEmpty(), "una cita cerrada sale de la agenda APPROVED");

    clock.set(day(8).atTime(9, 0));
    assertEquals("NO_SHOW", appointments.close(nextWeek.id(), new CloseAppointmentRequest("NO_SHOW", null), actor).status());
  }
}
