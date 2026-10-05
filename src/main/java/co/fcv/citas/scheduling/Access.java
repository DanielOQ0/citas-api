package co.fcv.citas.scheduling;

import co.fcv.citas.auth.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

/** Autorización por rol y ownership de los casos de uso de agenda (RF-02, PRD §8). */
final class Access {
  private Access() {}

  static void requireRole(Authentication auth, Role role) {
    if (!hasRole(auth, role)) throw forbidden();
  }

  static boolean hasRole(Authentication auth, Role role) {
    return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
  }

  static long userId(Authentication auth) {
    if (auth == null) throw forbidden();
    return Long.parseLong(auth.getName());
  }

  static ResponseStatusException forbidden() {
    return forbidden("No autorizado");
  }

  static ResponseStatusException forbidden(String message) {
    return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
  }

  static ResponseStatusException notFound(String message) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
  }

  static ResponseStatusException badRequest(String message) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }

  static ResponseStatusException conflict(String message) {
    return new ResponseStatusException(HttpStatus.CONFLICT, message);
  }
}
