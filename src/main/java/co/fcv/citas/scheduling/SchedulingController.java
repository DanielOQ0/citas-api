package co.fcv.citas.scheduling;

import co.fcv.citas.scheduling.SchedulingDtos.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Adaptador REST de agenda: traduce HTTP y delega en los casos de uso; no contiene reglas de negocio. */
@RestController
@RequestMapping("/api/v1")
public class SchedulingController {
  private final CatalogService catalogs;
  private final ProfessionalService professionals;
  private final AvailabilityService availability;
  private final AppointmentService appointments;
  private final RescheduleService reschedules;
  private final ProfileService profiles;

  public SchedulingController(CatalogService catalogs, ProfessionalService professionals, AvailabilityService availability,
                              AppointmentService appointments, RescheduleService reschedules, ProfileService profiles) {
    this.catalogs = catalogs;
    this.professionals = professionals;
    this.availability = availability;
    this.appointments = appointments;
    this.reschedules = reschedules;
    this.profiles = profiles;
  }

  // Catálogos públicos de solo lectura (HU-001)
  @GetMapping("/catalogs/locations") public List<CatalogItem> locations() { return catalogs.locations(); }
  @GetMapping("/catalogs/regimes") public List<CatalogItem> regimes() { return catalogs.regimes(); }
  @GetMapping("/catalogs/roles") public List<CatalogItem> roles() { return catalogs.roles(); }
  @GetMapping("/catalogs/appointment-statuses") public List<CatalogItem> appointmentStatuses() { return catalogs.appointmentStatuses(); }
  @GetMapping("/catalogs/reschedule-statuses") public List<CatalogItem> rescheduleStatuses() { return catalogs.rescheduleStatuses(); }
  @GetMapping("/catalogs/insurance-plans") public List<PlanItem> plans() { return catalogs.activePlans(); }
  @GetMapping("/catalogs/specialties") public List<SpecialtyItem> catalogSpecialties() { return catalogs.activeSpecialties(); }
  @GetMapping("/catalogs/professionals")
  public List<ProfessionalItem> catalogProfessionals(@RequestParam(required = false) Long specialtyId, @RequestParam(required = false) Long locationId) {
    return professionals.catalog(specialtyId, locationId);
  }

