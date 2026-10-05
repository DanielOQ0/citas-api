package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.fcv.citas.auth.JwtService;
import co.fcv.citas.auth.Role;
import co.fcv.citas.auth.UserEntity;
import co.fcv.citas.auth.UserRepository;
import co.fcv.citas.scheduling.*;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import co.fcv.citas.scheduling.domain.DomainException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

/**
 * Contexto compartido de las pruebas de integración (H2 en modo MySQL, migraciones V1-V4).
 * El reloj de negocio arranca en 2030-01-15 08:00 (Bogotá) y cada prueba crea sus propios actores con sufijo único.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestBase.TestClockConfig.class)
public abstract class IntegrationTestBase {
  protected static final LocalDateTime START = LocalDateTime.of(2030, 1, 15, 8, 0);
  private static final AtomicLong SEQUENCE = new AtomicLong(System.nanoTime() % 1_000_000);

  @TestConfiguration
  static class TestClockConfig {
    @Bean
    @Primary
    MutableClock testClock() {
      return new MutableClock(START);
    }
  }

  @Autowired protected JdbcTemplate jdbc;
  @Autowired protected UserRepository users;
  @Autowired protected JwtService jwt;
  @Autowired protected MockMvc mvc;
  @Autowired protected MutableClock clock;
  @Autowired protected CatalogService catalogs;
  @Autowired protected ProfessionalService professionals;
  @Autowired protected AvailabilityService availability;
  @Autowired protected AppointmentService appointments;
  @Autowired protected RescheduleService reschedules;
  @Autowired protected ProfileService profiles;

  @BeforeEach
  void resetClockAndSeedFixedCatalogs() {
    clock.set(START);
    for (String[] s : new String[][] {{"REQUESTED", "false"}, {"APPROVED", "false"}, {"REJECTED", "true"}, {"CANCELLED", "true"},
        {"COMPLETED", "true"}, {"NO_SHOW", "true"}}) {
      jdbc.update("merge into appointment_statuses(code, name, is_terminal) key(code) values (?, ?, ?)", s[0], s[0], Boolean.valueOf(s[1]));
    }
    for (String[] s : new String[][] {{"PENDING", "false"}, {"APPROVED", "true"}, {"REJECTED", "true"}, {"CANCELLED", "true"}}) {
      jdbc.update("merge into reschedule_request_statuses(code, name) key(code) values (?, ?)", s[0], s[0]);
    }
    jdbc.update("merge into insurance_regimes(code, name) key(code) values ('CONTRIBUTIVO', 'Contributivo')");
    jdbc.update("merge into locations(code, name, address, city, department, active) key(code) values ('HIC', 'Hospital Internacional de Colombia (HIC)', 'Km 7', 'Piedecuesta', 'Santander', true)");
    jdbc.update("merge into locations(code, name, address, city, department, active) key(code) values ('ICV', 'Instituto Cardiovascular (ICV)', 'Calle 155A', 'Floridablanca', 'Santander', true)");
    jdbc.update("merge into specialties(code, name, appointment_duration_minutes, is_general, requires_admin_approval, active) key(code) values ('MEDICINA_GENERAL', 'Medicina General', 30, true, false, true)");
  }

  protected static long unique() {
    return SEQUENCE.incrementAndGet();
  }

  protected long newUser(Role role) {
    long n = unique();
    UserEntity user = users.save(new UserEntity("Nombre" + n, role.name(), "CC", "D" + n, role.name().toLowerCase() + n + "@test.co",
        "3000000000", "hash", role));
    return user.getId();
  }

  protected static Authentication auth(long userId, Role role) {
    return new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
  }

  protected String bearer(long userId) {
    return "Bearer " + jwt.access(users.findById(userId).orElseThrow());
  }

  protected long location(String code) {
    return jdbc.queryForObject("select id from locations where code = ?", Long.class, code);
  }

  protected long generalSpecialty() {
    return jdbc.queryForObject("select id from specialties where code = 'MEDICINA_GENERAL'", Long.class);
  }

  protected long specialty(int duration, boolean requiresApproval) {
    long n = unique();
    jdbc.update("insert into specialties(code, name, appointment_duration_minutes, is_general, requires_admin_approval, active) values (?, ?, ?, false, ?, true)",
        "ESP_" + n, "Especialidad " + n, duration, requiresApproval);
    return jdbc.queryForObject("select id from specialties where code = ?", Long.class, "ESP_" + n);
  }

  protected record Pro(long id, long userId) {}

  protected Pro professional(long adminId, List<Long> specialtyIds, List<Long> locationIds) {
    long n = unique();
    ProfessionalAdminItem item = professionals.create(new ProfessionalRequest("Pro" + n, "Test", "CC", "P" + n, "pro" + n + "@test.co",
        "3001234567", "password123", "PROF" + n, "LIC" + n), auth(adminId, Role.ADMIN));
    professionals.assign(item.id(), new ProfessionalAssignments(specialtyIds, specialtyIds.getFirst(), locationIds), auth(adminId, Role.ADMIN));
    return new Pro(item.id(), jdbc.queryForObject("select user_id from professionals where id = ?", Long.class, item.id()));
  }

  protected AvailabilityBlockItem block(Pro pro, long locationId, LocalDate date, String start, String end) {
    return availability.addBlock(new AvailabilityBlockRequest(locationId, date, LocalTime.parse(start), LocalTime.parse(end)),
        auth(pro.userId(), Role.PROFESSIONAL));
  }

  protected AppointmentItem book(long patientId, Pro pro, long locationId, long specialtyId, LocalDate date, String start) {
    return appointments.book(new AppointmentRequest(pro.id(), locationId, specialtyId, date, LocalTime.parse(start), null),
        auth(patientId, Role.USER));
  }

  protected LocalDate day(int plusDays) {
    return START.toLocalDate().plusDays(plusDays);
  }

  /** Código HTTP con el que el adaptador REST respondería a la excepción del caso de uso. */
  protected static void assertStatus(int expected, Executable call) {
    RuntimeException error = assertThrows(RuntimeException.class, call);
    int actual = error instanceof DomainException domain
        ? (domain.kind() == DomainException.Kind.CONFLICT ? 409 : 400)
        : error instanceof ResponseStatusException status ? status.getStatusCode().value() : -1;
    assertEquals(expected, actual, error.getMessage());
  }
}
