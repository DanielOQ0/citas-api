package co.fcv.citas.scheduling;

import static co.fcv.citas.scheduling.Access.badRequest;
import static co.fcv.citas.scheduling.Access.conflict;
import static co.fcv.citas.scheduling.Access.notFound;
import static co.fcv.citas.scheduling.Access.requireRole;

import co.fcv.citas.auth.Role;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import co.fcv.citas.scheduling.domain.SlotPolicy;
import java.util.List;
import java.util.Locale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogos fijos de solo lectura (HU-001) y catálogos configurables por ADMIN (HU-008, HU-009, HU-012). */
@Service
public class CatalogService {
  private static final RowMapper<CatalogItem> CATALOG = (rs, n) -> new CatalogItem(rs.getLong(1), rs.getString(2), rs.getString(3));
  private static final RowMapper<EpsItem> EPS = (rs, n) -> new EpsItem(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
  private static final RowMapper<SpecialtyItem> SPECIALTY =
      (rs, n) -> new SpecialtyItem(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getBoolean(4), rs.getBoolean(5));
  private static final RowMapper<PlanItem> PLAN = (rs, n) -> new PlanItem(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4),
      rs.getString(5), rs.getLong(6), rs.getString(7), rs.getBoolean(8));
  private static final String PLAN_SELECT = """
      select p.id, p.code, p.name, e.id, e.name, r.id, r.name, p.active
      from eps_plans p
      join eps e on e.id = p.eps_id
      join insurance_regimes r on r.id = p.regime_id
      """;
  private static final String SPECIALTY_SELECT =
      "select id, name, appointment_duration_minutes, requires_admin_approval, active from specialties";

  private final JdbcTemplate jdbc;

  public CatalogService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // --- Catálogos fijos (HU-001): solo lectura, sin operaciones de escritura en la API.

  public List<CatalogItem> locations() {
    return jdbc.query("select id, code, name from locations where active = true order by name", CATALOG);
  }

  public List<CatalogItem> regimes() {
    return jdbc.query("select id, code, name from insurance_regimes order by name", CATALOG);
  }

  public List<CatalogItem> appointmentStatuses() {
    return jdbc.query("select id, code, name from appointment_statuses order by id", CATALOG);
  }

  public List<CatalogItem> rescheduleStatuses() {
    return jdbc.query("select id, code, name from reschedule_request_statuses order by id", CATALOG);
  }

  /** El esquema de referencia trae roles(code, name); la migración V1 solo roles(name) con el código. */
  public List<CatalogItem> roles() {
    return jdbc.queryForList("select * from roles order by id").stream()
        .map(row -> new CatalogItem(((Number) row.get("id")).longValue(),
            String.valueOf(row.containsKey("code") ? row.get("code") : row.get("name")),
            String.valueOf(row.get("name"))))
        .toList();
  }

  /** Planes elegibles para afiliación: plan y EPS activos (D-10). */
  public List<PlanItem> activePlans() {
    return jdbc.query(PLAN_SELECT + " where p.active = true and e.active = true order by e.name, p.name", PLAN);
  }

  public List<SpecialtyItem> activeSpecialties() {
    return jdbc.query(SPECIALTY_SELECT + " where active = true order by name", SPECIALTY);
  }

  // --- EPS (HU-008)

  public List<EpsItem> eps(Authentication auth) {
    requireRole(auth, Role.ADMIN);
    return jdbc.query("select id, code, name, active from eps order by name", EPS);
  }

  @Transactional
  public EpsItem createEps(EpsRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    String code = normalizeCode(request.code());
    if (count("select count(*) from eps where code = ?", code) > 0) throw conflict("Ya existe una EPS con ese código");
    jdbc.update("insert into eps(code, name, active) values (?, ?, ?)", code, request.name().trim(), request.active());
    return jdbc.queryForObject("select id, code, name, active from eps where code = ?", EPS, code);
  }

  @Transactional
  public EpsItem updateEps(long id, EpsRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    String code = normalizeCode(request.code());
    if (count("select count(*) from eps where code = ? and id <> ?", code, id) > 0) throw conflict("Ya existe una EPS con ese código");
    if (jdbc.update("update eps set code = ?, name = ?, active = ? where id = ?", code, request.name().trim(), request.active(), id) == 0) {
      throw notFound("EPS no encontrada");
    }
    return jdbc.queryForObject("select id, code, name, active from eps where id = ?", EPS, id);
  }

  /** D-11: borrado físico solo si nada la referencia; si tiene planes se ofrece desactivar. */
  @Transactional
  public void deleteEps(long id, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    if (count("select count(*) from eps where id = ?", id) == 0) throw notFound("EPS no encontrada");
    if (count("select count(*) from eps_plans where eps_id = ?", id) > 0) throw conflict("La EPS tiene planes asociados; desactívela en lugar de eliminarla");
    jdbc.update("delete from eps where id = ?", id);
  }

  // --- Planes (HU-008)

  public List<PlanItem> plans(Authentication auth) {
    requireRole(auth, Role.ADMIN);
    return jdbc.query(PLAN_SELECT + " order by e.name, p.name", PLAN);
  }

