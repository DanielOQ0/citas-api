---
tipo: plan-de-cierre
estado: Aprobado por el usuario (2026-10-04)
alcance: HU-001 a HU-026
repositorios: [citas-api, citas-web]
---

# Plan de cierre de HU — Sistema de Citas FCV

Origen: entrevista de planificación con el usuario (2026-10-04). Cada decisión de la sección 2 fue aprobada explícitamente, una por una.

## 1. Objetivo y criterio de cierre

- Las 26 HU pasan a `Aprobada` (aprobación del usuario, 2026-10-04) y luego a `Lista` **solo** cuando cada CA y el DoD tengan evidencia: prueba backend (unitaria o de integración) + E2E exploratorio con `playwright-cli` contra el stack Docker (MySQL real).
- Una HU sin evidencia completa queda en `Aprobada` con su bloqueo explicado en "Notas y decisiones".
- Primera entrega, por prioridad del usuario: eliminar el selector de desarrollo "Vista: Paciente/Médico/Administrador" y atar las vistas al rol autenticado (Fase 1).

## 2. Decisiones aprobadas

| ID | Tema | Decisión |
|---|---|---|
| D-01 | Arquitectura | Extraer al dominio (Java puro, sin Spring/JDBC) las reglas que se tocan: transiciones de cita y reprogramación, y política de slots 30/60, con pruebas unitarias. El resto del servicio JDBC queda como deuda técnica documentada (puertos/adaptadores pendientes). |
| D-02 | Ciclo de HU | Todas aprobadas hoy; estado final `Lista` (mismo término que HU-003). |
| D-03 | E2E | Exploratorio con `playwright-cli`, sin suite versionada, contra el Docker de desarrollo. Datos propios con sufijo único por corrida. |
| D-04 | Evidencia | `docs/wiki/scrum/evidencias/2026-10/` en `citas-api`: PNG 1280×800 + `pasos.md` por HU, enlazados desde la tabla "Evidencia de validación" de cada HU. |
| D-05 | SSR/Express | Eliminar SSR, Express y `@google/genai` de `citas-web`: SPA pura (RESTRICCIONES: sin Express/BFF). |
| D-06 | HU-001 | Nuevo `GET /api/v1/catalogs/roles`. Medicina General = `is_general=TRUE` / código `MEDICINA_GENERAL`. El filtro "tipo general/especializada" usa `requiresAdminApproval`. |
| D-07 | HU-002 | Tipo de documento CC/CE/TI/PA; número 5–20 alfanumérico; teléfono 7–15 dígitos; clave ≥ 8. Bean Validation (400 con detalle por campo) y mensajes por campo en el formulario. |
| D-08 | HU-006 | Solo el teléfono es editable; nombre, email y documento se muestran en solo lectura. |
| D-09 | HU-007 | `PUT /users/me/affiliation` recibe `{epsId, insurancePlanId}`: 400 si el plan no es de esa EPS o está inactivo. UI en cascada EPS → plan, con régimen visible. Una afiliación vigente; las anteriores quedan con `is_current=false`. |
| D-10 | HU-008 | EPS o plan inactivo bloquea solo afiliaciones nuevas (400). Las existentes se conservan y se marcan "plan inactivo". Una EPS inactiva inhabilita sus planes. |
| D-11 | HU-008/009 | `DELETE` de EPS, plan y especialidad: 204 si no está referenciado; 409 "en uso: desactívelo" si lo está. La UI ofrece "Desactivar" ante el 409. |
| D-12 | HU-011 | Profesional inactivo: no se oferta (búsqueda, reserva, publicación de bloques), conserva sus citas y puede consultar su agenda y cerrar atenciones. |
| D-13 | HU-013 | Se permite un bloque para hoy si su inicio es futuro (hora de Bogotá). Editar o borrar responde 409 si algún slot está APPROVED, REQUESTED o retenido por una reprogramación PENDING. |
| D-14 | Retenciones | Sin expiración automática. ADMIN no puede **aprobar** una solicitud ni una reprogramación cuyo horario ya pasó (409); sí puede rechazarlas. |
| D-15 | HU-017 | El motivo del paciente en una cita especializada es opcional (máx. 500). |
| D-16 | HU-022 | El motivo de rechazo de una reprogramación es obligatorio, con diálogo propio por solicitud. |
| D-17 | HU-020/021/022 | Cancelar una cita pasa su reprogramación PENDING a `CANCELLED`. Solo una PENDING por cita. `AppointmentItem` incluye la última reprogramación. Tras un rechazo, el paciente conserva la cita (`patient_action_after_rejection=KEEP`) o la cancela (`CANCEL`). |
| D-18 | HU-023 | La agenda del profesional incluye solo `patientName` de sus propias citas. |
| D-19 | HU-024 | Aplicable = cita propia, APPROVED y con hora de fin pasada (Bogotá). Fuente de auditoría `USER` con el profesional como actor. El E2E usa un fixture SQL que mueve una cita de prueba al pasado. |
| D-20 | HU-025 | Lectores: paciente dueño, profesional de la cita y ADMIN (otros: 403). Se muestra el nombre del actor. Aprobar una reprogramación registra un evento APPROVED/ADMIN con motivo "Reprogramada: … → …". Sin operaciones de escritura (405). |
| D-21 | HU-026 | Contrato oficial en Markdown: `llm-wiki/wiki/contratos-rest.md`, verificado por pruebas de integración y E2E. |
| D-22 | UI nueva | Las pantallas nuevas reutilizan tokens y patrones existentes, sin pasar por Stitch; se registra en la wiki. |
| D-23 | Roles | Rol efectivo desde el backend (`/api/me`); con varios roles, precedencia ADMIN > PROFESSIONAL > USER. Rutas lazy por rol con `canMatch`. API 403 a ADMIN/PROFESSIONAL en endpoints de paciente. |
| D-24 | Git | Restaurar `.git` de `citas-web`. Commits locales trazables en `develop` (ambos repos). Push y merge a `main` solo con OK final del usuario. |
| D-25 | BD (HU-001) | "El ajuste más sencillo posible": `docker-compose.yml` monta `database/reference/db.sql` como init de MySQL, así `scripts/reset-db.ps1` reconstruye el entorno y Flyway aplica V2–V7 sobre su baseline. Verificado en un MySQL temporal. Las migraciones aplicadas no se tocan; que Flyway construya el esquema desde cero queda como deuda (V4 y V7 asumen el esquema de referencia). |

