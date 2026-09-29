package co.fcv.citas.scheduling;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.List;

public final class SchedulingDtos {
  private SchedulingDtos() {}
  public record CatalogItem(Long id, String code, String name) {}
  public record PlanItem(Long id, String name, String epsName, boolean active) {}
  public record EpsRequest(@NotBlank String code,@NotBlank String name,boolean active) {}
  public record PlanRequest(@NotNull Long epsId,@NotNull Long regimeId,@NotBlank String code,@NotBlank String name,boolean active) {}
  public record EpsItem(Long id,String code,String name,boolean active) {}
  public record SpecialtyRequest(@NotBlank String name, @NotNull @Min(30) @Max(60) Integer durationMinutes, boolean requiresAdminApproval, boolean active) {}
  public record SpecialtyItem(Long id, String name, int durationMinutes, boolean requiresAdminApproval, boolean active) {}
  public record ProfessionalRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String documentType,
      @NotBlank String documentNumber, @Email @NotBlank String email, @NotBlank String phone, @Size(min=8) String password,
      @NotBlank String professionalCode, @NotBlank String licenseNumber) {}
  public record ProfessionalAssignments(@NotEmpty List<Long> specialtyIds, @NotNull Long primarySpecialtyId, @NotEmpty List<Long> locationIds) {}
  public record ProfessionalItem(Long id, String name, String professionalCode, boolean active) {}
  public record AvailabilityBlockRequest(@NotNull Long locationId, @NotNull LocalDate date, @NotNull LocalTime startTime, @NotNull LocalTime endTime) {}
  public record AvailabilityBlockItem(Long id, Long locationId, LocalDate date, LocalTime startTime, LocalTime endTime) {}
  public record AvailabilityItem(Long professionalId, Long locationId, Long specialtyId, LocalDate date, LocalTime startTime, int durationMinutes) {}
  public record AppointmentRequest(@NotNull Long professionalId, @NotNull Long locationId, @NotNull Long specialtyId, @NotNull LocalDate date, @NotNull LocalTime startTime, @Size(max=500) String reason) {}
  public record AppointmentItem(Long id, String status, Long professionalId, Long locationId, Long specialtyId, LocalDate date, LocalTime startTime, int durationMinutes, String rejectionReason) {}
  public record DecisionRequest(@NotBlank @Pattern(regexp="APPROVE|REJECT") String decision, @Size(max=500) String reason) {}
  public record CloseAppointmentRequest(@NotBlank @Pattern(regexp="COMPLETED|NO_SHOW") String status, @Size(max=500) String reason) {}
  public record ProfileItem(Long id, String name, String email, String phone) {}
  public record PhoneRequest(@NotBlank @Size(max=30) String phone) {}
  public record AffiliationRequest(@NotNull Long insurancePlanId) {}
  public record AffiliationItem(Long id,Long planId,String planName,String epsName,String regimeName,boolean active) {}
  public record RescheduleRequest(@NotNull LocalDate date,@NotNull LocalTime startTime,@Size(max=500) String reason) {}
  public record RescheduleItem(Long id,Long appointmentId,String status,LocalDate previousDate,LocalTime previousStart,LocalDate requestedDate,LocalTime requestedStart,String reason) {}
  public record RescheduleDecisionRequest(@NotBlank @Pattern(regexp="APPROVE|REJECT") String decision,@Size(max=500) String reason) {}
  public record HistoryItem(String status, Long actorId, String source, String reason, java.time.LocalDateTime occurredAt) {}
}
