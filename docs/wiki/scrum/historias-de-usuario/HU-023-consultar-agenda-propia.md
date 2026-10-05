---
id: HU-023
tipo: historia-de-usuario
titulo: "Consultar agenda propia"
estado: Lista
epica: "[[EP-008-agenda-profesional-y-cierre]]"
esfuerzo: Medio
sprint_sugerido: S5
dependencias: ["[[HU-016-reservar-cita-general]]", "[[HU-018-decidir-cita-especializada]]"]
relacionadas: ["[[HU-024-cerrar-atencion]]"]
---
# HU-023 — Consultar agenda propia
## Historia de usuario
**COMO** PROFESSIONAL **QUIERO** consultar mis citas `APPROVED` por día, semana y sede **PARA** organizar mi atención.
## Alcance
- Agenda propia de citas aprobadas con filtros solicitados.
## Fuera de alcance
- Datos de pacientes fuera de citas propias y citas de otros profesionales.
## Reglas de negocio
- Solo citas propias en estado `APPROVED`; privacidad por ownership.
## Dependencias y relaciones
- Épica: [[EP-008-agenda-profesional-y-cierre]]. Depende de [[HU-016-reservar-cita-general]] y [[HU-018-decidir-cita-especializada]].
## Esfuerzo
**Nivel:** Medio. **Justificación:** consulta temporal con protección de datos.
## Tareas de desarrollo
- [x] **T-01 — Implementar proyección propia/filtros e índices.** Dificultad: Medio.
- [x] **T-02 — Crear agenda día/semana/sede cliente.** Dificultad: Medio.
- [x] **T-03 — Probar estados y aislamiento de profesional.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Filtros de agenda
**Dado** PROFESSIONAL autenticado **cuando** consulta día, semana o sede **entonces** ve sus citas `APPROVED` coincidentes.
### CA-02 — Privacidad
**Dado** PROFESSIONAL **cuando** intenta obtener agenda/cita de otro profesional **entonces** se deniega.
### CA-03 — Estado
**Dado** una cita no `APPROVED` **cuando** consulta la agenda **entonces** no figura en esta vista.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Contrato, UI, índices de consulta y pruebas de ownership verificadas.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-023/pasos.md) · 01 | APPROVED propias por día, semana y sede |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-023/pasos.md) · API | Agenda o historial de otro profesional → sin datos / 403 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-023/pasos.md) · API | Las citas no APPROVED no figuran |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-023/pasos.md)); DoD completo.

## Notas y decisiones
- La proyección debe limitarse a datos necesarios para sus citas.
- Decisión aprobada 2026-10-04 · D-18: la agenda expone solo `patientName` de las citas propias.
