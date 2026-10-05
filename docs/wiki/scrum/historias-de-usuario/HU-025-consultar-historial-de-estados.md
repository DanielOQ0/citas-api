---
id: HU-025
tipo: historia-de-usuario
titulo: "Consultar historial de estados"
estado: Lista
epica: "[[EP-009-auditoria-y-contrato-rest]]"
esfuerzo: Alto
sprint_sugerido: S6
dependencias: ["[[HU-016-reservar-cita-general]]", "[[HU-018-decidir-cita-especializada]]", "[[HU-020-cancelar-cita]]", "[[HU-022-decidir-reprogramacion]]", "[[HU-024-cerrar-atencion]]"]
relacionadas: []
---
# HU-025 — Consultar historial de estados
## Historia de usuario
**COMO** actor autorizado **QUIERO** consultar el historial de cambios de estado de una cita **PARA** verificar su trazabilidad.
## Alcance
- Registro/lectura de cita, estado nuevo, actor cuando exista, fuente, fecha/hora y motivo opcional.
## Fuera de alcance
- CRUD normal, edición o borrado de auditoría.
## Reglas de negocio
- Fuente `SYSTEM`, `USER` o `ADMIN`; transiciones explícitas; auditoría inmutable.
## Dependencias y relaciones
- Épica: [[EP-009-auditoria-y-contrato-rest]]. Depende de las HU listadas en metadata que cambian estado.
## Esfuerzo
**Nivel:** Alto. **Justificación:** transversal a todo el ciclo de vida y a controles de acceso.
## Tareas de desarrollo
- [x] **T-01 — Modelar registro inmutable y Flyway.** Dificultad: Alto.
- [x] **T-02 — Integrar producción del evento en cada transición.** Dificultad: Alto.
- [x] **T-03 — Exponer consulta autorizada y pruebas de integridad.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Contenido de auditoría
**Dado** cambio de estado **cuando** se completa **entonces** guarda cita, nuevo estado, actor si existe, fuente, fecha/hora y motivo opcional.
### CA-02 — Inmutabilidad
**Dado** un registro existente **cuando** se intenta editar/borrar como CRUD normal **entonces** no se permite.
### CA-03 — Acceso autorizado
**Dado** actor sin ownership/rol pertinente **cuando** consulta historial **entonces** se deniega.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia en todas las transiciones aplicables.
- [x] Migración, integridad, autorización y pruebas de auditoría verificadas.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-025/pasos.md) · 01/02 | Cita, estado, actor, fuente, fecha y motivo en cada transición |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-025/pasos.md) · API | PUT/PATCH/DELETE/POST sobre el historial → 405 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-025/pasos.md) · + HU-023 | Terceros → 403 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-025/pasos.md)); DoD completo.

## Notas y decisiones
- Determinar por rol qué actores pueden leer qué historial durante aprobación.
- Decisión aprobada 2026-10-04 · D-20: lectores paciente dueño, profesional de la cita y ADMIN; se muestra el nombre del actor; la reprogramación aprobada se audita.
