---
id: HU-010
tipo: historia-de-usuario
titulo: "Registrar profesional"
estado: Lista
epica: "[[EP-003-profesionales-y-asignaciones]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-001-cargar-catalogos-fijos]]", "[[HU-009-gestionar-especialidades]]"]
relacionadas: ["[[HU-011-asignar-y-habilitar-profesional]]"]
---
# HU-010 — Registrar profesional
## Historia de usuario
**COMO** ADMIN **QUIERO** crear una cuenta PROFESSIONAL con código y matrícula ficticios **PARA** incorporar un prestador de agenda.
## Alcance
- Identidad PROFESSIONAL, código y matrícula sintéticos y únicos.
## Fuera de alcance
- Auto-registro de profesional y datos reales.
## Reglas de negocio
- Solo ADMIN crea PROFESSIONAL; código y matrícula se validan como datos de laboratorio.
## Dependencias y relaciones
- Épica: [[EP-003-profesionales-y-asignaciones]]. Depende de [[HU-001-cargar-catalogos-fijos]] y [[HU-009-gestionar-especialidades]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** identidad segura y perfil especializado en capas coordinadas.
## Tareas de desarrollo
- [x] **T-01 — Modelar extensión profesional, claves y Flyway.** Dificultad: Alto.
- [x] **T-02 — Implementar alta ADMIN y formulario sintético.** Dificultad: Alto.
- [x] **T-03 — Probar unicidad, rol y datos prohibidos.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Alta por ADMIN
**Dado** ADMIN y datos ficticios válidos **cuando** registra profesional **entonces** se crea con rol PROFESSIONAL.
### CA-02 — Identificadores únicos
**Dado** código o matrícula existente **cuando** se intenta reutilizar **entonces** se rechaza.
### CA-03 — Restricción de actor
**Dado** actor no ADMIN **cuando** intenta registrar profesional **entonces** se deniega.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Migración y restricciones 3FN verificadas; no hay datos personales reales.
- [x] Cliente, contrato y pruebas por rol disponibles; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-010/pasos.md) · 01 | Profesional con rol PROFESSIONAL |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-010/pasos.md) · E2E | Código o matrícula repetidos → 409 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-010/pasos.md) · API | Actor no ADMIN → 403 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-010/pasos.md)); DoD completo.

## Notas y decisiones
- La activación y asignaciones se resuelven en la HU siguiente.
- Decisión aprobada 2026-10-04 · D-07: mismas validaciones de identidad que el registro.
