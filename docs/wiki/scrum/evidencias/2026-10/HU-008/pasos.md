# HU-008 — Gestionar EPS y planes · Evidencia E2E

Fecha: 2026-10-04 · Sesión `admin` (`admin@demo.invalid`) · Corrida `042347` · Datos sintéticos: EPS `E2E042347`, planes `P042347` y `S042347`.
Nota: las capturas de página completa muestran el menú lateral desplazado por su `position: fixed`; es un efecto de la captura, no de la interfaz.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Crear EPS "EPS E2E 042347"; crear planes (Contributivo y Subsidiado); editar el nombre del plan | Cambios válidos disponibles | "EPS … creada", "Plan … creado para EPS E2E 042347", "Plan "Plan E2E 042347 editado" actualizado." | [01-eps-y-planes-creados.png](01-eps-y-planes-creados.png) |
| CA-02 | Plan con un código ya usado en la misma EPS | Plan válido y único por EPS | "La EPS ya tiene un plan con ese código" (`409`) | mismo |
| CA-03 | Eliminar el plan temporal sin uso | Borrado físico | "Plan "Plan temporal 042347" eliminado." | — |
| CA-03 | Paciente se afilia al plan E2E; ADMIN intenta eliminarlo | No se borra; se ofrece desactivar | `409` → aviso ""…" tiene afiliaciones: no se borra físicamente." con botón Desactivar → "desactivado: no admite nuevas afiliaciones." | [02-plan-en-uso-ofrece-desactivar.png](02-plan-en-uso-ofrece-desactivar.png) |
| CA-03 | Eliminar la EPS con planes | No se borra; se desactiva | "…tiene planes asociados…" → Desactivar → EPS inactiva | [03-eps-y-plan-desactivados.png](03-eps-y-plan-desactivados.png) |
| D-10 | Efecto de inactivar | Bloquea solo afiliaciones nuevas | Plan ausente de `/catalogs/insurance-plans`; afiliación existente conservada con `active=false`; nueva afiliación → `400 "El plan o la EPS no están activos para nuevas afiliaciones"` | — |
| CA-04 | `GET /api/v1/admin/eps` con token USER; `POST /api/v1/admin/plans` con token PROFESSIONAL | Denegado | `403` en ambos | — |

Pruebas automatizadas: `CatalogIntegrationTest.hu008EpsAndPlansCrudProtectReferencedRows`, `catalogAdministrationIsAdminOnly`.
