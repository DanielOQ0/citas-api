package co.fcv.citas.auth;
import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.Set;
@RestController @RequestMapping("/api/me") public class MeController {private final UserRepository users; public MeController(UserRepository u){users=u;} @GetMapping public AuthDtos.UserView me(Authentication a){UserEntity u=users.findById(Long.valueOf(a.getName())).orElseThrow();return new AuthDtos.UserView(u.getId(),u.getFirstName()+" "+u.getLastName(),u.getEmail(),Set.copyOf(u.getRoles()));}}
