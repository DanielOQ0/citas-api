---
id: HU-013
tipo: historia-de-usuario
titulo: "Publicar bloques de disponibilidad"
estado: Pendiente de aprobación
epica: "[[EP-004-disponibilidad-profesional]]"
esfuerzo: Alto
sprint_sugerido: S3
dependencias: ["[[HU-011-asignar-y-habilitar-profesional]]", "[[HU-012-definir-duracion-especialidad]]"]
relacionadas: ["[[HU-015-consultar-disponibilidad]]"]
---
# HU-013 — Publicar bloques de disponibilidad
## Historia de usuario
**COMO** PROFESSIONAL **QUIERO** crear, editar o eliminar mis bloques futuros por sede **PARA** publicar cuándo puedo atender.
## Alcance
- Múltiples bloques/día, sede por bloque, discretización a slots de 30 minutos y calendario propio.
## Fuera de alcance
- Bloques pasados, solapados o modificación de bloque comprometido.
## Reglas de negocio
- No pasado, no solapamiento, sede asignada y no edición/eliminación con cita comprometida.
## Dependencias y relaciones
- Épica: [[EP-004-disponibilidad-profesional]]. Depende de [[HU-011-asignar-y-habilitar-profesional]] y [[HU-012-definir-duracion-especialidad]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** reglas temporales e integridad de reservas.
## Tareas de desarrollo
- [ ] **T-01 — Modelar bloque/slot e índices, mediante Flyway.** Dificultad: Alto.
- [ ] **T-02 — Implementar validaciones de fecha, sede, solape y compromiso.** Dificultad: Alto.
- [ ] **T-03 — Construir gestión/calendario y pruebas de conflictos.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Bloque futuro por sede válida
**Dado** profesional habilitado en una sede **cuando** publica bloque futuro **entonces** se discretiza en slots de 30 min.
### CA-02 — Conflictos rechazados
**Dado** bloque pasado o solapado **cuando** intenta guardarlo **entonces** se rechaza.
### CA-03 — Protección de compromiso
**Dado** bloque futuro con cita retenida/reservada **cuando** intenta editarlo o borrarlo **entonces** se impide.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Migración, índices de agenda y pruebas de solape/compromiso verificadas.
- [ ] Cliente, autorización por ownership y trazabilidad actualizados.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Estrategia de lock/constraint de slots se define durante diseño técnico, no en esta especificación.