## 3. Línea base (diagnóstico del 2026-10-04)

- Stack Docker operativo; login real verificado por API para ADMIN, PROFESSIONAL y USER con las cuentas seed (clave común documentada en `database/reference/README_DB.md`; no se repite en evidencias).
- Selector "Vista": `header.ts` → `FcvDataService.switchUserRole()` reemplaza el usuario por mocks y el `roleGuard` confía en esa señal. Tras F5, nada restaura la sesión desde `/api/me` y el usuario vuelve al mock "Paciente". El contador del menú ADMIN sale de datos mock.
- La API no valida rol en endpoints de paciente (un ADMIN puede reservar).
- `citas-web` local sin `.git`; contenido idéntico a `origin/develop` (`dee0ac9`).
- Pruebas: 4 métodos de integración backend (H2 hasta V4); 2 specs triviales en el frontend; `validacion-s2-s4.md` declara más evidencia de la existente.
- Defectos backend: decisión ADMIN sin exigir `REQUESTED`; decisión de reprogramación sin exigir `PENDING`, sin auditoría, sin validar fecha futura ni unicidad PENDING, con `UPDATE…JOIN` no portable a H2; cancelar no libera la retención de su reprogramación; disponibilidad de 60 min sin verificar el slot consecutivo, con slots pasados, especialidades inactivas y sedes no asignadas; desfase horario API (UTC) vs MySQL (Bogotá); duración tomada de la especialidad y no de la cita; 500 en especialidad duplicada o inactiva.
- Huecos frontend: CRUD de EPS y planes, alta de profesional, edición y desactivación de especialidades, filtros de calendario y agenda, cierre de atención, solicitud de reprogramación, filtros y enlace de la bandeja de reprogramaciones, filtro por tipo, tipo de documento fijo en "CC", IDs en lugar de nombres.

