package co.fcv.citas.scheduling;

import static co.fcv.citas.scheduling.Access.badRequest;
import static co.fcv.citas.scheduling.Access.notFound;
import static co.fcv.citas.scheduling.Access.requireRole;
import static co.fcv.citas.scheduling.Access.userId;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.AffiliationItem;
import co.fcv.citas.scheduling.SchedulingDtos.AffiliationRequest;
import co.fcv.citas.scheduling.SchedulingDtos.PhoneRequest;
import co.fcv.citas.scheduling.SchedulingDtos.ProfileItem;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Perfil propio (HU-006: solo el teléfono es editable, D-08) y afiliación EPS/plan/régimen (HU-007, D-09, D-10). */
@Service
public class ProfileService {
  private final JdbcTemplate jdbc;
  private final Clock clock;

  public ProfileService(JdbcTemplate jdbc, Clock clock) {
    this.jdbc = jdbc;
    this.clock = clock;
  }

  /** El perfil siempre es el del token: no existe forma de pedir un perfil ajeno (HU-006 CA-03). */
  public ProfileItem profile(Authentication auth) {
    List<ProfileItem> rows = jdbc.query("""
        select id, first_name, last_name, email, document_type, document_number, phone from users where id = ?""",
        (rs, n) -> new ProfileItem(rs.getLong(1), rs.getString(2) + " " + rs.getString(3), rs.getString(2), rs.getString(3), rs.getString(4),
            rs.getString(5), rs.getString(6), rs.getString(7)), userId(auth));
    if (rows.isEmpty()) throw notFound("Usuario no encontrado");
    return rows.getFirst();
  }

  @Transactional
  public ProfileItem updatePhone(PhoneRequest request, Authentication auth) {
    jdbc.update("update users set phone = ? where id = ?", request.phone(), userId(auth));
    return profile(auth);
  }

  public AffiliationItem affiliation(Authentication auth) {
    requireRole(auth, Role.USER);
    return jdbc.query("""
        select a.id, e.id, e.name, p.id, p.name, r.name, p.active, e.active
        from user_insurance_affiliations a
        join eps_plans p on p.id = a.plan_id
        join eps e on e.id = p.eps_id
        join insurance_regimes r on r.id = p.regime_id
        where a.user_id = ? and a.is_current = true order by a.id desc""",
        rs -> rs.next()
            ? new AffiliationItem(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getLong(4), rs.getString(5), rs.getString(6),
                rs.getBoolean(7) && rs.getBoolean(8))
            : null,
        userId(auth));
  }

  /** Una afiliación vigente por usuario; las anteriores quedan como historial (is_current = false). */
  @Transactional
  public AffiliationItem saveAffiliation(AffiliationRequest request, Authentication auth) {
    requireRole(auth, Role.USER);
    long user = userId(auth);
    List<Map<String, Object>> plan = jdbc.queryForList("""
        select p.eps_id, p.active, e.active as eps_active from eps_plans p join eps e on e.id = p.eps_id where p.id = ?""",
        request.insurancePlanId());
    if (plan.isEmpty()) throw badRequest("El plan no existe");
    if (((Number) plan.getFirst().get("eps_id")).longValue() != request.epsId()) throw badRequest("El plan no pertenece a la EPS seleccionada");
    if (!isTrue(plan.getFirst().get("active")) || !isTrue(plan.getFirst().get("eps_active"))) {
      throw badRequest("El plan o la EPS no están activos para nuevas afiliaciones");
    }
    jdbc.update("update user_insurance_affiliations set is_current = false where user_id = ? and is_current = true", user);
    // Volver a un plan anterior reactiva su registro (la BD exige unicidad usuario/plan/número de afiliación).
    if (jdbc.update("update user_insurance_affiliations set is_current = true, valid_from = ? where user_id = ? and plan_id = ?",
        LocalDate.now(clock), user, request.insurancePlanId()) == 0) {
      jdbc.update("insert into user_insurance_affiliations(user_id, plan_id, membership_number, is_current, valid_from) values (?, ?, ?, true, ?)",
          user, request.insurancePlanId(), "PENDIENTE-" + user, LocalDate.now(clock));
    }
    return affiliation(auth);
  }

  private static boolean isTrue(Object value) {
    return value instanceof Boolean b ? b : value instanceof Number n && n.intValue() != 0;
  }
}
