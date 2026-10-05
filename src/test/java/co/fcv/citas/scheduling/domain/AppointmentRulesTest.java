package co.fcv.citas.scheduling.domain;

import static co.fcv.citas.scheduling.domain.AppointmentStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class AppointmentRulesTest {
  private static final LocalDateTime NOW = LocalDateTime.of(2030, 1, 15, 8, 0);
  private static final LocalDateTime FUTURE = NOW.plusDays(1);
  private static final LocalDateTime PAST = NOW.minusHours(2);

  private static DomainException.Kind kindOf(Executable call) {
    return assertThrows(DomainException.class, call).kind();
  }

  @Test
  void statusTransitionsAreExplicit() {
    assertTrue(REQUESTED.canTransitionTo(APPROVED));
    assertTrue(REQUESTED.canTransitionTo(REJECTED));
    assertTrue(APPROVED.canTransitionTo(COMPLETED));
    assertFalse(APPROVED.canTransitionTo(REJECTED));
    assertFalse(CANCELLED.canTransitionTo(APPROVED), "una cita cancelada no se reactiva");
    assertTrue(REJECTED.isTerminal() && CANCELLED.isTerminal() && COMPLETED.isTerminal() && NO_SHOW.isTerminal());
    assertEquals(DomainException.Kind.INVALID, kindOf(() -> AppointmentStatus.fromCode("PENDIENTE")));
  }

  @Test
  void onlyRequestedIsDecidedAndRejectionNeedsReason() {
    assertEquals(APPROVED, AppointmentRules.decide(REQUESTED, true, null, FUTURE, NOW));
    assertEquals(REJECTED, AppointmentRules.decide(REQUESTED, false, "Agenda completa", FUTURE, NOW));
    assertEquals(DomainException.Kind.INVALID, kindOf(() -> AppointmentRules.decide(REQUESTED, false, " ", FUTURE, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.decide(APPROVED, false, "x", FUTURE, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.decide(REQUESTED, true, null, PAST, NOW)));
  }

  @Test
  void cancellationNeedsFutureNonTerminalAppointment() {
    AppointmentRules.requireCancellable(APPROVED, FUTURE, NOW);
    AppointmentRules.requireCancellable(REQUESTED, FUTURE, NOW);
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.requireCancellable(APPROVED, PAST, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.requireCancellable(CANCELLED, FUTURE, NOW)));
  }

  @Test
  void closingNeedsApprovedAppointmentThatAlreadyEnded() {
    assertEquals(COMPLETED, AppointmentRules.close(APPROVED, COMPLETED, PAST, NOW));
    assertEquals(NO_SHOW, AppointmentRules.close(APPROVED, NO_SHOW, PAST, NOW));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.close(APPROVED, COMPLETED, FUTURE, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.close(REQUESTED, COMPLETED, PAST, NOW)));
    assertEquals(DomainException.Kind.INVALID, kindOf(() -> AppointmentRules.close(APPROVED, CANCELLED, PAST, NOW)));
  }

  @Test
  void rescheduleRequestRules() {
    AppointmentRules.requireReschedulable(APPROVED, FUTURE, FUTURE.plusDays(1), false, NOW);
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.requireReschedulable(REQUESTED, FUTURE, FUTURE.plusDays(1), false, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.requireReschedulable(APPROVED, PAST, FUTURE, false, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.requireReschedulable(APPROVED, FUTURE, FUTURE.plusDays(1), true, NOW)));
    assertEquals(DomainException.Kind.INVALID, kindOf(() -> AppointmentRules.requireReschedulable(APPROVED, FUTURE, PAST, false, NOW)));
    assertEquals(DomainException.Kind.INVALID, kindOf(() -> AppointmentRules.requireReschedulable(APPROVED, FUTURE, FUTURE, false, NOW)));
  }

  @Test
  void rescheduleDecisionRules() {
    assertEquals(RescheduleStatus.APPROVED, AppointmentRules.decideReschedule(RescheduleStatus.PENDING, true, null, APPROVED, FUTURE, NOW));
    assertEquals(RescheduleStatus.REJECTED, AppointmentRules.decideReschedule(RescheduleStatus.PENDING, false, "Sin cupo", APPROVED, FUTURE, NOW));
    assertEquals(DomainException.Kind.INVALID, kindOf(() -> AppointmentRules.decideReschedule(RescheduleStatus.PENDING, false, null, APPROVED, FUTURE, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.decideReschedule(RescheduleStatus.REJECTED, true, null, APPROVED, FUTURE, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.decideReschedule(RescheduleStatus.PENDING, true, null, CANCELLED, FUTURE, NOW)));
    assertEquals(DomainException.Kind.CONFLICT, kindOf(() -> AppointmentRules.decideReschedule(RescheduleStatus.PENDING, true, null, APPROVED, PAST, NOW)));
    assertTrue(RescheduleStatus.PENDING.canTransitionTo(RescheduleStatus.CANCELLED));
    assertFalse(RescheduleStatus.APPROVED.canTransitionTo(RescheduleStatus.REJECTED));
  }
}
