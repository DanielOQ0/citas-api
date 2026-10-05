package co.fcv.citas.scheduling;

import static co.fcv.citas.scheduling.Access.badRequest;
import static co.fcv.citas.scheduling.Access.conflict;
import static co.fcv.citas.scheduling.Access.notFound;
import static co.fcv.citas.scheduling.Access.requireRole;

import co.fcv.citas.auth.Role;
import co.fcv.citas.auth.UserEntity;
import co.fcv.citas.auth.UserRepository;
import co.fcv.citas.scheduling.SchedulingDtos.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, asignaciones y habilitación de profesionales (HU-010, HU-011). */
@Service
public class ProfessionalService {
  private final JdbcTemplate jdbc;
  private final UserRepository users;
  private final PasswordEncoder encoder;

  public ProfessionalService(JdbcTemplate jdbc, UserRepository users, PasswordEncoder encoder) {
    this.jdbc = jdbc;
    this.users = users;
    this.encoder = encoder;
  }

  /** Catálogo público: profesionales activos, opcionalmente habilitados para una especialidad y/o sede. */
  public List<ProfessionalItem> catalog(Long specialtyId, Long locationId) {
    StringBuilder sql = new StringBuilder("""
        select p.id, concat(u.first_name, ' ', u.last_name), p.professional_code, p.active
        from professionals p join users u on u.id = p.user_id
        where p.active = true
        """);
    List<Object> args = new ArrayList<>();
    if (specialtyId != null) {
      sql.append(" and exists (select 1 from professional_specialties x where x.professional_id = p.id and x.specialty_id = ? and x.active = true)");
      args.add(specialtyId);
    }
    if (locationId != null) {
      sql.append(" and exists (select 1 from professional_locations pl where pl.professional_id = p.id and pl.location_id = ? and pl.active = true)");
      args.add(locationId);
    }
    return jdbc.query(sql + " order by u.first_name, u.last_name",
        (rs, n) -> new ProfessionalItem(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)), args.toArray());
  }

  public List<ProfessionalAdminItem> list(Authentication auth) {
    requireRole(auth, Role.ADMIN);
    return adminItems(null);
  }

  @Transactional
  public ProfessionalAdminItem create(ProfessionalRequest request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    String email = request.email().trim().toLowerCase(Locale.ROOT);
    if (users.existsByEmailIgnoreCase(email) || users.existsByDocumentNumber(request.documentNumber())) {
      throw conflict("Email o documento ya registrado");
    }
    String code = request.professionalCode().trim().toUpperCase(Locale.ROOT);
    String license = request.licenseNumber().trim().toUpperCase(Locale.ROOT);
    if (count("select count(*) from professionals where professional_code = ?", code) > 0) throw conflict("El código profesional ya existe");
    if (count("select count(*) from professionals where license_number = ?", license) > 0) throw conflict("La matrícula ya existe");
    UserEntity user = users.save(new UserEntity(request.firstName().trim(), request.lastName().trim(), request.documentType(),
        request.documentNumber(), email, request.phone(), encoder.encode(request.password()), Role.PROFESSIONAL));
    jdbc.update("insert into professionals(user_id, professional_code, license_number, active) values (?, ?, ?, true)", user.getId(), code, license);
    Long id = jdbc.queryForObject("select id from professionals where user_id = ?", Long.class, user.getId());
    return adminItems(id).getFirst();
  }

  /** Una o varias especialidades con una primaria y una o ambas sedes (RF-07). */
  @Transactional
  public void assign(long id, ProfessionalAssignments request, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    if (count("select count(*) from professionals where id = ?", id) == 0) throw notFound("Profesional no encontrado");
    List<Long> specialties = List.copyOf(new LinkedHashSet<>(request.specialtyIds()));
    List<Long> locations = List.copyOf(new LinkedHashSet<>(request.locationIds()));
    if (!specialties.contains(request.primarySpecialtyId())) throw badRequest("La especialidad primaria debe estar entre las asignadas");
    for (Long specialty : specialties) {
      if (count("select count(*) from specialties where id = ? and active = true", specialty) == 0) {
        throw badRequest("La especialidad " + specialty + " no existe o está inactiva");
      }
    }
    for (Long location : locations) {
      if (count("select count(*) from locations where id = ? and active = true", location) == 0) {
        throw badRequest("La sede " + location + " no existe o está inactiva");
      }
    }
    jdbc.update("delete from professional_specialties where professional_id = ?", id);
    for (Long specialty : specialties) {
      jdbc.update("insert into professional_specialties(professional_id, specialty_id, is_primary, active) values (?, ?, ?, true)",
          id, specialty, specialty.equals(request.primarySpecialtyId()));
    }
    jdbc.update("delete from professional_locations where professional_id = ?", id);
    for (Long location : locations) {
      jdbc.update("insert into professional_locations(professional_id, location_id, active) values (?, ?, true)", id, location);
    }
  }

  /** D-12: inactivo deja de ofertarse, pero conserva sus citas y su agenda. */
  public void setActive(long id, boolean active, Authentication auth) {
    requireRole(auth, Role.ADMIN);
    if (jdbc.update("update professionals set active = ? where id = ?", active, id) == 0) throw notFound("Profesional no encontrado");
  }

  /** Profesional asociado al usuario autenticado; con onlyActive exige que esté habilitado. */
  long professionalIdOf(Authentication auth, boolean onlyActive) {
    requireRole(auth, Role.PROFESSIONAL);
    List<Map<String, Object>> rows = jdbc.queryForList("select id, active from professionals where user_id = ?", Access.userId(auth));
    if (rows.isEmpty()) throw Access.forbidden();
    if (onlyActive && !isTrue(rows.getFirst().get("active"))) {
      throw Access.forbidden("El profesional está inactivo y no puede publicar disponibilidad");
    }
    return ((Number) rows.getFirst().get("id")).longValue();
  }

  private List<ProfessionalAdminItem> adminItems(Long onlyId) {
    String where = onlyId == null ? "" : " where p.id = ?";
    Object[] args = onlyId == null ? new Object[0] : new Object[] {onlyId};
    Map<Long, List<Long>> specialties = new HashMap<>();
    Map<Long, Long> primary = new HashMap<>();
    jdbc.query("select professional_id, specialty_id, is_primary from professional_specialties where active = true order by specialty_id", rs -> {
      long professional = rs.getLong(1);
      specialties.computeIfAbsent(professional, k -> new ArrayList<>()).add(rs.getLong(2));
      if (rs.getBoolean(3)) primary.put(professional, rs.getLong(2));
    });
    Map<Long, List<Long>> locations = new HashMap<>();
    jdbc.query("select professional_id, location_id from professional_locations where active = true order by location_id",
        rs -> { locations.computeIfAbsent(rs.getLong(1), k -> new ArrayList<>()).add(rs.getLong(2)); });
    return jdbc.query("""
        select p.id, concat(u.first_name, ' ', u.last_name), u.email, p.professional_code, p.license_number, p.active
        from professionals p join users u on u.id = p.user_id""" + where + " order by u.first_name, u.last_name",
        (rs, n) -> {
          long id = rs.getLong(1);
          return new ProfessionalAdminItem(id, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(6),
              specialties.getOrDefault(id, List.of()), primary.get(id), locations.getOrDefault(id, List.of()));
        }, args);
  }

  private int count(String sql, Object... args) {
    Integer value = jdbc.queryForObject(sql, Integer.class, args);
    return value == null ? 0 : value;
  }

  /** MySQL devuelve TINYINT(1) como Boolean o Integer según el driver; H2 como Boolean. */
  private static boolean isTrue(Object value) {
    return value instanceof Boolean b ? b : value instanceof Number n && n.intValue() != 0;
  }
}
