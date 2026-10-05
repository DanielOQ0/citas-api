---
id: HU-018
tipo: historia-de-usuario
titulo: "Decidir cita especializada"
estado: Lista
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
- [x] **T-01 — Implementar bandeja/decisión transaccional y auditoría.** Dificultad: Alto.
- [x] **T-02 — Construir filtros/acción ADMIN y validación de motivo.** Dificultad: Alto.
- [x] **T-03 — Probar aprobación, rechazo, rol y liberación.** Dificultad: Alto.
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
- [x] CA-01 a CA-04 validados con evidencia.
- [x] Autorización, atomicidad, auditoría, contrato/UI y pruebas relevantes verificados.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-018/pasos.md) · 01 | Bandeja REQUESTED filtrable por los 4 criterios |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-018/pasos.md) · 03 | APPROVED conserva sus slots |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-018/pasos.md) · 03 | REJECTED con motivo y slots liberados |
| CA-04 | Verificado | [pasos](../evidencias/2026-10/HU-018/pasos.md) · 02 | Rechazo sin motivo bloqueado |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03, CA-04 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-018/pasos.md)); DoD completo.

## Notas y decisiones
- La bandeja de reprogramación es tratada por [[HU-022-decidir-reprogramacion]].
- Decisión aprobada 2026-10-04 · D-14: no se aprueba una solicitud con horario vencido; solo se decide lo que está en REQUESTED.
