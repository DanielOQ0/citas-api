package co.fcv.citas.auth;
import jakarta.validation.constraints.*; import java.util.Set;
public final class AuthDtos {
 // D-07: tipo CC/CE/TI/PA, documento 5-20 alfanumérico, teléfono 7-15 dígitos y clave >= 8.
 public record RegisterRequest(@NotBlank @Size(max=80) String firstName,@NotBlank @Size(max=80) String lastName,@NotBlank @Pattern(regexp="CC|CE|TI|PA",message="Tipo de documento no válido (CC, CE, TI o PA)") String documentType,@NotBlank @Pattern(regexp="[A-Za-z0-9]{5,20}",message="El documento debe tener de 5 a 20 caracteres alfanuméricos") String documentNumber,@NotBlank @Email(message="Email no válido") String email,@NotBlank @Pattern(regexp="\\d{7,15}",message="El teléfono debe tener entre 7 y 15 dígitos") String phone,@NotBlank @Size(min=8,message="La contraseña debe tener al menos 8 caracteres") String password, Long insurancePlanId){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record RefreshRequest(@NotBlank String refreshToken){}
 public record LogoutRequest(@NotBlank String refreshToken){}
 public record PasswordRecoveryRequest(@Email @NotBlank String email){}
 public record PasswordResetRequest(@NotBlank String token,@NotBlank @Size(min=8,message="La contraseña debe tener al menos 8 caracteres") String password){}
 public record PasswordRecoveryResponse(boolean accepted,String developmentToken){}
 public record UserView(Long id,String name,String email,Set<Role> roles){}
 public record TokenResponse(String accessToken,String refreshToken,long expiresIn,UserView user){}
}
