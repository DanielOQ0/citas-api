# HU-009 — Gestionar especialidades · Evidencia E2E

Fecha: 2026-10-04/05 · Sesión `admin` · Corrida `042347` · Especialidad sintética "Especialidad E2E 042347" (60 min, requiere aprobación).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Crear "Especialidad E2E 042347" (60 min) y "Temporal E2E 042347" (30 min); editar la temporal a 60 min; eliminarla sin uso | Queda disponible para asignación; edición y borrado libre | "…creada (60 min)", "…actualizada", "…eliminada"; luego se asigna al profesional E2E (HU-011) | [01-especialidades-crud.png](01-especialidades-crud.png) |
| CA-01 | Nombre repetido en mayúsculas | Unicidad | "Ya existe una especialidad con ese nombre" (`409`) | — |
| CA-02 | Eliminar "Especialidad E2E 042347" (asignada y con citas) | Se conserva y se ofrece desactivar | `409` → aviso "…está en uso por citas o profesionales: no se borra físicamente." → Desactivar → estado "Inactiva"; su cita APPROVED existente se conserva | [02-en-uso-ofrece-desactivar.png](02-en-uso-ofrece-desactivar.png) |
| CA-03 | Especialidad inactiva: catálogo del paciente, disponibilidad y `POST /appointments` | No elegible | Ausente de `/catalogs/specialties` y del selector; 0 franjas; reserva → `400 "La especialidad no existe o está inactiva"` | — |
| CA-03 | Especialidad no asociada al profesional (Cardiología Adulto con el profesional E2E) | No elegible | `400 "El profesional no está habilitado para esa sede y especialidad"` | — |

Pruebas automatizadas: `CatalogIntegrationTest.hu009SpecialtyCrudRules`, `catalogAdministrationIsAdminOnly`. Decisión D-11 (borrado con `409` si está en uso).
