# Contratos REST

## Estado

**Contrato oficial** (D-21 del [plan de cierre](../../scrum/plan-cierre-2026-10.md)), vigente desde 2026-10-04 para HU-001 a HU-026. Cualquier cambio sigue la secuencia cross-repo: contrato → `citas-api` → `citas-web` → evidencia. Verificación: pruebas de integración de `citas-api` (`*IntegrationTest`) y E2E en [evidencias/2026-10](../../scrum/evidencias/2026-10/README.md).

## Convenciones

- Base URL local: `http://localhost:8080`; el cliente la toma de `src/environments/environment.ts` (override de laboratorio: `localStorage.fcv_api_url`). REST/JSON directo, sin BFF.
- Autenticación: `Authorization: Bearer <access JWT>`. Access y refresh son JWT separados (secretos y claim `type` distintos).
- Fechas `YYYY-MM-DD`, horas `HH:mm[:ss]`, fecha-hora ISO local. Zona de negocio `America/Bogota` (el backend usa un `Clock` de esa zona).
- CORS explícito al origen configurado en `FRONTEND_ORIGIN`.
- **Errores** (todos los códigos 4xx/5xx): `{"status","error","message","fieldErrors":[{"field","message"}],"path"}`.
  - `400` validación o regla de entrada (con `fieldErrors` si viene de Bean Validation).
  - `401` sin token o token inválido (el cliente intenta un refresh una sola vez).
  - `403` rol u ownership no autorizados.
  - `404` recurso inexistente.
  - `405` método no soportado (catálogos fijos de solo lectura).
  - `409` conflicto de estado, unicidad o franja tomada/retenida.

## Identidad (`/api/auth`, `/api/me`)

| Método | Ruta | Actor | Entrada | Respuesta | Errores |
|---|---|---|---|---|---|
| POST | `/api/auth/register` | público | `firstName`, `lastName`, `documentType` (CC/CE/TI/PA), `documentNumber` (5-20 alfanum.), `email`, `phone` (7-15 dígitos), `password` (≥ 8), `insurancePlanId?` | `201` `TokenResponse` (crea rol USER) | `400`, `409` email/documento |
| POST | `/api/auth/login` | público | `email`, `password` | `200` `{accessToken, refreshToken, expiresIn, user{id,name,email,roles[]}}` | `401` genérico |
| POST | `/api/auth/refresh` | público | `refreshToken` | `200` tokens rotados (el refresh anterior queda revocado) | `401` |
| POST | `/api/auth/logout` | público | `refreshToken` | `204` revoca el refresh | — |
| POST | `/api/auth/password-recovery` | público | `email` | `200` `{accepted, developmentToken?}`; token temporal (30 min) de un uso; `developmentToken` solo con `EXPOSE_RECOVERY_TOKEN=true` y también para cuentas inexistentes (no permite enumerar) | `400` |
| POST | `/api/auth/password-reset` | público | `token`, `password` (≥ 8) | `204`; consume el token | `400` token inválido/vencido/usado |
| GET | `/api/me` | autenticado | — | `200` `{id, name, email, roles[]}`: fuente del rol de la sesión en el cliente | `401` |

## Catálogos fijos (solo lectura, HU-001)

`GET /api/v1/catalogs/{locations|regimes|roles|appointment-statuses|reschedule-statuses}` → `[{id, code, name}]`. Públicos. `POST/PUT/PATCH/DELETE` → `405`.
Medicina General se identifica por `specialties.is_general = TRUE` (código `MEDICINA_GENERAL`); en el cliente, "tipo general/especializada" = `requiresAdminApproval`.

| Método | Ruta | Actor | Respuesta |
|---|---|---|---|
| GET | `/api/v1/catalogs/insurance-plans` | público | planes con plan y EPS activos: `[{id, code, name, epsId, epsName, regimeId, regimeName, active}]` |
| GET | `/api/v1/catalogs/specialties` | público | activas: `[{id, name, durationMinutes, requiresAdminApproval, active}]` |
| GET | `/api/v1/catalogs/professionals?specialtyId&locationId` | público | profesionales activos, opcionalmente habilitados para especialidad/sede: `[{id, name, professionalCode, active}]` |

## Administración (ADMIN; otros roles `403`)

| Método | Ruta | Entrada | Respuesta | Errores |
|---|---|---|---|---|
| GET/POST | `/api/v1/admin/eps` | `{code, name, active}` | `EpsItem` (`201` al crear) | `409` código |
| PATCH/DELETE | `/api/v1/admin/eps/{id}` | `{code, name, active}` | `EpsItem` / `204` | `404`; `409` si tiene planes (desactivar) |
| GET/POST | `/api/v1/admin/plans` | `{epsId, regimeId, code, name, active}` | `PlanItem` | `400` EPS/régimen inexistente; `409` código por EPS |
| PATCH/DELETE | `/api/v1/admin/plans/{id}` | ídem | `PlanItem` / `204` | `409` si tiene afiliaciones (desactivar) |
| GET/POST | `/api/v1/admin/specialties` | `{name, durationMinutes (30/60), requiresAdminApproval, active}` | `SpecialtyItem` | `400` duración; `409` nombre |
| PATCH/DELETE | `/api/v1/admin/specialties/{id}` | ídem | `SpecialtyItem` / `204` | `409` si la usan citas o profesionales (desactivar) |
| GET/POST | `/api/v1/admin/professionals` | `{firstName, lastName, documentType, documentNumber, email, phone, password, professionalCode, licenseNumber}` | `[{id, name, email, professionalCode, licenseNumber, active, specialtyIds[], primarySpecialtyId, locationIds[]}]` | `409` email/documento/código/matrícula |
| PUT | `/api/v1/admin/professionals/{id}/assignments` | `{specialtyIds[], primarySpecialtyId, locationIds[]}` | `204` | `400` primaria fuera de la lista o IDs inexistentes/inactivos; `404` |
| PATCH | `/api/v1/admin/professionals/{id}/active?active=` | — | `204`; inactivo deja de ofertarse, conserva citas y agenda | `404` |
| GET | `/api/v1/admin/appointments?locationId&professionalId&specialtyId&date` | — | `AppointmentItem[]` en `REQUESTED` | — |
| POST | `/api/v1/admin/appointments/{id}/decision` | `{decision: APPROVE\|REJECT, reason}` | `AppointmentItem` | `400` rechazo sin motivo; `409` no `REQUESTED` o aprobar con horario pasado; `404` |
| GET | `/api/v1/admin/reschedule-requests?locationId&professionalId&specialtyId&date` | — | `RescheduleItem[]` en `PENDING` | — |
| POST | `/api/v1/admin/reschedule-requests/{id}/decision` | `{decision, reason}` | `RescheduleItem`; aprobar mueve la cita y se audita | `400` rechazo sin motivo; `409` no `PENDING`, cita no aprobada, horario pasado o franja no disponible |

