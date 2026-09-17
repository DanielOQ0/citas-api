---
id: HU-020
tipo: historia-de-usuario
titulo: "Cancelar cita"
estado: Pendiente de aprobación
epica: "[[EP-006-cita-especializada-y-administracion]]"
esfuerzo: Alto
sprint_sugerido: S4
dependencias: ["[[HU-019-consultar-mis-citas]]"]
relacionadas: ["[[HU-025-consultar-historial-de-estados]]"]
---
# HU-020 — Cancelar cita
## Historia de usuario
**COMO** USER **QUIERO** cancelar una cita futura no terminal **PARA** liberar su capacidad cuando ya no asistiré.
## Alcance
- Transición `CANCELLED`, liberación de slots y registro de historial.
## Fuera de alcance
- Reactivar directamente una cita cancelada.
## Reglas de negocio
- Solo propia, futura y no terminal; cancelación libera slots y es auditable.
## Dependencias y relaciones
- Épica: [[EP-006-cita-especializada-y-administracion]]. Depende de [[HU-019-consultar-mis-citas]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** transición con liberación y reglas de elegibilidad.
## Tareas de desarrollo
- [ ] **T-01 — Implementar transición atómica y liberación.** Dificultad: Alto.
- [ ] **T-02 — Ofrecer acción solo en cita elegible.** Dificultad: Medio.
- [ ] **T-03 — Probar pasado, terminal, ajena y nueva disponibilidad.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Cancelación válida
**Dado** cita propia futura no terminal **cuando** USER cancela **entonces** pasa a `CANCELLED` y se liberan sus slots.
### CA-02 — Protección
**Dado** cita pasada, terminal o ajena **cuando** USER intenta cancelar **entonces** se deniega.
### CA-03 — No reactivación
**Dado** cita cancelada **cuando** se intenta restaurar directamente **entonces** no se permite.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Auditoría, liberación, contrato/UI y pruebas de estados verificadas.
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
- Estados terminales concretos se deben consolidar en contrato.
