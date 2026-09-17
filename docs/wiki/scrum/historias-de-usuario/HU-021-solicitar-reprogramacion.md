---
id: HU-021
tipo: historia-de-usuario
titulo: "Solicitar reprogramación"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Modelar solicitud/franja propuesta e integridad Flyway.** Dificultad: Alto.
- [ ] **T-02 — Implementar validación y retención atómica.** Dificultad: Alto.
- [ ] **T-03 — Crear flujo cliente y pruebas de elegibilidad/concurrencia.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Solicitud válida
**Dado** cita `APPROVED` futura **cuando** USER propone franja libre **entonces** queda reprogramación `PENDING` y se retiene la nueva franja.
### CA-02 — Original preservada
**Dado** reprogramación pendiente **cuando** se consulta la cita original **entonces** conserva su franja previa.
### CA-03 — Restricciones
**Dado** cita no aprobada/no futura o cambio de profesional **cuando** USER solicita **entonces** se deniega e indica que el cambio de profesional es nueva cita.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Flyway/3FN, retención, contrato/UI y pruebas de preservación verificadas.
- [ ] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- La representación de franja propuesta no debe duplicar atributos de catálogo.
