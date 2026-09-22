package co.fcv.citas.auth;
import jakarta.validation.Valid; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") public class AuthController {private final AuthService auth; public AuthController(AuthService a){auth=a;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AuthDtos.TokenResponse register(@Valid @RequestBody AuthDtos.RegisterRequest r){return auth.register(r);}
 @PostMapping("/login") public AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest r){return auth.login(r);}
 @PostMapping("/refresh") public AuthDtos.TokenResponse refresh(@Valid @RequestBody AuthDtos.RefreshRequest r){return auth.refresh(r.refreshToken());}
 @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) public void logout(@Valid @RequestBody AuthDtos.LogoutRequest r){auth.logout(r.refreshToken());}
}
