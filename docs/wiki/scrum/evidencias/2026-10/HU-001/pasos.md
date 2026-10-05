# HU-001 — Cargar catálogos fijos · Evidencia E2E

Fecha: 2026-10-04 · Entorno: Docker dev (MySQL con `db.sql` + Flyway V2–V7) · Corrida `042347`.

| CA | Paso | Esperado | Obtenido |
|---|---|---|---|
| CA-01 | `GET /api/v1/catalogs/{roles,locations,appointment-statuses,reschedule-statuses,regimes}` | Valores del PRD y las dos sedes | roles `USER, PROFESSIONAL, ADMIN`; sedes `HIC, ICV`; estados de cita `REQUESTED, APPROVED, REJECTED, CANCELLED, COMPLETED, NO_SHOW`; reprogramación `PENDING, APPROVED, REJECTED, CANCELLED`; regímenes `CONTRIBUTIVO, ESPECIAL, EXCEPCION, PARTICULAR, SUBSIDIADO` |
| CA-01 | Selector de sede en la búsqueda del paciente | HIC e ICV visibles | Ver [HU-015/01-busqueda-60min-consecutivos.png](../HU-015/01-busqueda-60min-consecutivos.png) |
| CA-02 | `POST`, `PUT`, `DELETE /api/v1/catalogs/roles` con token ADMIN | No permitido | `405 Method Not Allowed` en los tres (cuerpo de error uniforme) |
| CA-03 | Claves foráneas hacia catálogos (`information_schema`) | Referencias por id, no texto | `appointments.status_id → appointment_statuses`, `appointment_status_history.status_id → appointment_statuses`, `user_roles.role_id → roles`, `eps_plans.regime_id → insurance_regimes`, `reschedule_requests.status_id → reschedule_request_statuses`, `*.location_id → locations` |
| DoD | Reconstrucción del entorno | `reset-db.ps1` → `db.sql` (init de MySQL) → Flyway baseline + V2–V7 | Verificado en un MySQL temporal desechable: "Successfully applied 6 migrations … now at version v7" y API iniciada (D-25) |

Pruebas automatizadas: `CatalogIntegrationTest.hu001FixedCatalogsAreExposedReadOnly`, `RoleAccessIntegrationTest.errorsKeepStatusAndUniformBody` (405 real en el contenedor servlet).
Decisión: Medicina General = `specialties.is_general` / `MEDICINA_GENERAL`; el filtro "tipo" usa `requiresAdminApproval` (D-06).
