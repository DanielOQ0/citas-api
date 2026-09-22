package co.fcv.citas.auth;
import jakarta.validation.constraints.*; import java.util.Set;
public final class AuthDtos {
 public record RegisterRequest(@NotBlank String firstName,@NotBlank String lastName,@NotBlank String documentType,@NotBlank String documentNumber,@Email @NotBlank String email,@NotBlank String phone,@Size(min=8) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record RefreshRequest(@NotBlank String refreshToken){}
 public record LogoutRequest(@NotBlank String refreshToken){}
 public record UserView(Long id,String name,String email,Set<Role> roles){}
 public record TokenResponse(String accessToken,String refreshToken,long expiresIn,UserView user){}
}
