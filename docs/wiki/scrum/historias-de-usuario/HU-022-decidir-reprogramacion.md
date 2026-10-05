---
id: HU-022
tipo: historia-de-usuario
titulo: "Decidir reprogramación"
estado: Lista
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
- [x] **T-01 — Implementar decisión transaccional y auditoría.** Dificultad: Alto.
- [x] **T-02 — Construir bandeja/filtros y validación de motivos.** Dificultad: Alto.
- [x] **T-03 — Probar aprobación, rechazo, liberaciones y citas originales.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Aprobación segura
**Dado** `PENDING` **cuando** ADMIN aprueba **entonces** libera la franja antigua, asigna la nueva y actualiza cita.
### CA-02 — Rechazo preserva
**Dado** `PENDING` **cuando** ADMIN rechaza con motivo si corresponde **entonces** libera solo la nueva provisional y conserva original.
### CA-03 — Bandeja ADMIN
**Dado** ADMIN **cuando** consulta pendientes **entonces** puede filtrarlas por sede, profesional, especialidad y fecha.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Atomicidad, auditoría, autorización, contrato/UI y pruebas de ambos desenlaces verificadas.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-022/pasos.md) · 02 | Aprobar mueve la cita, libera la franja antigua y se audita |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-022/pasos.md) · 02/03/04 | Rechazar con motivo conserva la original; el paciente conserva o cancela |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-022/pasos.md) · 01 | Bandeja PENDING filtrable por los 4 criterios |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-022/pasos.md)); DoD completo.

## Notas y decisiones
- Acordar si el motivo de rechazo de reprogramación es obligatorio; PRD exige motivo “cuando corresponda”.
- Decisión aprobada 2026-10-04 · D-16: el motivo de rechazo es obligatorio.
- Decisión aprobada 2026-10-04 · D-17: el paciente responde con conservar (`KEEP_APPOINTMENT`) o cancelar.
