---
id: HU-024
tipo: historia-de-usuario
titulo: "Cerrar atención"
estado: Lista
epica: "[[EP-008-agenda-profesional-y-cierre]]"
esfuerzo: Alto
sprint_sugerido: S5
dependencias: ["[[HU-023-consultar-agenda-propia]]"]
relacionadas: ["[[HU-025-consultar-historial-de-estados]]"]
---
# HU-024 — Cerrar atención
## Historia de usuario
**COMO** PROFESSIONAL **QUIERO** marcar una cita aplicable como `COMPLETED` o `NO_SHOW` **PARA** reflejar su desenlace operativo.
## Alcance
- Transición de cita propia pasada/aplicable y registro de historial.
## Fuera de alcance
- Diagnósticos, tratamientos y cierre de cita ajena/no aplicable.
## Reglas de negocio
- Solo PROFESSIONAL de la cita; estados permitidos `COMPLETED`/`NO_SHOW`; transición explícita y auditada.
## Dependencias y relaciones
- Épica: [[EP-008-agenda-profesional-y-cierre]]. Depende de [[HU-023-consultar-agenda-propia]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** reglas temporales, ownership y auditoría.
## Tareas de desarrollo
- [x] **T-01 — Definir transiciones válidas y persistir auditoría.** Dificultad: Alto.
- [x] **T-02 — Exponer cierre solo al profesional propietario.** Dificultad: Alto.
- [x] **T-03 — Probar estados, fecha/aplicabilidad y actor.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Cierre permitido
**Dado** cita propia pasada/aplicable **cuando** PROFESSIONAL la marca **entonces** cambia a `COMPLETED` o `NO_SHOW`.
### CA-02 — Protección
**Dado** cita ajena o no aplicable **cuando** intenta cerrarla **entonces** se deniega.
### CA-03 — Auditoría
**Dado** cierre exitoso **cuando** se consulta historial **entonces** incluye nuevo estado, actor, fuente y fecha/hora.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Reglas de transición, autorización, contrato/UI y pruebas verificadas.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-024/pasos.md) · 01/02 | COMPLETED y NO_SHOW en citas terminadas (fixture SQL) |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-024/pasos.md) · API | Futura → 409; ajena → 403; ya cerrada → 409 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-024/pasos.md) · E2E | Historial con estado, actor, fuente USER y fecha |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-024/pasos.md)); DoD completo.

## Notas y decisiones
- “Aplicable” requiere una definición de negocio al aprobar esta HU.
- Decisión aprobada 2026-10-04 · D-19: aplicable = propia, APPROVED y con fin pasado (Bogotá); fuente USER con el profesional como actor.