## 4. Fases

### Fase 0 — Preparación

1. Restaurar `citas-web/.git` desde un clon fresco de `DanielOQ0/citas-web` (rama `develop`, `dee0ac9`) y verificar `git status` limpio (normalizando CRLF).
2. Línea base: `docker compose run --rm citas-api-dev mvn test`; en `citas-web`, `npm run build` y `npm test -- --watch=false`.
3. Instalar `playwright-cli` (`npm install -g @playwright/cli@latest`) y su navegador.
4. Registrar en las 26 HU: "2026-10-04 — Aprobada por el usuario para cierre".

### Fase 1 — Vistas atadas al rol (prioridad)

- **API:** exigir rol USER en reservar, mis citas, cancelar, reprogramar y afiliación (GET/PUT) → 403 para otros roles; pruebas de integración por rol.
- **Web:**
  - `SessionService`: usuario y rol solo desde login y `GET /api/me` (inicializador al arrancar, también tras F5). Eliminar `switchUserRole`, usuarios mock y datos mock de `FcvDataService` (queda solo para notificaciones).
  - Quitar el selector "Vista" del header.
  - Rutas lazy por rol (`paciente.routes.ts`, `profesional.routes.ts`, `administrador.routes.ts`) con `canMatch`; `''` y `**` redirigen al inicio del rol.
  - Interceptor: si falla el refresh, limpiar la sesión e ir a `/login`.
  - Sidebar: contadores reales (REQUESTED + PENDING), enlace a Reprogramaciones, pie sin "Sandbox H2".
  - Eliminar SSR/Express (D-05).
- **E2E** (`evidencias/2026-10/roles/`), para admin, profesional y paciente: no hay selector; el menú solo muestra lo del rol; una URL de otro rol redirige al inicio propio; F5 conserva el rol; logout; la API responde 403 cuando ADMIN intenta reservar.

### Fase 2 — Backend

1. **Dominio** (D-01): `AppointmentStatus`, `RescheduleStatus`, `SlotPolicy` + pruebas unitarias.
2. **Tiempo:** `Clock` America/Bogota en todas las reglas temporales; sin `current_timestamp` de BD en comparaciones de negocio.
3. **Errores uniformes:** `{status, message, fieldErrors[]}` mediante `@RestControllerAdvice`.
4. **Correcciones por HU:**
   - HU-001: `GET /catalogs/roles`.
   - HU-002: validaciones D-07.
   - HU-007: `epsId` validado (D-09).
   - HU-008: `DELETE` de EPS y plan (D-11); `PlanItem` con `code`, `epsId`, `regimeId`, `regimeName`.
   - HU-009: duplicado → 409; `DELETE` (D-11); especialidad inactiva → 400 y excluida de disponibilidad.
   - HU-011: validar IDs de asignación (400); `ProfessionalItem` con asignaciones; disponibilidad exige sede asignada activa; política D-12.
   - HU-012: duración real de la cita (`scheduled_end_at - scheduled_start_at`).
   - HU-013: D-13.
   - HU-015: dos slots consecutivos para 60 min; excluir slots pasados; nombre del profesional en la respuesta.
   - HU-016/017: rol USER; motivo opcional (D-15).
   - HU-018: solo `REQUESTED`, con bloqueo `for update`; no aprobar pasadas (D-14).
   - HU-019: `AppointmentItem` con nombres de sede, profesional y especialidad + resumen de reprogramación.
   - HU-020: cascada a la reprogramación PENDING (D-17).
   - HU-021: fecha futura; una PENDING por cita; respuesta enriquecida.
   - HU-022: solo PENDING; la cita sigue APPROVED y futura; SQL portable; evento de historial (D-20); `RescheduleItem` con nombres; `POST …/reschedule-requests/{rid}/keep`.
   - HU-023: `patientName` (D-18).
   - HU-024: `Clock`.
   - HU-025: `actorName`; 403 y 405.
