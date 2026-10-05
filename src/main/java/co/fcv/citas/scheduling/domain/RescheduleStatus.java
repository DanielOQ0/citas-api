package co.fcv.citas.scheduling.domain;

/** Estados de una solicitud de reprogramación: solo PENDING admite transición (RF-15). */
public enum RescheduleStatus {
  PENDING, APPROVED, REJECTED, CANCELLED;

  public boolean isTerminal() {
    return this != PENDING;
  }

  public boolean canTransitionTo(RescheduleStatus target) {
    return this == PENDING && target != PENDING;
  }
}
