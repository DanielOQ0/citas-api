package co.fcv.citas.scheduling.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/** Política de slots de 30 minutos (RF-08, RF-09, RN-05, RN-06). */
public final class SlotPolicy {
  public static final int SLOT_MINUTES = 30;

  private SlotPolicy() {}

  /** HU-012: una especialidad dura 30 o 60 minutos. */
  public static void requireValidDuration(int durationMinutes) {
    if (durationMinutes != 30 && durationMinutes != 60) {
      throw DomainException.invalid("La duración debe ser 30 o 60 minutos");
    }
  }

  /** 30 min = 1 slot; 60 min = 2 slots consecutivos. */
  public static int requiredSlots(int durationMinutes) {
    requireValidDuration(durationMinutes);
    return durationMinutes / SLOT_MINUTES;
  }

  /** HU-013: bloque futuro, alineado a 30 minutos; devuelve el inicio de cada slot. */
  public static List<LocalDateTime> blockSlots(LocalDate date, LocalTime start, LocalTime end, LocalDateTime now) {
    if (!start.isBefore(end)) {
      throw DomainException.invalid("La hora de inicio debe ser anterior a la hora de fin");
    }
    if (!aligned(start) || !aligned(end)) {
      throw DomainException.invalid("Los límites del bloque deben ser múltiplos de 30 minutos");
    }
    if (!LocalDateTime.of(date, start).isAfter(now)) {
      throw DomainException.invalid("El bloque debe iniciar en el futuro");
    }
    List<LocalDateTime> slots = new ArrayList<>();
    for (LocalTime t = start; t.isBefore(end); t = t.plusMinutes(SLOT_MINUTES)) {
      slots.add(LocalDateTime.of(date, t));
    }
    return slots;
  }

  /** HU-015: inicios futuros cuyos slots consecutivos requeridos están todos libres. */
  public static List<LocalDateTime> bookableStarts(Collection<LocalDateTime> freeSlotStarts, int durationMinutes, LocalDateTime now) {
    int needed = requiredSlots(durationMinutes);
    Set<LocalDateTime> free = new HashSet<>(freeSlotStarts);
    return freeSlotStarts.stream()
        .sorted()
        .filter(start -> start.isAfter(now))
        .filter(start -> IntStream.range(1, needed).allMatch(i -> free.contains(start.plusMinutes((long) SLOT_MINUTES * i))))
        .toList();
  }

  /** HU-016/017/021: no hay cita ni retención si falta algún slot de la duración completa. */
  public static void requireFullCoverage(int availableSlots, int durationMinutes) {
    if (availableSlots != requiredSlots(durationMinutes)) {
      throw DomainException.conflict("La franja ya no está disponible");
    }
  }

  public static void requireFutureStart(LocalDateTime start, LocalDateTime now) {
    if (!start.isAfter(now)) {
      throw DomainException.invalid("La cita debe ser futura");
    }
  }

  private static boolean aligned(LocalTime time) {
    return time.getMinute() % SLOT_MINUTES == 0 && time.getSecond() == 0 && time.getNano() == 0;
  }
}
