package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** HU-006 y HU-007. */
class ProfileIntegrationTest extends IntegrationTestBase {

  @Test
  void hu006ProfileIsOwnAndOnlyPhoneIsEditable() throws Exception {
    long patient = newUser(Role.USER);
    String token = bearer(patient);
    mvc.perform(get("/api/v1/users/me").header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(patient))
        .andExpect(jsonPath("$.documentType").value("CC"));
    mvc.perform(patch("/api/v1/users/me").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"phone\":\"3109876543\",\"email\":\"otro@test.co\",\"firstName\":\"Cambiado\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.phone").value("3109876543"));
    ProfileItem after = profiles.profile(auth(patient, Role.USER));
    assertEquals("3109876543", after.phone());
    assertFalse(after.email().startsWith("otro"), "el email no se edita por este flujo");
    assertFalse(after.firstName().equals("Cambiado"), "el nombre no se edita por este flujo");
    mvc.perform(patch("/api/v1/users/me").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content("{\"phone\":\"30-abc\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors[0].field").value("phone"));
    mvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void hu007AffiliationUsesCatalogReferencesAndValidatesEpsPlan() {
    var admin = auth(newUser(Role.ADMIN), Role.ADMIN);
    long regime = jdbc.queryForObject("select id from insurance_regimes where code = 'CONTRIBUTIVO'", Long.class);
    long n = unique();
    EpsItem epsA = catalogs.createEps(new EpsRequest("A" + n, "EPS A " + n, true), admin);
    EpsItem epsB = catalogs.createEps(new EpsRequest("B" + n, "EPS B " + n, true), admin);
    PlanItem planA = catalogs.createPlan(new PlanRequest(epsA.id(), regime, "PA", "Plan A", true), admin);
    PlanItem planB = catalogs.createPlan(new PlanRequest(epsB.id(), regime, "PB", "Plan B", true), admin);
    PlanItem inactive = catalogs.createPlan(new PlanRequest(epsA.id(), regime, "PX", "Plan inactivo", false), admin);

    var me = auth(newUser(Role.USER), Role.USER);
    assertStatus(400, () -> profiles.saveAffiliation(new AffiliationRequest(epsA.id(), planB.id()), me));
    assertStatus(400, () -> profiles.saveAffiliation(new AffiliationRequest(epsA.id(), inactive.id()), me));
    AffiliationItem saved = profiles.saveAffiliation(new AffiliationRequest(epsA.id(), planA.id()), me);
    assertEquals(epsA.id(), saved.epsId());
    assertEquals("Contributivo", saved.regimeName());
    profiles.saveAffiliation(new AffiliationRequest(epsB.id(), planB.id()), me);
    assertEquals(planA.id(), profiles.saveAffiliation(new AffiliationRequest(epsA.id(), planA.id()), me).planId(), "volver a un plan anterior");
    long userId = Long.parseLong(me.getName());
    assertEquals(1, jdbc.queryForObject("select count(*) from user_insurance_affiliations where user_id = ? and is_current = true", Integer.class, userId));
    assertEquals(0, jdbc.queryForObject("select count(*) from information_schema.columns where lower(table_name) = 'users' "
        + "and lower(column_name) in ('eps_name', 'plan_name', 'regime_name')", Integer.class), "3FN: sin nombres de catálogo en users");
  }
}
