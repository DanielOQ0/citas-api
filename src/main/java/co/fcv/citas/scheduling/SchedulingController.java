package co.fcv.citas.scheduling;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/api/v1")
public class SchedulingController {
  private final SchedulingService s; public SchedulingController(SchedulingService s){this.s=s;}
  @GetMapping("/catalogs/locations") public List<SchedulingDtos.CatalogItem> locations(){return s.locations();}
  @GetMapping("/catalogs/regimes") public List<SchedulingDtos.CatalogItem> regimes(){return s.regimes();}
  @GetMapping("/catalogs/appointment-statuses") public List<SchedulingDtos.CatalogItem> appointmentStatuses(){return s.statuses("appointment_statuses");}
  @GetMapping("/catalogs/reschedule-statuses") public List<SchedulingDtos.CatalogItem> rescheduleStatuses(){return s.statuses("reschedule_request_statuses");}
  @GetMapping("/catalogs/insurance-plans") public List<SchedulingDtos.PlanItem> plans(){return s.plans();}
  @GetMapping("/catalogs/specialties") public List<SchedulingDtos.SpecialtyItem> catalogSpecialties(){return s.specialties().stream().filter(SchedulingDtos.SpecialtyItem::active).toList();}
  @GetMapping("/admin/eps") public List<SchedulingDtos.EpsItem> eps(Authentication a){return s.adminEps(a);}
  @PostMapping("/admin/eps") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.EpsItem createEps(@Valid @RequestBody SchedulingDtos.EpsRequest r,Authentication a){return s.createEps(r,a);}
  @PatchMapping("/admin/eps/{id}") public SchedulingDtos.EpsItem updateEps(@PathVariable long id,@Valid @RequestBody SchedulingDtos.EpsRequest r,Authentication a){return s.updateEps(id,r,a);}
  @GetMapping("/admin/plans") public List<SchedulingDtos.PlanItem> adminPlans(Authentication a){return s.adminPlans(a);}
  @PostMapping("/admin/plans") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.PlanItem createPlan(@Valid @RequestBody SchedulingDtos.PlanRequest r,Authentication a){return s.createPlan(r,a);}
  @PatchMapping("/admin/plans/{id}") public SchedulingDtos.PlanItem updatePlan(@PathVariable long id,@Valid @RequestBody SchedulingDtos.PlanRequest r,Authentication a){return s.updatePlan(id,r,a);}
  @GetMapping("/admin/specialties") public List<SchedulingDtos.SpecialtyItem> specialties(Authentication a){return s.adminSpecialties(a);}
  @PostMapping("/admin/specialties") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.SpecialtyItem createSpecialty(@Valid @RequestBody SchedulingDtos.SpecialtyRequest r,Authentication a){return s.createSpecialty(r,a);}
  @PatchMapping("/admin/specialties/{id}") public SchedulingDtos.SpecialtyItem updateSpecialty(@PathVariable long id,@Valid @RequestBody SchedulingDtos.SpecialtyRequest r,Authentication a){return s.updateSpecialty(id,r,a);}
  @GetMapping("/admin/professionals") public List<SchedulingDtos.ProfessionalItem> professionals(Authentication a){return s.professionals(a);}
  @PostMapping("/admin/professionals") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.ProfessionalItem createProfessional(@Valid @RequestBody SchedulingDtos.ProfessionalRequest r,Authentication a){return s.createProfessional(r,a);}
  @PutMapping("/admin/professionals/{id}/assignments") @ResponseStatus(HttpStatus.NO_CONTENT) public void assignments(@PathVariable long id,@Valid @RequestBody SchedulingDtos.ProfessionalAssignments r,Authentication a){s.assignments(id,r,a);}
  @PatchMapping("/admin/professionals/{id}/active") @ResponseStatus(HttpStatus.NO_CONTENT) public void active(@PathVariable long id,@RequestParam boolean active,Authentication a){s.active(id,active,a);}
  @GetMapping("/professional/availability-blocks") public List<SchedulingDtos.AvailabilityBlockItem> blocks(@RequestParam(required=false) LocalDate date,@RequestParam(required=false) Long locationId,Authentication a){return s.blocks(date,locationId,a);}
  @PostMapping("/professional/availability-blocks") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.AvailabilityBlockItem block(@Valid @RequestBody SchedulingDtos.AvailabilityBlockRequest r,Authentication a){return s.addBlock(r,a);}
  @GetMapping("/availability") public List<SchedulingDtos.AvailabilityItem> availability(@RequestParam long locationId,@RequestParam long specialtyId,@RequestParam(required=false) Long professionalId,@RequestParam LocalDate date){return s.availability(locationId,specialtyId,professionalId,date);}
  @PostMapping("/appointments") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.AppointmentItem appointment(@Valid @RequestBody SchedulingDtos.AppointmentRequest r,Authentication a){return s.book(r,a);}
  @GetMapping("/admin/appointments") public List<SchedulingDtos.AppointmentItem> requested(@RequestParam(required=false) Long locationId,@RequestParam(required=false) Long professionalId,@RequestParam(required=false) Long specialtyId,@RequestParam(required=false) LocalDate date,Authentication a){return s.requested(locationId,professionalId,specialtyId,date,a);}
  @PostMapping("/admin/appointments/{id}/decision") public SchedulingDtos.AppointmentItem decision(@PathVariable long id,@Valid @RequestBody SchedulingDtos.DecisionRequest r,Authentication a){return s.decide(id,r,a);}
  @GetMapping("/users/me") public SchedulingDtos.ProfileItem profile(Authentication a){return s.profile(a);}
  @PatchMapping("/users/me") public SchedulingDtos.ProfileItem profile(@Valid @RequestBody SchedulingDtos.PhoneRequest r,Authentication a){return s.updateProfile(r,a);}
  @GetMapping("/users/me/affiliation") public SchedulingDtos.AffiliationItem affiliation(Authentication a){return s.affiliation(a);}
  @PutMapping("/users/me/affiliation") public SchedulingDtos.AffiliationItem affiliation(@Valid @RequestBody SchedulingDtos.AffiliationRequest r,Authentication a){return s.saveAffiliation(r,a);}
  @GetMapping("/appointments") public List<SchedulingDtos.AppointmentItem> mine(@RequestParam(required=false) String status,@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,Authentication a){return s.mine(status,from,to,a);}
  @PostMapping("/appointments/{id}/cancel") @ResponseStatus(HttpStatus.NO_CONTENT) public void cancel(@PathVariable long id,Authentication a){s.cancel(id,a);}
  @GetMapping("/appointments/{id}/history") public List<SchedulingDtos.HistoryItem> history(@PathVariable long id,Authentication a){return s.history(id,a);}
  @PostMapping("/professional/appointments/{id}/close") public SchedulingDtos.AppointmentItem close(@PathVariable long id,@Valid @RequestBody SchedulingDtos.CloseAppointmentRequest r,Authentication a){return s.close(id,r,a);}
  @PostMapping("/appointments/{id}/reschedule-requests") @ResponseStatus(HttpStatus.CREATED) public SchedulingDtos.RescheduleItem requestReschedule(@PathVariable long id,@Valid @RequestBody SchedulingDtos.RescheduleRequest r,Authentication a){return s.requestReschedule(id,r,a);}
  @GetMapping("/admin/reschedule-requests") public List<SchedulingDtos.RescheduleItem> pendingReschedules(@RequestParam(required=false) Long locationId,@RequestParam(required=false) Long professionalId,@RequestParam(required=false) Long specialtyId,@RequestParam(required=false) LocalDate date,Authentication a){return s.pendingReschedules(locationId,professionalId,specialtyId,date,a);}
  @PostMapping("/admin/reschedule-requests/{id}/decision") public SchedulingDtos.RescheduleItem decideReschedule(@PathVariable long id,@Valid @RequestBody SchedulingDtos.RescheduleDecisionRequest r,Authentication a){return s.decideReschedule(id,r,a);}
  @GetMapping("/professional/appointments") public List<SchedulingDtos.AppointmentItem> agenda(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,@RequestParam(required=false) Long locationId,Authentication a){return s.agenda(from,to,locationId,a);}
}
