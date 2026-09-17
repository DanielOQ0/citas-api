---
id: HU-018
tipo: historia-de-usuario
titulo: "Decidir cita especializada"
estado: Pendiente de aprobación
epica: "[[EP-006-cita-especializada-y-administracion]]"
esfuerzo: Alto
sprint_sugerido: S4
dependencias: ["[[HU-017-solicitar-cita-especializada]]"]
relacionadas: ["[[HU-019-consultar-mis-citas]]"]
---
# HU-018 — Decidir cita especializada
## Historia de usuario
**COMO** ADMIN **QUIERO** aprobar o rechazar solicitudes especializadas **PARA** controlar su confirmación y capacidad.
## Alcance
- Bandeja `REQUESTED`, filtros por sede/profesional/especialidad/fecha, decisión y motivo de rechazo.
## Fuera de alcance
- Decisión por PROFESSIONAL.
## Reglas de negocio
- Aprobar → `APPROVED`; rechazar → `REJECTED`, motivo obligatorio y slots liberados.
## Dependencias y relaciones
- Épica: [[EP-006-cita-especializada-y-administracion]]. Depende de [[HU-017-solicitar-cita-especializada]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** transición, liberación y controles ADMIN.
## Tareas de desarrollo
- [ ] **T-01 — Implementar bandeja/decisión transaccional y auditoría.** Dificultad: Alto.
- [ ] **T-02 — Construir filtros/acción ADMIN y validación de motivo.** Dificultad: Alto.
- [ ] **T-03 — Probar aprobación, rechazo, rol y liberación.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Bandeja filtrable
**Dado** ADMIN **cuando** consulta pendientes **entonces** ve `REQUESTED` filtrables por los cuatro criterios del PRD.
### CA-02 — Aprobación
**Dado** solicitud `REQUESTED` **cuando** ADMIN aprueba **entonces** pasa a `APPROVED` y conserva sus slots.
### CA-03 — Rechazo
**Dado** solicitud `REQUESTED` **cuando** ADMIN rechaza con motivo **entonces** queda `REJECTED`, registra motivo y libera slots.
### CA-04 — Motivo obligatorio
**Dado** rechazo sin motivo **cuando** ADMIN confirma **entonces** se rechaza la decisión.
## Definition of Done
- [ ] CA-01 a CA-04 validados con evidencia.
- [ ] Autorización, atomicidad, auditoría, contrato/UI y pruebas relevantes verificados.
- [ ] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| CA-04 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- La bandeja de reprogramación es tratada por [[HU-022-decidir-reprogramacion]].
