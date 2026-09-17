---
id: HU-022
tipo: historia-de-usuario
titulo: "Decidir reprogramación"
estado: Pendiente de aprobación
epica: "[[EP-007-reprogramacion]]"
esfuerzo: Alto
sprint_sugerido: S5
dependencias: ["[[HU-021-solicitar-reprogramacion]]"]
relacionadas: ["[[HU-020-cancelar-cita]]"]
---
# HU-022 — Decidir reprogramación
## Historia de usuario
**COMO** ADMIN **QUIERO** aprobar o rechazar una reprogramación pendiente **PARA** conservar o reemplazar la cita de forma segura.
## Alcance
- Bandeja `PENDING`, decisión, motivo cuando corresponda, liberación de franja correcta.
## Fuera de alcance
- Cambio de profesional.
## Reglas de negocio
- Aprobar libera antiguos/asigna nuevos/actualiza cita; rechazar libera provisional y conserva original; rechazo permite conservar o cancelar.
## Dependencias y relaciones
- Épica: [[EP-007-reprogramacion]]. Depende de [[HU-021-solicitar-reprogramacion]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** operación multi-franja atómica y consecuencias de estado.
## Tareas de desarrollo
- [ ] **T-01 — Implementar decisión transaccional y auditoría.** Dificultad: Alto.
- [ ] **T-02 — Construir bandeja/filtros y validación de motivos.** Dificultad: Alto.
- [ ] **T-03 — Probar aprobación, rechazo, liberaciones y citas originales.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Aprobación segura
**Dado** `PENDING` **cuando** ADMIN aprueba **entonces** libera la franja antigua, asigna la nueva y actualiza cita.
### CA-02 — Rechazo preserva
**Dado** `PENDING` **cuando** ADMIN rechaza con motivo si corresponde **entonces** libera solo la nueva provisional y conserva original.
### CA-03 — Bandeja ADMIN
**Dado** ADMIN **cuando** consulta pendientes **entonces** puede filtrarlas por sede, profesional, especialidad y fecha.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Atomicidad, auditoría, autorización, contrato/UI y pruebas de ambos desenlaces verificadas.
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
- Acordar si el motivo de rechazo de reprogramación es obligatorio; PRD exige motivo “cuando corresponda”.