## Profesional (PROFESSIONAL; solo recursos propios)

| Método | Ruta | Entrada | Respuesta | Errores |
|---|---|---|---|---|
| GET | `/api/v1/professional/availability-blocks?date&locationId` | — | `[{id, locationId, locationName, date, startTime, endTime, slots, committedSlots}]` | `403` otro rol |
| POST | `/api/v1/professional/availability-blocks` | `{locationId, date, startTime, endTime}` | `201` bloque (slots de 30 min) | `400` pasado, no alineado o sede no asignada; `403` inactivo; `409` solape |
| PATCH/DELETE | `/api/v1/professional/availability-blocks/{id}` | ídem / — | bloque / `204` | `404` ajeno; `409` slot reservado, retenido o con reprogramación PENDING |
| GET | `/api/v1/professional/appointments?from&to&locationId` | — | `AppointmentItem[]` solo `APPROVED` propias (incluye `patientName`) | `403` |
| POST | `/api/v1/professional/appointments/{id}/close` | `{status: COMPLETED\|NO_SHOW, reason?}` | `AppointmentItem` | `403` ajena; `409` no `APPROVED` o aún no termina |

## Paciente (USER; ADMIN/PROFESSIONAL reciben `403`)

| Método | Ruta | Entrada | Respuesta | Errores |
|---|---|---|---|---|
| GET | `/api/v1/availability?locationId&specialtyId&date&professionalId?` | público | `[{professionalId, professionalName, locationId, specialtyId, date, startTime, durationMinutes}]`: solo franjas futuras que completan la duración (60 min = 2 slots consecutivos) de especialidad activa y profesional habilitado | — |
| POST | `/api/v1/appointments` | `{professionalId, locationId, specialtyId, date, startTime, reason?}` | `201` `AppointmentItem`: `APPROVED` (general, auditado por SYSTEM) o `REQUESTED` (especializada, retiene slots) | `400` pasado/no habilitado/especialidad inactiva; `409` franja tomada o retenida |
| GET | `/api/v1/appointments?status&from&to` | — | `AppointmentItem[]` propias | `400` estado desconocido |
| POST | `/api/v1/appointments/{id}/cancel` | — | `204`; libera slots y cancela su reprogramación PENDING | `403` ajena; `409` pasada o terminal |
| GET | `/api/v1/appointments/{id}/history` | paciente dueño, profesional de la cita o ADMIN | `[{status, actorId, actorName, source (SYSTEM/USER/ADMIN), reason, occurredAt}]` | `403`; `404` |
| POST | `/api/v1/appointments/{id}/reschedule-requests` | `{date, startTime}` | `201` `RescheduleItem` `PENDING` (retiene la nueva franja; la original se conserva) | `400` franja pasada o igual; `403`; `409` cita no `APPROVED`/no futura, ya hay una PENDING o franja no disponible |
| POST | `/api/v1/appointments/{id}/reschedule-requests/{requestId}/keep` | — | `RescheduleItem` con `patientAction = KEEP_APPOINTMENT` | `403`; `409` si no está rechazada o ya respondida |
| GET/PATCH | `/api/v1/users/me` | autenticado; PATCH `{phone}` (7-15 dígitos) | `{id, name, firstName, lastName, email, documentType, documentNumber, phone}` | `400` |
| GET/PUT | `/api/v1/users/me/affiliation` | PUT `{epsId, insurancePlanId}` | `{id, epsId, epsName, planId, planName, regimeName, active}` o `null` | `400` plan de otra EPS, inexistente o inactivo |

### Recursos compartidos

- `AppointmentItem`: `{id, status, patientName, professionalId, professionalName, locationId, locationName, specialtyId, specialtyName, requiresAdminApproval, date, startTime, endTime, durationMinutes, reason, rejectionReason, reschedule}`. `durationMinutes` es la duración real de la cita. `reschedule` es la última solicitud: `{id, status, requestedDate, requestedStart, decisionReason, patientAction}`.
- `RescheduleItem`: `{id, appointmentId, status, patientName, professionalId, professionalName, specialtyId, specialtyName, locationId, locationName, previousDate, previousStart, requestedDate, requestedStart, durationMinutes, decisionReason, patientAction}`.

Fuente: [PRD, RF-01 a RF-20](../raw/prd-v1.0.md), [restricciones](../raw/restricciones-tecnicas.md), HU aprobadas en `docs/wiki/scrum/`.
