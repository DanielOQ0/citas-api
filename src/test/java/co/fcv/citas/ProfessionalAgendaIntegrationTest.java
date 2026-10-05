package co.fcv.citas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** HU-010, HU-011, HU-013, HU-014. */
class ProfessionalAgendaIntegrationTest extends IntegrationTestBase {

  @Test
  void hu010OnlyAdminCreatesProfessionalsWithUniqueCodeAndLicense() {
    long admin = newUser(Role.ADMIN);
    long n = unique();
    ProfessionalAdminItem created = professionals.create(new ProfessionalRequest("Laura", "Demo", "CC", "PR" + n, "laura" + n + "@test.co",
        "3001112233", "password123", "prof-" + n, "rm-" + n), auth(admin, Role.ADMIN));
    assertEquals("PROF-" + n, created.professionalCode());
    long userId = jdbc.queryForObject("select user_id from professionals where id = ?", Long.class, created.id());
    assertEquals(List.of(Role.PROFESSIONAL), List.copyOf(users.findById(userId).orElseThrow().getRoles()));
    assertStatus(409, () -> professionals.create(new ProfessionalRequest("Otro", "Demo", "CC", "PX" + n, "otro" + n + "@test.co",
        "3001112233", "password123", "PROF-" + n, "RM-X" + n), auth(admin, Role.ADMIN)));
    assertStatus(409, () -> professionals.create(new ProfessionalRequest("Otro", "Demo", "CC", "PY" + n, "otro2" + n + "@test.co",
        "3001112233", "password123", "PROF-Y" + n, "RM-" + n), auth(admin, Role.ADMIN)));
    assertStatus(403, () -> professionals.create(new ProfessionalRequest("No", "Admin", "CC", "PZ" + n, "z" + n + "@test.co",
        "3001112233", "password123", "PROF-Z" + n, "RM-Z" + n), auth(newUser(Role.USER), Role.USER)));
  }

  @Test
  void hu011AssignmentsKeepOnePrimaryAndControlEligibility() {
    long admin = newUser(Role.ADMIN);
    long general = generalSpecialty();
    long cardio = specialty(60, true);
    Pro pro = professional(admin, List.of(general, cardio), List.of(location("HIC"), location("ICV")));
    ProfessionalAdminItem item = professionals.list(auth(admin, Role.ADMIN)).stream().filter(p -> p.id() == pro.id()).findFirst().orElseThrow();
    assertEquals(List.of(general, cardio).stream().sorted().toList(), item.specialtyIds());
    assertEquals(general, item.primarySpecialtyId());
    assertEquals(2, item.locationIds().size());
    assertEquals(1, jdbc.queryForObject("select count(*) from professional_specialties where professional_id = ? and is_primary = true",
        Integer.class, pro.id()));
    assertStatus(400, () -> professionals.assign(pro.id(), new ProfessionalAssignments(List.of(general), cardio, List.of(location("HIC"))), auth(admin, Role.ADMIN)));
    assertStatus(400, () -> professionals.assign(pro.id(), new ProfessionalAssignments(List.of(-5L), -5L, List.of(location("HIC"))), auth(admin, Role.ADMIN)));
    assertStatus(400, () -> professionals.assign(pro.id(), new ProfessionalAssignments(List.of(general), general, List.of(-7L)), auth(admin, Role.ADMIN)));

    professionals.assign(pro.id(), new ProfessionalAssignments(List.of(general), general, List.of(location("HIC"))), auth(admin, Role.ADMIN));
    assertStatus(400, () -> block(pro, location("ICV"), day(2), "09:00", "10:00"));
    block(pro, location("HIC"), day(2), "09:00", "10:00");
    assertEquals(2, availability.availability(location("HIC"), general, pro.id(), day(2)).size());

    professionals.setActive(pro.id(), false, auth(admin, Role.ADMIN));
    assertTrue(availability.availability(location("HIC"), general, pro.id(), day(2)).isEmpty(), "inactivo no se oferta");
    assertFalse(professionals.catalog(general, null).stream().anyMatch(p -> p.id().equals(pro.id())));
    assertStatus(403, () -> block(pro, location("HIC"), day(3), "09:00", "10:00"));
    assertEquals(1, availability.blocks(null, null, auth(pro.userId(), Role.PROFESSIONAL)).size(), "conserva la consulta de su agenda");
    assertTrue(appointments.agenda(null, null, null, auth(pro.userId(), Role.PROFESSIONAL)).isEmpty());
  }