  // EPS y planes (HU-008)
  @GetMapping("/admin/eps") public List<EpsItem> eps(Authentication a) { return catalogs.eps(a); }
  @PostMapping("/admin/eps") @ResponseStatus(HttpStatus.CREATED)
  public EpsItem createEps(@Valid @RequestBody EpsRequest r, Authentication a) { return catalogs.createEps(r, a); }
  @PatchMapping("/admin/eps/{id}") public EpsItem updateEps(@PathVariable long id, @Valid @RequestBody EpsRequest r, Authentication a) { return catalogs.updateEps(id, r, a); }
  @DeleteMapping("/admin/eps/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEps(@PathVariable long id, Authentication a) { catalogs.deleteEps(id, a); }
  @GetMapping("/admin/plans") public List<PlanItem> adminPlans(Authentication a) { return catalogs.plans(a); }
  @PostMapping("/admin/plans") @ResponseStatus(HttpStatus.CREATED)
  public PlanItem createPlan(@Valid @RequestBody PlanRequest r, Authentication a) { return catalogs.createPlan(r, a); }
  @PatchMapping("/admin/plans/{id}") public PlanItem updatePlan(@PathVariable long id, @Valid @RequestBody PlanRequest r, Authentication a) { return catalogs.updatePlan(id, r, a); }
  @DeleteMapping("/admin/plans/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePlan(@PathVariable long id, Authentication a) { catalogs.deletePlan(id, a); }

  // Especialidades (HU-009, HU-012)
  @GetMapping("/admin/specialties") public List<SpecialtyItem> specialties(Authentication a) { return catalogs.specialties(a); }
  @PostMapping("/admin/specialties") @ResponseStatus(HttpStatus.CREATED)
  public SpecialtyItem createSpecialty(@Valid @RequestBody SpecialtyRequest r, Authentication a) { return catalogs.createSpecialty(r, a); }
  @PatchMapping("/admin/specialties/{id}")
  public SpecialtyItem updateSpecialty(@PathVariable long id, @Valid @RequestBody SpecialtyRequest r, Authentication a) { return catalogs.updateSpecialty(id, r, a); }
  @DeleteMapping("/admin/specialties/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSpecialty(@PathVariable long id, Authentication a) { catalogs.deleteSpecialty(id, a); }

  // Profesionales (HU-010, HU-011)
  @GetMapping("/admin/professionals") public List<ProfessionalAdminItem> professionals(Authentication a) { return professionals.list(a); }
  @PostMapping("/admin/professionals") @ResponseStatus(HttpStatus.CREATED)
  public ProfessionalAdminItem createProfessional(@Valid @RequestBody ProfessionalRequest r, Authentication a) { return professionals.create(r, a); }
  @PutMapping("/admin/professionals/{id}/assignments") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void assignments(@PathVariable long id, @Valid @RequestBody ProfessionalAssignments r, Authentication a) { professionals.assign(id, r, a); }
  @PatchMapping("/admin/professionals/{id}/active") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void active(@PathVariable long id, @RequestParam boolean active, Authentication a) { professionals.setActive(id, active, a); }

  // Bloques y disponibilidad (HU-013 a HU-015)
  @GetMapping("/professional/availability-blocks")
  public List<AvailabilityBlockItem> blocks(@RequestParam(required = false) LocalDate date, @RequestParam(required = false) Long locationId, Authentication a) {
    return availability.blocks(date, locationId, a);
  }
  @PostMapping("/professional/availability-blocks") @ResponseStatus(HttpStatus.CREATED)
  public AvailabilityBlockItem block(@Valid @RequestBody AvailabilityBlockRequest r, Authentication a) { return availability.addBlock(r, a); }
  @PatchMapping("/professional/availability-blocks/{id}")
  public AvailabilityBlockItem updateBlock(@PathVariable long id, @Valid @RequestBody AvailabilityBlockRequest r, Authentication a) { return availability.updateBlock(id, r, a); }
  @DeleteMapping("/professional/availability-blocks/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBlock(@PathVariable long id, Authentication a) { availability.deleteBlock(id, a); }
  @GetMapping("/availability")
  public List<AvailabilityItem> availability(@RequestParam long locationId, @RequestParam long specialtyId,
                                             @RequestParam(required = false) Long professionalId, @RequestParam LocalDate date) {
    return availability.availability(locationId, specialtyId, professionalId, date);
  }

  // Citas (HU-016 a HU-020, HU-023 a HU-025)
  @PostMapping("/appointments") @ResponseStatus(HttpStatus.CREATED)
  public AppointmentItem appointment(@Valid @RequestBody AppointmentRequest r, Authentication a) { return appointments.book(r, a); }
  @GetMapping("/appointments")
  public List<AppointmentItem> mine(@RequestParam(required = false) String status, @RequestParam(required = false) LocalDate from,
                                    @RequestParam(required = false) LocalDate to, Authentication a) {
    return appointments.mine(status, from, to, a);
  }
  @PostMapping("/appointments/{id}/cancel") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@PathVariable long id, Authentication a) { appointments.cancel(id, a); }
  @GetMapping("/appointments/{id}/history") public List<HistoryItem> history(@PathVariable long id, Authentication a) { return appointments.history(id, a); }
  @GetMapping("/admin/appointments")
  public List<AppointmentItem> requested(@RequestParam(required = false) Long locationId, @RequestParam(required = false) Long professionalId,
                                         @RequestParam(required = false) Long specialtyId, @RequestParam(required = false) LocalDate date, Authentication a) {
    return appointments.requested(locationId, professionalId, specialtyId, date, a);
  }
  @PostMapping("/admin/appointments/{id}/decision")
  public AppointmentItem decision(@PathVariable long id, @Valid @RequestBody DecisionRequest r, Authentication a) { return appointments.decide(id, r, a); }
  @GetMapping("/professional/appointments")
  public List<AppointmentItem> agenda(@RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
                                      @RequestParam(required = false) Long locationId, Authentication a) {
    return appointments.agenda(from, to, locationId, a);
  }
  @PostMapping("/professional/appointments/{id}/close")
  public AppointmentItem close(@PathVariable long id, @Valid @RequestBody CloseAppointmentRequest r, Authentication a) { return appointments.close(id, r, a); }

  // Reprogramación (HU-021, HU-022)
  @PostMapping("/appointments/{id}/reschedule-requests") @ResponseStatus(HttpStatus.CREATED)
  public RescheduleItem requestReschedule(@PathVariable long id, @Valid @RequestBody RescheduleRequest r, Authentication a) { return reschedules.request(id, r, a); }
  @PostMapping("/appointments/{id}/reschedule-requests/{requestId}/keep")
  public RescheduleItem keepAfterRejection(@PathVariable long id, @PathVariable long requestId, Authentication a) { return reschedules.keep(id, requestId, a); }
  @GetMapping("/admin/reschedule-requests")
  public List<RescheduleItem> pendingReschedules(@RequestParam(required = false) Long locationId, @RequestParam(required = false) Long professionalId,
                                                 @RequestParam(required = false) Long specialtyId, @RequestParam(required = false) LocalDate date, Authentication a) {
    return reschedules.pending(locationId, professionalId, specialtyId, date, a);
  }
  @PostMapping("/admin/reschedule-requests/{id}/decision")
  public RescheduleItem decideReschedule(@PathVariable long id, @Valid @RequestBody RescheduleDecisionRequest r, Authentication a) { return reschedules.decide(id, r, a); }

  // Perfil y afiliación (HU-006, HU-007)
  @GetMapping("/users/me") public ProfileItem profile(Authentication a) { return profiles.profile(a); }
  @PatchMapping("/users/me") public ProfileItem profile(@Valid @RequestBody PhoneRequest r, Authentication a) { return profiles.updatePhone(r, a); }
  @GetMapping("/users/me/affiliation") public AffiliationItem affiliation(Authentication a) { return profiles.affiliation(a); }
  @PutMapping("/users/me/affiliation") public AffiliationItem affiliation(@Valid @RequestBody AffiliationRequest r, Authentication a) { return profiles.saveAffiliation(r, a); }
}
