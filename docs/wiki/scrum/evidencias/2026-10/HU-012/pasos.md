# HU-012 — Definir duración de especialidad · Evidencia E2E

Fecha: 2026-10-04/05 · Corrida `042347`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Selector de duración al crear o editar una especialidad | Solo 30 o 60 | Opciones "30 minutos \| 60 minutos"; `PATCH` con 45 → `400 "La duración debe ser 30 o 60 minutos"` | [../HU-009/01-especialidades-crud.png](../HU-009/01-especialidades-crud.png) |
| CA-02 | Especialidad de 60 min en búsqueda y reserva; Medicina General de 30 min | 1 slot (30) o 2 consecutivos (60) | La búsqueda de 60 min omite 11:30 y 16:30 (sin segundo slot); la solicitud de 60 min retiene 2 slots; la cita general de 30 min ocupa 1 | [../HU-015/01-busqueda-60min-consecutivos.png](../HU-015/01-busqueda-60min-consecutivos.png), [../HU-017/02-solicitud-registrada-franja-retenida.png](../HU-017/02-solicitud-registrada-franja-retenida.png) |
| CA-03 | `PATCH /api/v1/admin/specialties/{id}` con token PROFESSIONAL | El profesional no cambia la duración | `403`; la agenda del profesional no tiene ningún control de duración | — |

Las citas existentes conservan su duración real (`durationMinutes` se calcula de la propia cita). Pruebas: `CatalogIntegrationTest.hu012DurationIsThirtyOrSixtyAndOnlyAdminChangesIt`, `SlotPolicyTest`.
