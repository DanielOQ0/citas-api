package co.fcv.citas.scheduling;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.List;

/** Contrato REST de agenda (ver llm-wiki/wiki/contratos-rest.md). */
public final class SchedulingDtos {
  private SchedulingDtos() {}

  public static final String DOCUMENT_TYPES = "CC|CE|TI|PA";
  public static final String DOCUMENT_NUMBER = "[A-Za-z0-9]{5,20}";
  public static final String PHONE = "\\d{7,15}";

  // Catálogos
  public record CatalogItem(Long id, String code, String name) {}
  public record EpsItem(Long id, String code, String name, boolean active) {}
  public record EpsRequest(@NotBlank @Size(max = 30) String code, @NotBlank @Size(max = 150) String name, boolean active) {}
  public record PlanItem(Long id, String code, String name, Long epsId, String epsName, Long regimeId, String regimeName, boolean active) {}
  public record PlanRequest(@NotNull Long epsId, @NotNull Long regimeId, @NotBlank @Size(max = 50) String code, @NotBlank @Size(max = 150) String name, boolean active) {}
  public record SpecialtyItem(Long id, String name, int durationMinutes, boolean requiresAdminApproval, boolean active) {}
  public record SpecialtyRequest(@NotBlank @Size(max = 120) String name, @NotNull Integer durationMinutes, boolean requiresAdminApproval, boolean active) {}

  // Profesionales
  public record ProfessionalItem(Long id, String name, String professionalCode, boolean active) {}
  public record ProfessionalAdminItem(Long id, String name, String email, String professionalCode, String licenseNumber, boolean active,
                                      List<Long> specialtyIds, Long primarySpecialtyId, List<Long> locationIds) {}
  public record ProfessionalRequest(
      @NotBlank @Size(max = 80) String firstName,
      @NotBlank @Size(max = 80) String lastName,
      @NotBlank @Pattern(regexp = DOCUMENT_TYPES, message = "Tipo de documento no válido (CC, CE, TI o PA)") String documentType,
      @NotBlank @Pattern(regexp = DOCUMENT_NUMBER, message = "El documento debe tener de 5 a 20 caracteres alfanuméricos") String documentNumber,
      @NotBlank @Email(message = "Email no válido") String email,
      @NotBlank @Pattern(regexp = PHONE, message = "El teléfono debe tener entre 7 y 15 dígitos") String phone,
      @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
      @NotBlank @Size(max = 40) String professionalCode,
      @NotBlank @Size(max = 80) String licenseNumber) {}
  public record ProfessionalAssignments(@NotEmpty List<Long> specialtyIds, @NotNull Long primarySpecialtyId, @NotEmpty List<Long> locationIds) {}

  // Disponibilidad
  public record AvailabilityBlockRequest(@NotNull Long locationId, @NotNull LocalDate date, @NotNull LocalTime startTime, @NotNull LocalTime endTime) {}
  public record AvailabilityBlockItem(Long id, Long locationId, String locationName, LocalDate date, LocalTime startTime, LocalTime endTime,
                                      int slots, int committedSlots) {}
  public record AvailabilityItem(Long professionalId, String professionalName, Long locationId, Long specialtyId, LocalDate date, LocalTime startTime,
                                 int durationMinutes) {}

  // Citas
  public record AppointmentRequest(@NotNull Long professionalId, @NotNull Long locationId, @NotNull Long specialtyId, @NotNull LocalDate date,
                                   @NotNull LocalTime startTime, @Size(max = 500) String reason) {}
  public record RescheduleSummary(Long id, String status, LocalDate requestedDate, LocalTime requestedStart, String decisionReason, String patientAction) {}
  public record AppointmentItem(Long id, String status, String patientName, Long professionalId, String professionalName, Long locationId,
                                String locationName, Long specialtyId, String specialtyName, boolean requiresAdminApproval, LocalDate date,
                                LocalTime startTime, LocalTime endTime, int durationMinutes, String reason, String rejectionReason,
                                RescheduleSummary reschedule) {}
  public record DecisionRequest(@NotBlank @Pattern(regexp = "APPROVE|REJECT") String decision, @Size(max = 500) String reason) {}
  public record CloseAppointmentRequest(@NotBlank @Pattern(regexp = "COMPLETED|NO_SHOW") String status, @Size(max = 500) String reason) {}
  public record HistoryItem(String status, Long actorId, String actorName, String source, String reason, LocalDateTime occurredAt) {}

  // Reprogramación
  public record RescheduleRequest(@NotNull LocalDate date, @NotNull LocalTime startTime) {}
  public record RescheduleItem(Long id, Long appointmentId, String status, String patientName, Long professionalId, String professionalName,
                               Long specialtyId, String specialtyName, Long locationId, String locationName, LocalDate previousDate,
                               LocalTime previousStart, LocalDate requestedDate, LocalTime requestedStart, int durationMinutes,
                               String decisionReason, String patientAction) {}
  public record RescheduleDecisionRequest(@NotBlank @Pattern(regexp = "APPROVE|REJECT") String decision, @Size(max = 500) String reason) {}

  // Perfil y afiliación
  public record ProfileItem(Long id, String name, String firstName, String lastName, String email, String documentType, String documentNumber,
                            String phone) {}
  public record PhoneRequest(@NotBlank @Pattern(regexp = PHONE, message = "El teléfono debe tener entre 7 y 15 dígitos") String phone) {}
  public record AffiliationRequest(@NotNull Long epsId, @NotNull Long insurancePlanId) {}
  public record AffiliationItem(Long id, Long epsId, String epsName, Long planId, String planName, String regimeName, boolean active) {}
}
