package co.fcv.citas.scheduling.domain;

import static co.fcv.citas.scheduling.domain.AppointmentStatus.APPROVED;
import static co.fcv.citas.scheduling.domain.AppointmentStatus.CANCELLED;
import static co.fcv.citas.scheduling.domain.AppointmentStatus.COMPLETED;
import static co.fcv.citas.scheduling.domain.AppointmentStatus.NO_SHOW;
import static co.fcv.citas.scheduling.domain.AppointmentStatus.REJECTED;
import static co.fcv.citas.scheduling.domain.AppointmentStatus.REQUESTED;

import java.time.LocalDateTime;

/** Reglas de transición de citas y reprogramaciones (RF-12 a RF-17, RN-04, RN-10, RN-11, D-14, D-19). */
public final class AppointmentRules {
  private AppointmentRules() {}

  /** HU-018: solo una solicitud REQUESTED se decide; aprobar exige horario futuro y rechazar exige motivo. */
  public static AppointmentStatus decide(AppointmentStatus current, boolean approve, String reason, LocalDateTime start, LocalDateTime now) {
    if (current != REQUESTED) {
      throw DomainException.conflict("La solicitud ya fue decidida");
    }
    if (approve) {
      if (!start.isAfter(now)) {
        throw DomainException.conflict("No se puede aprobar una solicitud cuyo horario ya pasó");
      }
      return APPROVED;
    }
    requireReason(reason, "El motivo de rechazo es obligatorio");
    return REJECTED;
  }

  /** HU-020: solo citas propias, futuras y no terminales se cancelan. */
  public static void requireCancellable(AppointmentStatus current, LocalDateTime start, LocalDateTime now) {
    if (!current.canTransitionTo(CANCELLED) || !start.isAfter(now)) {
      throw DomainException.conflict("La cita no es cancelable: debe ser futura y no estar finalizada");
    }
  }

  /** HU-024: aplicable = APPROVED cuya hora de fin ya pasó (D-19). */
  public static AppointmentStatus close(AppointmentStatus current, AppointmentStatus outcome, LocalDateTime end, LocalDateTime now) {
    if (outcome != COMPLETED && outcome != NO_SHOW) {
      throw DomainException.invalid("El cierre solo admite COMPLETED o NO_SHOW");
    }
    if (current != APPROVED) {
      throw DomainException.conflict("La cita no está aprobada o ya fue cerrada");
    }
    if (!end.isBefore(now)) {
      throw DomainException.conflict("La cita solo puede cerrarse después de finalizar");
    }
    return outcome;
  }

  /** HU-021: cita APPROVED y futura, sin otra reprogramación pendiente, hacia una franja futura distinta. */
  public static void requireReschedulable(AppointmentStatus current, LocalDateTime currentStart, LocalDateTime requestedStart,
                                          boolean hasPendingRequest, LocalDateTime now) {
    if (current != APPROVED) {
      throw DomainException.conflict("Solo se puede reprogramar una cita aprobada; cambiar de profesional es una cita nueva");
    }
    if (!currentStart.isAfter(now)) {
      throw DomainException.conflict("Solo se puede reprogramar una cita futura");
    }
    if (hasPendingRequest) {
      throw DomainException.conflict("La cita ya tiene una reprogramación pendiente");
    }
    if (!requestedStart.isAfter(now)) {
      throw DomainException.invalid("La nueva franja debe ser futura");
    }
    if (requestedStart.equals(currentStart)) {
      throw DomainException.invalid("La nueva franja debe ser distinta de la actual");
    }
  }

  /** HU-022: solo PENDING se decide; aprobar exige cita aún APPROVED y franja futura; rechazar exige motivo (D-16). */
  public static RescheduleStatus decideReschedule(RescheduleStatus current, boolean approve, String reason,
                                                  AppointmentStatus appointment, LocalDateTime requestedStart, LocalDateTime now) {
    if (current != RescheduleStatus.PENDING) {
      throw DomainException.conflict("La reprogramación ya fue decidida");
    }
    if (approve) {
      if (appointment != APPROVED) {
        throw DomainException.conflict("La cita original ya no está aprobada");
      }
      if (!requestedStart.isAfter(now)) {
        throw DomainException.conflict("No se puede aprobar una reprogramación cuyo horario ya pasó");
      }
      return RescheduleStatus.APPROVED;
    }
    requireReason(reason, "El motivo de rechazo es obligatorio");
    return RescheduleStatus.REJECTED;
  }

  public static void requireReason(String reason, String message) {
    if (reason == null || reason.isBlank()) {
      throw DomainException.invalid(message);
    }
  }
}
