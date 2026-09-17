---
id: HU-025
tipo: historia-de-usuario
titulo: "Consultar historial de estados"
estado: Pendiente de aprobación
epica: "[[EP-009-auditoria-y-contrato-rest]]"
esfuerzo: Alto
sprint_sugerido: S6
dependencias: ["[[HU-016-reservar-cita-general]]", "[[HU-018-decidir-cita-especializada]]", "[[HU-020-cancelar-cita]]", "[[HU-022-decidir-reprogramacion]]", "[[HU-024-cerrar-atencion]]"]
relacionadas: []
---
# HU-025 — Consultar historial de estados
## Historia de usuario
**COMO** actor autorizado **QUIERO** consultar el historial de cambios de estado de una cita **PARA** verificar su trazabilidad.
## Alcance
- Registro/lectura de cita, estado nuevo, actor cuando exista, fuente, fecha/hora y motivo opcional.
## Fuera de alcance
- CRUD normal, edición o borrado de auditoría.
## Reglas de negocio
- Fuente `SYSTEM`, `USER` o `ADMIN`; transiciones explícitas; auditoría inmutable.
## Dependencias y relaciones
- Épica: [[EP-009-auditoria-y-contrato-rest]]. Depende de las HU listadas en metadata que cambian estado.
## Esfuerzo
**Nivel:** Alto. **Justificación:** transversal a todo el ciclo de vida y a controles de acceso.
## Tareas de desarrollo
- [ ] **T-01 — Modelar registro inmutable y Flyway.** Dificultad: Alto.
- [ ] **T-02 — Integrar producción del evento en cada transición.** Dificultad: Alto.
- [ ] **T-03 — Exponer consulta autorizada y pruebas de integridad.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Contenido de auditoría
**Dado** cambio de estado **cuando** se completa **entonces** guarda cita, nuevo estado, actor si existe, fuente, fecha/hora y motivo opcional.
### CA-02 — Inmutabilidad
**Dado** un registro existente **cuando** se intenta editar/borrar como CRUD normal **entonces** no se permite.
### CA-03 — Acceso autorizado
**Dado** actor sin ownership/rol pertinente **cuando** consulta historial **entonces** se deniega.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia en todas las transiciones aplicables.
- [ ] Migración, integridad, autorización y pruebas de auditoría verificadas.
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
- Determinar por rol qué actores pueden leer qué historial durante aprobación.