  @Test
  void hu013BlocksAreFutureAlignedNonOverlappingAndProtectedWhenCommitted() {
    long admin = newUser(Role.ADMIN);
    long general = generalSpecialty();
    Pro pro = professional(admin, List.of(general), List.of(location("HIC")));
    var actor = auth(pro.userId(), Role.PROFESSIONAL);
    long hic = location("HIC");

    AvailabilityBlockItem today = block(pro, hic, day(0), "10:00", "12:00");
    assertEquals(4, today.slots(), "08:00 + bloque hoy 10:00-12:00 = 4 slots de 30 min");
    assertStatus(400, () -> block(pro, hic, day(0), "07:00", "09:00"));
    assertStatus(400, () -> block(pro, hic, day(-1), "10:00", "11:00"));
    assertStatus(400, () -> block(pro, hic, day(1), "10:15", "11:00"));
    assertStatus(409, () -> block(pro, hic, day(0), "11:30", "13:00"));
    block(pro, hic, day(0), "14:00", "17:00");

    AvailabilityBlockItem tomorrow = block(pro, hic, day(1), "08:00", "09:00");
    AvailabilityBlockItem edited = availability.updateBlock(tomorrow.id(), new AvailabilityBlockRequest(hic, day(1), LocalTime.of(8, 0), LocalTime.of(10, 0)), actor);
    assertEquals(4, edited.slots());
    book(newUser(Role.USER), pro, hic, general, day(1), "08:30");
    assertStatus(409, () -> availability.updateBlock(tomorrow.id(), new AvailabilityBlockRequest(hic, day(1), LocalTime.of(8, 0), LocalTime.of(9, 0)), actor));
    assertStatus(409, () -> availability.deleteBlock(tomorrow.id(), actor));
    assertEquals(1, availability.blocks(day(1), null, actor).getFirst().committedSlots());

    AvailabilityBlockItem free = block(pro, hic, day(4), "08:00", "09:00");
    availability.deleteBlock(free.id(), actor);
    assertStatus(404, () -> availability.deleteBlock(free.id(), actor));
  }

  @Test
  void hu014CalendarShowsOnlyOwnBlocksWithFilters() {
    long admin = newUser(Role.ADMIN);
    long general = generalSpecialty();
    Pro first = professional(admin, List.of(general), List.of(location("HIC"), location("ICV")));
    Pro second = professional(admin, List.of(general), List.of(location("HIC")));
    AvailabilityBlockItem own = block(first, location("HIC"), day(2), "08:00", "10:00");
    block(first, location("ICV"), day(3), "08:00", "10:00");
    block(second, location("HIC"), day(2), "08:00", "10:00");

    var firstActor = auth(first.userId(), Role.PROFESSIONAL);
    assertEquals(2, availability.blocks(null, null, firstActor).size());
    assertEquals(List.of(own.id()), availability.blocks(day(2), location("HIC"), firstActor).stream().map(AvailabilityBlockItem::id).toList());
    assertEquals(1, availability.blocks(null, null, auth(second.userId(), Role.PROFESSIONAL)).size(), "no ve bloques ajenos");
    assertStatus(404, () -> availability.deleteBlock(own.id(), auth(second.userId(), Role.PROFESSIONAL)));
    assertStatus(403, () -> availability.blocks(null, null, auth(newUser(Role.USER), Role.USER)));
  }
}
