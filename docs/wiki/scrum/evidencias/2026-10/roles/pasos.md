# Evidencia E2E — Vistas atadas al rol (Fase 1, D-23)

Fecha: 2026-10-04 · Entorno: Docker dev (`localhost:4200` Angular, `localhost:8080` API, MySQL 8.4 con seed `db.sql`).
Herramienta: `playwright-cli` 0.1.22 (Chromium), viewport 1280×800, helpers en [`../e2e-helpers.sh`](../e2e-helpers.sh).
Cuentas seed: `admin@demo.invalid` (ADMIN), `andrea.ruiz@demo.invalid` (PROFESSIONAL), `paciente1@demo.invalid` (USER); clave de laboratorio según `database/reference/README_DB.md`.

## Cambios verificados

- Sin selector de desarrollo "Vista: Paciente/Médico/Administrador" ni usuarios mock en `citas-web`.
- Usuario y rol se restauran desde `GET /api/me` al arrancar (también tras F5); `localStorage` solo guarda tokens.
- Rutas lazy por rol con `canMatch`: cada rol solo descarga su chunk (`paciente-routes`, `profesional-routes`, `administrador-routes`).
- La API responde 403 si ADMIN o PROFESSIONAL usan endpoints de paciente; los errores conservan su código real (antes llegaban como 401).

## Pasos y resultados

| # | Rol | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|---|
| 1 | ADMIN | `login admin admin@demo.invalid` | Inicio `/administrador/solicitudes`, sin texto "Vista:" | `/administrador/solicitudes`; "Vista:" 0 coincidencias; menú: Solicitudes (1), Reprogramaciones (1), Profesionales y Catálogos | [01-admin-inicio-sin-selector.png](01-admin-inicio-sin-selector.png) |
| 2 | ADMIN | `go admin /paciente/inicio`, `/profesional/mi-agenda`, `/ruta-inexistente` | Vuelve al inicio ADMIN | Las tres → `/administrador/solicitudes` | — |
| 3 | ADMIN | `go admin /administrador/reprogramaciones` + `reload` | F5 conserva rol y ruta | `/administrador/reprogramaciones`, header "Admin Laboratorio · Administrador"; solo se cargó `administrador-routes` | [02-admin-f5-conserva-rol.png](02-admin-f5-conserva-rol.png) |
| 4 | ADMIN | `POST /api/v1/appointments`, `GET /api/v1/users/me/affiliation` con token ADMIN | 403 JSON | `403 {"status":403,"error":"Forbidden","message":"No autorizado",…}` en ambos | — |
| 5 | ADMIN | Clic en `#btn-logout`; luego `go /administrador/solicitudes` y `/paciente/inicio` | Login; sin tokens; rutas privadas → login | `/login`; tokens `false/false`; ambas → `/login` | — |
| 6 | PROFESSIONAL | `login prof andrea.ruiz@demo.invalid` | Inicio `/profesional/mi-agenda`, menú solo "Mi Agenda Asistencial" | Correcto; "Vista:" 0 coincidencias | [03-profesional-inicio.png](03-profesional-inicio.png) |
| 7 | PROFESSIONAL | `go prof /administrador/solicitudes`, `/paciente/mis-citas`, `reload` | Inicio propio; F5 conserva | Todas → `/profesional/mi-agenda`; solo se cargó `profesional-routes` | — |
| 8 | USER | `login pac1 paciente1@demo.invalid` | Inicio `/paciente/inicio`, menú de paciente con "Mi Perfil y Afiliación" | Correcto; contador real "Mis Citas" = 1 | [04-paciente-inicio.png](04-paciente-inicio.png) |
| 9 | USER | `go pac1 /administrador/solicitudes`, `/profesional/mi-agenda`, `/paciente/perfil` + `reload` | Ajenas → inicio; propia se conserva tras F5 | Ajenas → `/paciente/inicio`; `/paciente/perfil` se conserva; solo se cargó `paciente-routes` | — |
| 10 | API | USER → `/admin/appointments`, `/professional/appointments`; PROFESSIONAL → `/appointments` | 403 | 403 en los tres; USER → `/appointments` 200 | — |

Pruebas automatizadas asociadas: `citas-api` `RoleAccessIntegrationTest` (contenedor servlet real) y `citas-web` `session.service.spec.ts`, `auth.guard.spec.ts`.