5. **Pruebas de integración por HU** en H2, con SQL portable y `flyway.target` ajustado si hace falta.

**Cambios de contrato (regla cross-repo: contrato → backend → cliente → evidencia):**

- Nuevos:
  - `GET /api/v1/catalogs/roles`
  - `DELETE /api/v1/admin/eps/{id}`
  - `DELETE /api/v1/admin/plans/{id}`
  - `DELETE /api/v1/admin/specialties/{id}`
  - `POST /api/v1/appointments/{id}/reschedule-requests/{requestId}/keep`
- Modificados:
  - `PUT /users/me/affiliation` (+`epsId`)
  - `POST /appointments` (`reason` opcional)
  - `AppointmentItem` (+nombres, +`reschedule`, duración real)
  - `PlanItem`, `ProfessionalItem`, `RescheduleItem`, `AvailabilityItem` (+nombres)
  - `HistoryItem` (+`actorName`)
  - Agenda del profesional (+`patientName`)
  - Formato de error uniforme
  - 403 por rol en endpoints de paciente

### Fase 3 — Frontend por HU

| HU | Cambio en `citas-web` |
|---|---|
| HU-002 | Select CC/CE/TI/PA, validación y mensajes por campo, errores del backend visibles. |
| HU-004 | Redirección a `/login` si el refresh falla; logout real. |
| HU-006/007 | Perfil con datos en solo lectura + teléfono; afiliación en cascada EPS → plan, con régimen y marca de inactivo. |
| HU-008 | Nueva pantalla ADMIN "EPS y planes": listar, crear, editar, activar/desactivar, eliminar con manejo de 409. |
| HU-009/012 | Especialidades: editar nombre, duración 30/60, aprobación requerida y estado; eliminar con 409 → desactivar. |
| HU-010/011 | Formulario de alta de profesional; asignaciones precargadas; activar/desactivar. |
| HU-013/014 | Bloques con nombres de sede y filtros de fecha y sede. |
| HU-015/016/017 | Filtros sede/tipo/especialidad/profesional/fecha; tarjetas con nombre del profesional; motivo opcional. |
| HU-018 | Bandeja con nombres y diálogo de rechazo con motivo obligatorio. |
| HU-019/020/021 | Mis citas con los 7 campos, filtros de todos los estados, historial, cancelar y reprogramar (selección de franja disponible del mismo profesional), y conservar/cancelar tras un rechazo. |
| HU-022 | Bandeja de reprogramaciones con filtros, nombres y diálogo por solicitud; enlace en el menú. |
| HU-023/024 | Agenda día/semana/sede, nombre del paciente, botones Completada / No asistió en citas aplicables. |
| HU-025 | Historial con nombre del actor en las vistas de paciente, profesional y ADMIN. |

### Fase 4 — E2E exploratorio (`playwright-cli`)

Cuentas: `admin@demo.invalid`, `andrea.ruiz@demo.invalid` (Medicina General), `paciente1@demo.invalid` y datos creados por la corrida (`*.e2e.<run>@demo.invalid`). Se usan sesiones separadas (`-s=admin`, `-s=prof`, `-s=pac1`, `-s=pac2`) para los conflictos concurrentes.

