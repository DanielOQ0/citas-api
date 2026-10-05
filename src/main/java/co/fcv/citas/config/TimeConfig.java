package co.fcv.citas.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Zona de negocio del contrato (America/Bogota): toda regla temporal usa este reloj, no la zona del contenedor. */
@Configuration
public class TimeConfig {
  public static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Bogota");

  @Bean
  Clock clock() {
    return Clock.system(BUSINESS_ZONE);
  }
}
