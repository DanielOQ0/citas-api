package co.fcv.citas;

import co.fcv.citas.config.TimeConfig;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Reloj de pruebas en la zona de negocio que puede moverse para simular el paso del tiempo. */
public class MutableClock extends Clock {
  private Instant instant;

  public MutableClock(LocalDateTime start) {
    set(start);
  }

  public void set(LocalDateTime time) {
    instant = time.atZone(TimeConfig.BUSINESS_ZONE).toInstant();
  }

  public void advance(Duration duration) {
    instant = instant.plus(duration);
  }

  @Override
  public ZoneId getZone() {
    return TimeConfig.BUSINESS_ZONE;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return Clock.fixed(instant, zone);
  }

  @Override
  public Instant instant() {
    return instant;
  }
}