  @Transactional
  public PlanItem createPlan(PlanRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    requirePlanReferences(request);
    String code = normalizeCode(request.code());
    if (count("select count(*) from eps_plans where eps_id = ? and code = ?", request.epsId(), code) > 0) {
      throw conflict("La EPS ya tiene un plan con ese código");
    }
    jdbc.update("insert into eps_plans(eps_id, regime_id, code, name, active) values (?, ?, ?, ?, ?)",
        request.epsId(), request.regimeId(), code, request.name().trim(), request.active());
    return jdbc.queryForObject(PLAN_SELECT + " where p.eps_id = ? and p.code = ?", PLAN, request.epsId(), code);
  }

  @Transactional
  public PlanItem updatePlan(long id, PlanRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    requirePlanReferences(request);
    String code = normalizeCode(request.code());
    if (count("select count(*) from eps_plans where eps_id = ? and code = ? and id <> ?", request.epsId(), code, id) > 0) {
      throw conflict("La EPS ya tiene un plan con ese código");
    }
    if (jdbc.update("update eps_plans set eps_id = ?, regime_id = ?, code = ?, name = ?, active = ? where id = ?",
        request.epsId(), request.regimeId(), code, request.name().trim(), request.active(), id) == 0) {
      throw notFound("Plan no encontrado");
    }
    return jdbc.queryForObject(PLAN_SELECT + " where p.id = ?", PLAN, id);
  }

  /** D-11: un plan con afiliaciones no se borra físicamente. */
  @Transactional
  public void deletePlan(long id, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    if (count("select count(*) from eps_plans where id = ?", id) == 0) throw notFound("Plan no encontrado");
    if (count("select count(*) from user_insurance_affiliations where plan_id = ?", id) > 0) {
      throw conflict("El plan tiene afiliaciones; desactívelo en lugar de eliminarlo");
    }
    jdbc.update("delete from eps_plans where id = ?", id);
  }

  // --- Especialidades (HU-009, HU-012)

  public List<SpecialtyItem> specialties(Authentication auth) {
    requireRole(auth, Role.ADMIN);
    return jdbc.query(SPECIALTY_SELECT + " order by name", SPECIALTY);
  }

  @Transactional
  public SpecialtyItem createSpecialty(SpecialtyRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    SlotPolicy.requireValidDuration(request.durationMinutes());
    String name = request.name().trim();
    String code = specialtyCode(name);
    if (count("select count(*) from specialties where lower(name) = lower(?) or code = ?", name, code) > 0) {
      throw conflict("Ya existe una especialidad con ese nombre");
    }
    jdbc.update("insert into specialties(code, name, appointment_duration_minutes, is_general, requires_admin_approval, active) values (?, ?, ?, false, ?, ?)",
        code, name, request.durationMinutes(), request.requiresAdminApproval(), request.active());
    return jdbc.queryForObject(SPECIALTY_SELECT + " where code = ?", SPECIALTY, code);
  }

  /** La duración la gobierna la especialidad; las citas ya creadas conservan la suya (HU-012). */
  @Transactional
  public SpecialtyItem updateSpecialty(long id, SpecialtyRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    SlotPolicy.requireValidDuration(request.durationMinutes());
    String name = request.name().trim();
    if (count("select count(*) from specialties where lower(name) = lower(?) and id <> ?", name, id) > 0) {
      throw conflict("Ya existe una especialidad con ese nombre");
    }
    if (jdbc.update("update specialties set name = ?, appointment_duration_minutes = ?, requires_admin_approval = ?, active = ? where id = ?",
        name, request.durationMinutes(), request.requiresAdminApproval(), request.active(), id) == 0) {
      throw notFound("Especialidad no encontrada");
    }
    return jdbc.queryForObject(SPECIALTY_SELECT + " where id = ?", SPECIALTY, id);
  }

  /** D-11: una especialidad usada por citas o profesionales no se borra físicamente. */
  @Transactional
  public void deleteSpecialty(long id, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    if (count("select count(*) from specialties where id = ?", id) == 0) throw notFound("Especialidad no encontrada");
    if (count("select count(*) from appointments where specialty_id = ?", id) > 0
        || count("select count(*) from professional_specialties where specialty_id = ?", id) > 0) {
      throw conflict("La especialidad está en uso por citas o profesionales; desactívela en lugar de eliminarla");
    }
    jdbc.update("delete from specialties where id = ?", id);
  }

  private void requirePlanReferences(PlanRequest request) {
    if (count("select count(*) from eps where id = ?", request.epsId()) == 0) throw badRequest("La EPS no existe");
    if (count("select count(*) from insurance_regimes where id = ?", request.regimeId()) == 0) throw badRequest("El régimen no existe");
  }

  private int count(String sql, Object... args) {
    Integer value = jdbc.queryForObject(sql, Integer.class, args);
    return value == null ? 0 : value;
  }

  private static String normalizeCode(String code) {
    return code.trim().toUpperCase(Locale.ROOT);
  }

  private static String specialtyCode(String name) {
    String ascii = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    String code = ascii.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_").replaceAll("^_|_$", "");
    return code.length() > 40 ? code.substring(0, 40) : code;
  }
}
