---
id: HU-011
tipo: historia-de-usuario
titulo: "Asignar y habilitar profesional"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Modelar tablas puente y restricciones 3FN/Flyway.** Dificultad: Alto.
- [ ] **T-02 — Crear administración y validación de asignaciones.** Dificultad: Alto.
- [ ] **T-03 — Probar primaria, sedes y no elegibilidad.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Asignación múltiple
**Dado** un profesional **cuando** ADMIN asigna especialidades y sedes válidas **entonces** se conservan como relaciones separadas.
### CA-02 — Especialidad primaria
**Dado** varias especialidades **cuando** ADMIN designa una primaria **entonces** solo una queda marcada como primaria.
### CA-03 — Habilitación de agenda
**Dado** profesional no asignado a una sede o inactivo **cuando** intenta publicar/figurar disponible **entonces** no es elegible.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Flyway, restricciones N:M, autorización y pruebas pertinentes verificados.
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
- La política sobre desactivación con citas existentes queda pendiente de revisión.
