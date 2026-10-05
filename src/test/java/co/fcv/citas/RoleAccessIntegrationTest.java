package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import co.fcv.citas.auth.JwtService;
import co.fcv.citas.auth.Role;
import co.fcv.citas.auth.UserEntity;
import co.fcv.citas.auth.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Corre sobre el contenedor servlet real: MockMvc no reproduce el reenvío a /error,
 * que antes convertía cualquier 4xx de un endpoint autenticado en 401.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoleAccessIntegrationTest {
  @Autowired TestRestTemplate http;
  @Autowired UserRepository users;
  @Autowired JwtService jwt;

  private String tokenFor(Role role) {
    long suffix = System.nanoTime();
    UserEntity user = users.save(new UserEntity("Rol", role.name(), "CC", "R" + suffix,
        role.name().toLowerCase() + suffix + "@test.co", "3000000000", "hash", role));
    return jwt.access(user);
  }

  private ResponseEntity<String> call(HttpMethod method, String path, String token, String json) {
    HttpHeaders headers = new HttpHeaders();
    if (token != null) headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return http.exchange(path, method, new HttpEntity<>(json, headers), String.class);
  }

  @Test
  void patientEndpointsRejectAdminAndProfessional() {
    String tomorrow = LocalDate.now().plusDays(1).toString();
    String booking = "{\"professionalId\":1,\"locationId\":1,\"specialtyId\":1,\"date\":\"" + tomorrow + "\",\"startTime\":\"09:00\"}";
    String reschedule = "{\"date\":\"" + tomorrow + "\",\"startTime\":\"10:00\"}";
    for (Role role : List.of(Role.ADMIN, Role.PROFESSIONAL)) {
      String token = tokenFor(role);
      assertEquals(403, call(POST, "/api/v1/appointments", token, booking).getStatusCode().value(), role + " reserva");
      assertEquals(403, call(GET, "/api/v1/appointments", token, null).getStatusCode().value(), role + " mis citas");
      assertEquals(403, call(POST, "/api/v1/appointments/999/cancel", token, "{}").getStatusCode().value(), role + " cancela");
      assertEquals(403, call(POST, "/api/v1/appointments/999/reschedule-requests", token, reschedule).getStatusCode().value(), role + " reprograma");
      assertEquals(403, call(GET, "/api/v1/users/me/affiliation", token, null).getStatusCode().value(), role + " consulta afiliación");
      assertEquals(403, call(PUT, "/api/v1/users/me/affiliation", token, "{\"insurancePlanId\":1}").getStatusCode().value(), role + " guarda afiliación");
    }
  }

  @Test
  void userReachesOnlyPatientEndpoints() {
    String token = tokenFor(Role.USER);
    assertEquals(200, call(GET, "/api/v1/appointments", token, null).getStatusCode().value());
    assertEquals(403, call(GET, "/api/v1/admin/appointments", token, null).getStatusCode().value());
    assertEquals(403, call(GET, "/api/v1/professional/appointments", token, null).getStatusCode().value());
  }

  @Test
  void errorsKeepStatusAndUniformBody() {
    String admin = tokenFor(Role.ADMIN);
    ResponseEntity<String> missing = call(POST, "/api/v1/admin/appointments/999999/decision", admin, "{\"decision\":\"APPROVE\"}");
    assertEquals(404, missing.getStatusCode().value());
    assertTrue(missing.getBody().contains("\"message\":\"Cita no encontrada\""), missing.getBody());

    ResponseEntity<String> invalid = call(POST, "/api/v1/admin/appointments/999999/decision", admin, "{\"decision\":\"MAYBE\"}");
    assertEquals(400, invalid.getStatusCode().value());
    assertTrue(invalid.getBody().contains("\"field\":\"decision\""), invalid.getBody());

    ResponseEntity<String> anonymous = call(GET, "/api/v1/appointments", null, null);
    assertEquals(401, anonymous.getStatusCode().value());
    assertTrue(anonymous.getBody().contains("\"status\":401"), anonymous.getBody());

    assertEquals(405, call(POST, "/api/v1/catalogs/locations", admin, "{}").getStatusCode().value());
  }
}