| HU | Escenario E2E (CA cubiertos) |
|---|---|
| Roles | Por rol: sin selector, menú propio, URL ajena → inicio propio, F5 conserva rol, API 403. |
| HU-001 | Selects muestran HIC/ICV; API de catálogos (incluido roles); POST/PUT/DELETE sobre catálogos fijos → 405. |
| HU-002 | Registro de paciente nuevo; email o documento duplicado; teléfono inválido con error de campo. |
| HU-003 | Login por rol → inicio correcto; clave errónea → mensaje genérico. |
| HU-004 | Access inválido → refresh transparente; logout → el refresh anterior ya no renueva. |
| HU-005 | Solicitar → token de desarrollo → cambiar clave → login nuevo; reuso del token → rechazo. |
| HU-006/007 | Teléfono persiste tras F5; afiliación EPS → plan; `epsId` incongruente → 400. |
| HU-008 | Crear, editar y desactivar EPS y plan (desaparece del registro); eliminar libre → OK; eliminar en uso → 409 → desactivar. |
| HU-009/012 | Crear especialidad de 60 min; duplicado → error; inactiva no se oferta; PATCH como PROFESSIONAL → 403. |
| HU-010/011 | Alta de profesional; código o matrícula duplicados → error; asignar especialidades, primaria y sede; desactivar → fuera de la búsqueda. |
| HU-013/014 | Publicar bloque (slots de 30); solape → 409; pasado → 400; editar o borrar con cita → 409; aislamiento entre profesionales. |
| HU-015/016/017 | Búsqueda con 5 filtros; 60 min solo consecutivos; cita general APPROVED; conflicto entre dos pacientes; especializada REQUESTED retiene el slot. |
| HU-018 | Bandeja con 4 filtros; aprobar; rechazar sin motivo → bloqueado; con motivo → REJECTED y slot liberado. |
| HU-019/020 | 7 campos + motivo de rechazo; filtros; cancelar libera el slot; cita pasada o terminal no cancelable; historial ajeno → 403. |
| HU-021/022 | Reprogramar → PENDING con original intacta y nueva franja retenida; aprobar → cita movida y slot viejo libre; rechazar con motivo → original conservada; conservar o cancelar. |
| HU-023/024 | Agenda por día, semana y sede, solo APPROVED, con nombre del paciente; fixture SQL → COMPLETED y NO_SHOW; cita futura sin cierre. |
| HU-025/026 | Historial con actor, fuente, hora y motivo para los 3 lectores; tercero → 403; escritura → 405; contrato contrastado contra los endpoints. |

### Fase 5 — Documentación y cierre

- Cada HU: tabla de evidencia, DoD marcado, historial (`Aprobada` → `Lista`) y decisiones D-xx aplicadas.
- `validacion-final-2026-10.md` como resumen de cierre (se conserva `validacion-s2-s4.md`).
- Wiki: `contratos-rest.md` (oficial), `decisiones.md` (D-xx), `preguntas-abiertas.md` (resueltas), `riesgos.md` (deuda hexagonal), `sintesis.md`, `arquitectura.md`, `seguridad.md`, `index.md` y anexo en `log.md`.
- Commits trazables por fase en `develop` (ambos repos). Push y merge solo con OK final del usuario.

## 5. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| La BD de desarrollo acumula datos E2E. | Correos `*.e2e.<run>@demo.invalid` y fechas a +7..+10 días; reinicio opcional recargando `db.sql`. |
| H2 (pruebas) frente a MySQL (E2E). | SQL portable (sin `UPDATE…JOIN`); el E2E sobre MySQL cubre diferencias de dialecto. |
| El esquema de desarrollo viene de `db.sql` + baseline de Flyway. | Evitar migraciones nuevas; si alguna es inevitable, compatible con ambos o por proveedor. |
| Desfase horario. | `Clock` America/Bogota; los datos seed usan `CURDATE()` de MySQL (Bogotá). |
| Deuda de arquitectura hexagonal (D-01). | Documentada en `riesgos.md` con alcance pendiente. |
| Volumen de trabajo (26 HU). | Fases con commits; una HU bloqueada queda `Aprobada` con causa explícita. |
