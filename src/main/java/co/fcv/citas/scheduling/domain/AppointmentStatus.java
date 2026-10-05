package co.fcv.citas.scheduling.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Estados de cita (catálogo fijo RF-05) y sus transiciones explícitas (RN-11). */
public enum AppointmentStatus {
  REQUESTED, APPROVED, REJECTED, CANCELLED, COMPLETED, NO_SHOW;

  private static final Map<AppointmentStatus, Set<AppointmentStatus>> NEXT = Map.of(
      REQUESTED, EnumSet.of(APPROVED, REJECTED, CANCELLED),
      APPROVED, EnumSet.of(CANCELLED, COMPLETED, NO_SHOW));

  public boolean isTerminal() {
    return !NEXT.containsKey(this);
  }

  public boolean canTransitionTo(AppointmentStatus target) {
    return NEXT.getOrDefault(this, Set.of()).contains(target);
  }

  /** Convierte un código recibido del cliente; un código desconocido es una entrada inválida. */
  public static AppointmentStatus fromCode(String code) {
    try {
      return valueOf(code);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw DomainException.invalid("Estado de cita desconocido: " + code);
    }
  }
}
