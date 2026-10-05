---
id: HU-017
tipo: historia-de-usuario
titulo: "Solicitar cita especializada"
estado: Lista
epica: "[[EP-006-cita-especializada-y-administracion]]"
esfuerzo: Alto
sprint_sugerido: S4
dependencias: ["[[HU-015-consultar-disponibilidad]]"]
relacionadas: ["[[HU-018-decidir-cita-especializada]]"]
---
# HU-017 — Solicitar cita especializada
## Historia de usuario
**COMO** USER **QUIERO** solicitar una cita especializada con sede, profesional y horario **PARA** que ADMIN decida su aprobación.
## Alcance
- Crear solicitud `REQUESTED` y retener todos sus slots.
## Fuera de alcance
- Aprobarla automáticamente.
## Reglas de negocio
- Requiere especialidad activa/asociada y franja disponible; la retención evita doble reserva.
## Dependencias y relaciones
- Épica: [[EP-006-cita-especializada-y-administracion]]. Depende de [[HU-015-consultar-disponibilidad]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** reserva retenida y transición administrada.
## Tareas de desarrollo
- [x] **T-01 — Extender modelo de cita/retención con Flyway.** Dificultad: Alto.
- [x] **T-02 — Implementar solicitud atómica y confirmación cliente.** Dificultad: Alto.
- [x] **T-03 — Probar ocupación, duración y doble intento.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Solicitud retenida
**Dado** una franja especializada libre **cuando** USER solicita **entonces** nace en `REQUESTED` y retiene la franja.
### CA-02 — No doble reserva
**Dado** franja retenida/ocupada **cuando** otro USER solicita **entonces** no se crea solicitud competidora.
### CA-03 — Elegibilidad
**Dado** especialidad/profesional/sede inválidos **cuando** se solicita **entonces** se rechaza.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Persistencia, concurrencia, contrato/UI y pruebas de integración verificados.
- [x] Auditoría inicial trazable al habilitarse [[HU-025-consultar-historial-de-estados]].
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-017/pasos.md) · 01/02 | REQUESTED con dos slots retenidos |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-017/pasos.md) · API | Solicitud competidora → 409 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-017/pasos.md) · API | Sede/especialidad no habilitada → 400 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-017/pasos.md)); DoD completo.

## Notas y decisiones
- Ninguna.
- Decisión aprobada 2026-10-04 · D-15: el motivo del paciente es opcional.
- Decisión aprobada 2026-10-04 · D-14: las retenciones no expiran automáticamente.
