# HU-011 — Asignar y habilitar profesional · Evidencia E2E

Fecha: 2026-10-04/05 · Sesión `admin` · Profesional `E2E-042347`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Marcar Medicina General + "Especialidad E2E 042347" y la sede HIC; "Guardar asignaciones"; F5 | Relaciones separadas que persisten | "Asignaciones … guardadas."; tras F5 siguen marcadas; BD: 2 filas en `professional_specialties`, sede `1` en `professional_locations` | [01-asignaciones-guardadas.png](01-asignaciones-guardadas.png) |
| CA-02 | Marcar "Especialidad E2E" como primaria (radio) | Una sola primaria | BD: "2 especialidades, 1 primaria"; API con primaria fuera de la lista → `400 "La especialidad primaria debe estar entre las asignadas"` | mismo |
| CA-03 | Sin sede ICV asignada: publicar bloque en ICV | No elegible | `400 "La sede no está asignada al profesional"` | [../HU-013/01-bloques-publicados-y-rechazos.png](../HU-013/01-bloques-publicados-y-rechazos.png) |
| CA-03 | Desactivar al profesional | Deja de figurar y de publicar | Fuera de `/catalogs/professionals`; 0 franjas; publicar → `403 "El profesional está inactivo…"`; reservar → `400`; sigue consultando su agenda (`200`, D-12); al reactivar vuelven sus 11 franjas | [02-profesional-desactivado.png](02-profesional-desactivado.png) |

Pruebas automatizadas: `ProfessionalAgendaIntegrationTest.hu011AssignmentsKeepOnePrimaryAndControlEligibility`.
