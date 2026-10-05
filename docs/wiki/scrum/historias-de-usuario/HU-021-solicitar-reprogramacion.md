---
id: HU-021
tipo: historia-de-usuario
titulo: "Solicitar reprogramación"
estado: Lista
epica: "[[EP-007-reprogramacion]]"
esfuerzo: Alto
sprint_sugerido: S5
dependencias: ["[[HU-019-consultar-mis-citas]]", "[[HU-015-consultar-disponibilidad]]"]
relacionadas: ["[[HU-022-decidir-reprogramacion]]"]
---
# HU-021 — Solicitar reprogramación
## Historia de usuario
**COMO** USER **QUIERO** proponer fecha/hora nueva para una cita aprobada **PARA** cambiarla sin perder la franja original mientras se decide.
## Alcance
- Solicitud `PENDING`, misma especialidad/profesional y retención de nueva franja.
## Fuera de alcance
- Cambio de profesional; crea una cita nueva.
## Reglas de negocio
- Solo `APPROVED` futura; original permanece intacta; nueva franja se retiene.
## Dependencias y relaciones
- Épica: [[EP-007-reprogramacion]]. Depende de [[HU-019-consultar-mis-citas]] y [[HU-015-consultar-disponibilidad]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** dos franjas y preservación de estado original.
## Tareas de desarrollo
- [x] **T-01 — Modelar solicitud/franja propuesta e integridad Flyway.** Dificultad: Alto.
- [x] **T-02 — Implementar validación y retención atómica.** Dificultad: Alto.
- [x] **T-03 — Crear flujo cliente y pruebas de elegibilidad/concurrencia.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Solicitud válida
**Dado** cita `APPROVED` futura **cuando** USER propone franja libre **entonces** queda reprogramación `PENDING` y se retiene la nueva franja.
### CA-02 — Original preservada
**Dado** reprogramación pendiente **cuando** se consulta la cita original **entonces** conserva su franja previa.
### CA-03 — Restricciones
**Dado** cita no aprobada/no futura o cambio de profesional **cuando** USER solicita **entonces** se deniega e indica que el cambio de profesional es nueva cita.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Flyway/3FN, retención, contrato/UI y pruebas de preservación verificadas.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-021/pasos.md) · 01 | PENDING y nueva franja retenida |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-021/pasos.md) · 02 | La cita original conserva su franja |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-021/pasos.md) · API | No aprobada/no futura o segunda solicitud → 409; cambiar profesional = cita nueva |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-021/pasos.md)); DoD completo.

## Notas y decisiones
- La representación de franja propuesta no debe duplicar atributos de catálogo.
- Decisión aprobada 2026-10-04 · D-17: una reprogramación PENDING por cita; se conserva profesional, especialidad y sede.
- Decisión aprobada 2026-10-04 · La solicitud no lleva motivo del paciente (no lo exige la HU y el modelo no tiene columna para él).
