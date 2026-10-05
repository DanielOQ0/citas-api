# Arquitectura

## HECHOS

- `citas-api` concentra negocio, persistencia, seguridad y API REST JSON. `citas-web` concentra la UI: Angular 21, SPA sin SSR ni Express (D-05).
- No existe Express ni BFF: el navegador consume directamente Spring Boot por REST; la URL del backend sale de `src/environments/environment.ts`.
- El entorno Docker de desarrollo arranca MySQL (con `database/reference/db.sql` como init, D-25), Spring Boot y Angular con `docker compose up -d`. Angular se publica en `localhost:4200`, la API en `localhost:8080` y la salud en `GET /actuator/health`.

Fuente: [restricciones técnicas](../raw/restricciones-tecnicas.md) y [README workspace](../raw/readme-workspace.md).

## `citas-api` (2026-10)

- `scheduling/domain`: Java puro, sin Spring ni JDBC (D-01).
  - `AppointmentStatus` y `RescheduleStatus`: estados y transiciones.
  - `AppointmentRules`: decisión, cancelación, cierre y reprogramación.
  - `SlotPolicy`: 30/60, slots consecutivos y bloques futuros alineados.
  - `DomainException`: el adaptador la traduce a 400 o 409.
- `scheduling`: casos de uso por área (`CatalogService`, `ProfessionalService`, `AvailabilityService`, `AppointmentService`, `RescheduleService`, `ProfileService`). La persistencia se hace con JDBC; `AppointmentReader` y `StatusHistory` son las proyecciones de lectura y la auditoría.
- `SchedulingController`, `AuthController` y `MeController` son adaptadores REST: traducen HTTP y delegan, sin reglas de negocio.
- `web/ApiExceptionHandler` define el formato de error único. `config/TimeConfig` define el `Clock` America/Bogota.
- Deuda: los casos de uso aún dependen de JDBC directamente (faltan puertos de salida y adaptadores separados).

## `citas-web` (2026-10)

- `SessionService` es la única fuente de usuario y rol (login o `/api/me` en `provideAppInitializer`). `authInterceptor` adjunta el token, renueva una vez y expira la sesión.
- Grupos de rutas por rol (`paciente.routes.ts`, `profesional.routes.ts`, `administrador.routes.ts`) con `canMatch`: cada rol solo descarga su chunk.
- `shared/` contiene el formato, el diálogo de motivo y el historial. `styles.css` define los patrones `fcv-*` sobre los tokens del diseño aprobado (D-22).

## Coordinación

Un cambio REST exige un plan previo; luego se actualizan contrato, backend y frontend, y se deja evidencia verificable en ambos repositorios (aplicado en el cierre: [plan](../../scrum/plan-cierre-2026-10.md)).
