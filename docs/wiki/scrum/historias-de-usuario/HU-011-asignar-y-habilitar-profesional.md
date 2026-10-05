---
id: HU-011
tipo: historia-de-usuario
titulo: "Asignar y habilitar profesional"
estado: Lista
epica: "[[EP-003-profesionales-y-asignaciones]]"
esfuerzo: Alto
sprint_sugerido: S3
dependencias: ["[[HU-010-registrar-profesional]]", "[[HU-009-gestionar-especialidades]]"]
relacionadas: ["[[HU-013-publicar-bloques-disponibilidad]]"]
---
# HU-011 — Asignar y habilitar profesional
## Historia de usuario
**COMO** ADMIN **QUIERO** asignar especialidades, especialidad primaria, sedes y estado a un profesional **PARA** habilitarlo para la oferta correcta.
## Alcance
- N:M especialidad/sede, primaria y activación/desactivación.
## Fuera de alcance
- Publicar bloques o duración elegida por profesional.
## Reglas de negocio
- Una o más especialidades, una primaria; una o ambas sedes; solo agenda en sede asignada.
## Dependencias y relaciones
- Épica: [[EP-003-profesionales-y-asignaciones]]. Depende de [[HU-010-registrar-profesional]] y [[HU-009-gestionar-especialidades]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** relaciones N:M e impacto directo en elegibilidad.
## Tareas de desarrollo
- [x] **T-01 — Modelar tablas puente y restricciones 3FN/Flyway.** Dificultad: Alto.
- [x] **T-02 — Crear administración y validación de asignaciones.** Dificultad: Alto.
- [x] **T-03 — Probar primaria, sedes y no elegibilidad.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Asignación múltiple
**Dado** un profesional **cuando** ADMIN asigna especialidades y sedes válidas **entonces** se conservan como relaciones separadas.
### CA-02 — Especialidad primaria
**Dado** varias especialidades **cuando** ADMIN designa una primaria **entonces** solo una queda marcada como primaria.
### CA-03 — Habilitación de agenda
**Dado** profesional no asignado a una sede o inactivo **cuando** intenta publicar/figurar disponible **entonces** no es elegible.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Flyway, restricciones N:M, autorización y pruebas pertinentes verificados.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-011/pasos.md) · 01 | Especialidades y sedes como relaciones separadas |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-011/pasos.md) · 01 | Una sola primaria; primaria fuera de la lista → 400 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-011/pasos.md) · 02 + HU-013 | Sede no asignada o profesional inactivo: no elegible |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-011/pasos.md)); DoD completo.

## Notas y decisiones
- La política sobre desactivación con citas existentes queda pendiente de revisión.
- Decisión aprobada 2026-10-04 · D-12: el inactivo deja de ofertarse, conserva sus citas y su agenda.
