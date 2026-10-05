package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

/** HU-002, HU-004 y HU-005 con el token de recuperación expuesto solo para desarrollo (EXPOSE_RECOVERY_TOKEN). */
@TestPropertySource(properties = "app.password-recovery.expose-token=true")
class AuthFlowIntegrationTest extends IntegrationTestBase {

  private ResultActions postJson(String path, String json) throws Exception {
    return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json));
  }

  private String register(long n, String documentType, String document, String phone) {
    return "{\"firstName\":\"Ana\",\"lastName\":\"Prueba\",\"documentType\":\"" + documentType + "\",\"documentNumber\":\"" + document
        + "\",\"email\":\"ana" + n + "@demo.invalid\",\"phone\":\"" + phone + "\",\"password\":\"password123\"}";
  }

  @Test
  void hu002RegistrationValidatesFormatsAndUniqueness() throws Exception {
    long n = unique();
    postJson("/api/auth/register", register(n, "XX", "AB" + n, "3001234567"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors[0].field").value("documentType"));
    postJson("/api/auth/register", register(n, "CC", "1-2", "3001234567"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors[0].field").value("documentNumber"));
    postJson("/api/auth/register", register(n, "CC", "AB" + n, "300-abc"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors[0].field").value("phone"));
    postJson("/api/auth/register", register(n, "CE", "AB" + n, "3001234567"))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.user.roles[0]").value("USER"));
    postJson("/api/auth/register", register(unique(), "CC", "AB" + n, "3001234567"))
        .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Email o documento ya registrado"));
    String hash = jdbc.queryForObject("select password_hash from users where email = ?", String.class, "ana" + n + "@demo.invalid");
    assertFalse(hash.contains("password123"), "la contraseña se almacena con hash BCrypt");
  }

  @Test
  void hu004RefreshRotatesAndLogoutRevokes() throws Exception {
    long n = unique();
    String body = postJson("/api/auth/register", register(n, "CC", "RF" + n, "3001234567")).andReturn().getResponse().getContentAsString();
    String refresh = JsonPath.read(body, "$.refreshToken");
    String rotated = JsonPath.read(postJson("/api/auth/refresh", "{\"refreshToken\":\"" + refresh + "\"}")
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.refreshToken");
    assertNotEquals(refresh, rotated);
    postJson("/api/auth/refresh", "{\"refreshToken\":\"" + refresh + "\"}").andExpect(status().isUnauthorized());
    postJson("/api/auth/logout", "{\"refreshToken\":\"" + rotated + "\"}").andExpect(status().isNoContent());
    postJson("/api/auth/refresh", "{\"refreshToken\":\"" + rotated + "\"}").andExpect(status().isUnauthorized());
    postJson("/api/auth/refresh", "{\"refreshToken\":\"no-es-un-jwt\"}").andExpect(status().isUnauthorized()).andExpect(jsonPath("$.accessToken").doesNotExist());
  }

  @Test
  void hu005RecoveryTokenIsTemporaryAndSingleUse() throws Exception {
    long n = unique();
    postJson("/api/auth/register", register(n, "CC", "RC" + n, "3001234567")).andExpect(status().isCreated());
    // Cuenta inexistente: misma respuesta que una real (sin enumeración), pero el token no sirve para nada.
    String decoy = JsonPath.read(postJson("/api/auth/password-recovery", "{\"email\":\"nadie" + n + "@demo.invalid\"}")
        .andExpect(status().isOk()).andExpect(jsonPath("$.accepted").value(true)).andReturn().getResponse().getContentAsString(), "$.developmentToken");
    postJson("/api/auth/password-reset", "{\"token\":\"" + decoy + "\",\"password\":\"NuevaClave2030\"}").andExpect(status().isBadRequest());
    String token = JsonPath.read(postJson("/api/auth/password-recovery", "{\"email\":\"ana" + n + "@demo.invalid\"}")
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.developmentToken");
    postJson("/api/auth/password-reset", "{\"token\":\"" + token + "\",\"password\":\"corta\"}").andExpect(status().isBadRequest());
    postJson("/api/auth/password-reset", "{\"token\":\"" + token + "\",\"password\":\"NuevaClave2030\"}").andExpect(status().isNoContent());
    postJson("/api/auth/password-reset", "{\"token\":\"" + token + "\",\"password\":\"OtraClave2030\"}").andExpect(status().isBadRequest());
    postJson("/api/auth/login", "{\"email\":\"ana" + n + "@demo.invalid\",\"password\":\"password123\"}").andExpect(status().isUnauthorized());
    postJson("/api/auth/login", "{\"email\":\"ana" + n + "@demo.invalid\",\"password\":\"NuevaClave2030\"}").andExpect(status().isOk());

    String expiring = JsonPath.read(postJson("/api/auth/password-recovery", "{\"email\":\"ana" + n + "@demo.invalid\"}")
        .andReturn().getResponse().getContentAsString(), "$.developmentToken");
    clock.advance(java.time.Duration.ofMinutes(31));
    postJson("/api/auth/password-reset", "{\"token\":\"" + expiring + "\",\"password\":\"Vencida2030\"}").andExpect(status().isBadRequest());
  }
}
