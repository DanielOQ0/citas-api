package co.fcv.citas.scheduling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SlotPolicyTest {
  private static final LocalDate DAY = LocalDate.of(2030, 1, 15);
  private static final LocalDateTime NOW = DAY.atTime(8, 0);

  private static LocalDateTime at(int hour, int minute) {
    return DAY.atTime(hour, minute);
  }

  @Test
  void durationIsThirtyOrSixtyMinutes() {
    assertEquals(1, SlotPolicy.requiredSlots(30));
    assertEquals(2, SlotPolicy.requiredSlots(60));
    assertEquals(DomainException.Kind.INVALID, assertThrows(DomainException.class, () -> SlotPolicy.requiredSlots(45)).kind());
  }

  @Test
  void blockIsDiscretizedInThirtyMinuteSlots() {
    assertEquals(List.of(at(8, 30), at(9, 0), at(9, 30)), SlotPolicy.blockSlots(DAY, LocalTime.of(8, 30), LocalTime.of(10, 0), NOW));
  }

  @Test
  void blockMustBeFutureAlignedAndOrdered() {
    assertThrows(DomainException.class, () -> SlotPolicy.blockSlots(DAY, LocalTime.of(7, 0), LocalTime.of(9, 0), NOW));
    assertThrows(DomainException.class, () -> SlotPolicy.blockSlots(DAY, LocalTime.of(9, 15), LocalTime.of(10, 0), NOW));
    assertThrows(DomainException.class, () -> SlotPolicy.blockSlots(DAY, LocalTime.of(11, 0), LocalTime.of(10, 0), NOW));
  }

  @Test
  void sixtyMinuteStartsNeedTheNextSlotFree() {
    List<LocalDateTime> free = List.of(at(9, 0), at(9, 30), at(10, 0), at(11, 0));
    assertEquals(List.of(at(9, 0), at(9, 30)), SlotPolicy.bookableStarts(free, 60, NOW));
    assertEquals(free, SlotPolicy.bookableStarts(free, 30, NOW));
  }

  @Test
  void pastSlotsAreNeverOffered() {
    assertEquals(List.of(at(9, 0)), SlotPolicy.bookableStarts(List.of(at(7, 30), at(9, 0)), 30, NOW));
  }

  @Test
  void bookingNeedsEveryRequiredSlot() {
    SlotPolicy.requireFullCoverage(2, 60);
    assertEquals(DomainException.Kind.CONFLICT, assertThrows(DomainException.class, () -> SlotPolicy.requireFullCoverage(1, 60)).kind());
    assertThrows(DomainException.class, () -> SlotPolicy.requireFutureStart(NOW, NOW));
  }
}
