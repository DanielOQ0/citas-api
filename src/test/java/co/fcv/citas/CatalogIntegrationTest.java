package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** HU-001, HU-008, HU-009, HU-012. */
class CatalogIntegrationTest extends IntegrationTestBase {

  @Test
  void hu001FixedCatalogsAreExposedReadOnly() throws Exception {
    assertEquals(List.of("USER", "PROFESSIONAL", "ADMIN"), catalogs.roles().stream().map(CatalogItem::code).toList());
    assertTrue(catalogs.locations().stream().map(CatalogItem::code).toList().containsAll(List.of("HIC", "ICV")));
    assertTrue(catalogs.appointmentStatuses().stream().map(CatalogItem::code).toList()
        .containsAll(List.of("REQUESTED", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW")));
    String admin = bearer(newUser(Role.ADMIN));
    mvc.perform(post("/api/v1/catalogs/roles").header("Authorization", admin).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isMethodNotAllowed());
    mvc.perform(put("/api/v1/catalogs/appointment-statuses").header("Authorization", admin).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isMethodNotAllowed());
    mvc.perform(delete("/api/v1/catalogs/locations").header("Authorization", admin)).andExpect(status().isMethodNotAllowed());
  }

  @Test
  void hu008EpsAndPlansCrudProtectReferencedRows() {
    var admin = auth(newUser(Role.ADMIN), Role.ADMIN);
    long regime = jdbc.queryForObject("select id from insurance_regimes where code = 'CONTRIBUTIVO'", Long.class);
    long n = unique();
    EpsItem eps = catalogs.createEps(new EpsRequest("eps" + n, "EPS Prueba " + n, true), admin);
    assertEquals("EPS" + n, eps.code());
    assertStatus(409, () -> catalogs.createEps(new EpsRequest("EPS" + n, "Duplicada", true), admin));
    PlanItem plan = catalogs.createPlan(new PlanRequest(eps.id(), regime, "p1", "Plan Uno", true), admin);
    assertStatus(409, () -> catalogs.createPlan(new PlanRequest(eps.id(), regime, "P1", "Plan repetido", true), admin));
    assertStatus(400, () -> catalogs.createPlan(new PlanRequest(-1L, regime, "P2", "Sin EPS", true), admin));
    PlanItem spare = catalogs.createPlan(new PlanRequest(eps.id(), regime, "P2", "Plan Dos", true), admin);
    assertEquals("Plan Dos Editado", catalogs.updatePlan(spare.id(), new PlanRequest(eps.id(), regime, "P2", "Plan Dos Editado", true), admin).name());
    catalogs.deletePlan(spare.id(), admin);

    long patient = newUser(Role.USER);
    profiles.saveAffiliation(new AffiliationRequest(eps.id(), plan.id()), auth(patient, Role.USER));
    assertStatus(409, () -> catalogs.deletePlan(plan.id(), admin));
    assertStatus(409, () -> catalogs.deleteEps(eps.id(), admin));

    catalogs.updateEps(eps.id(), new EpsRequest(eps.code(), eps.name(), false), admin);
    assertFalse(catalogs.activePlans().stream().anyMatch(p -> p.id().equals(plan.id())), "EPS inactiva inhabilita sus planes");
    assertStatus(400, () -> profiles.saveAffiliation(new AffiliationRequest(eps.id(), plan.id()), auth(newUser(Role.USER), Role.USER)));
    assertEquals(plan.id(), profiles.affiliation(auth(patient, Role.USER)).planId(), "la afiliación existente se conserva");
    assertFalse(profiles.affiliation(auth(patient, Role.USER)).active());

    EpsItem unused = catalogs.createEps(new EpsRequest("TMP" + n, "Temporal", true), admin);
    catalogs.deleteEps(unused.id(), admin);
    assertStatus(404, () -> catalogs.deleteEps(unused.id(), admin));
  }

  @Test
  void hu009SpecialtyCrudRules() {
    var admin = auth(newUser(Role.ADMIN), Role.ADMIN);
    long n = unique();
    SpecialtyItem created = catalogs.createSpecialty(new SpecialtyRequest("Dermatología " + n, 30, true, true), admin);
    assertStatus(409, () -> catalogs.createSpecialty(new SpecialtyRequest("DERMATOLOGÍA " + n, 30, true, true), admin));
    assertStatus(400, () -> catalogs.createSpecialty(new SpecialtyRequest("Duración inválida " + n, 45, true, true), admin));
    assertTrue(catalogs.activeSpecialties().stream().anyMatch(s -> s.id().equals(created.id())));

    SpecialtyItem inactive = catalogs.updateSpecialty(created.id(), new SpecialtyRequest(created.name(), 30, true, false), admin);
    assertFalse(inactive.active());
    assertFalse(catalogs.activeSpecialties().stream().anyMatch(s -> s.id().equals(created.id())), "inactiva no es elegible");

    long used = specialty(30, true);
    professional(newUser(Role.ADMIN), List.of(used), List.of(location("HIC")));
    assertStatus(409, () -> catalogs.deleteSpecialty(used, admin));
    catalogs.deleteSpecialty(created.id(), admin);
  }

  @Test
  void hu012DurationIsThirtyOrSixtyAndOnlyAdminChangesIt() {
    var admin = auth(newUser(Role.ADMIN), Role.ADMIN);
    long id = specialty(30, true);
    assertEquals(60, catalogs.updateSpecialty(id, new SpecialtyRequest("Especialidad sesenta " + id, 60, true, true), admin).durationMinutes());
    assertStatus(400, () -> catalogs.updateSpecialty(id, new SpecialtyRequest("Especialidad sesenta " + id, 90, true, true), admin));
    assertStatus(403, () -> catalogs.updateSpecialty(id, new SpecialtyRequest("x", 30, true, true), auth(newUser(Role.PROFESSIONAL), Role.PROFESSIONAL)));
  }

  @Test
  void catalogAdministrationIsAdminOnly() {
    for (Role role : List.of(Role.USER, Role.PROFESSIONAL)) {
      var actor = auth(newUser(role), role);
      assertStatus(403, () -> catalogs.eps(actor));
      assertStatus(403, () -> catalogs.createEps(new EpsRequest("X" + unique(), "X", true), actor));
      assertStatus(403, () -> catalogs.createSpecialty(new SpecialtyRequest("X" + unique(), 30, true, true), actor));
      assertStatus(403, () -> catalogs.deleteSpecialty(generalSpecialty(), actor));
    }
  }
}
