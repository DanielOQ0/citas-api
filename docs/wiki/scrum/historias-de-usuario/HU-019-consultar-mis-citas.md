---
id: HU-019
tipo: historia-de-usuario
titulo: "Consultar mis citas"
estado: Pendiente de aprobación
epica: "[[EP-006-cita-especializada-y-administracion]]"
esfuerzo: Medio
sprint_sugerido: S4
dependencias: ["[[HU-016-reservar-cita-general]]", "[[HU-017-solicitar-cita-especializada]]"]
relacionadas: ["[[HU-020-cancelar-cita]]", "[[HU-021-solicitar-reprogramacion]]"]
---
# HU-019 — Consultar mis citas
## Historia de usuario
**COMO** USER **QUIERO** consultar y filtrar mis citas **PARA** conocer su estado y gestionar las futuras.
## Alcance
- Filtro por estado/fecha y detalle con sede, profesional, especialidad, fecha/hora, duración, estado y rechazo.
## Fuera de alcance
- Citas de otros usuarios.
## Reglas de negocio
- Ownership obligatorio; el motivo se muestra cuando existe.
## Dependencias y relaciones
- Épica: [[EP-006-cita-especializada-y-administracion]]. Depende de [[HU-016-reservar-cita-general]] y [[HU-017-solicitar-cita-especializada]].
## Esfuerzo
**Nivel:** Medio. **Justificación:** proyección segura de datos y filtros.
## Tareas de desarrollo
- [ ] **T-01 — Crear consulta propia y filtros.** Dificultad: Medio.
- [ ] **T-02 — Crear lista/detalle cliente con estados y motivos.** Dificultad: Medio.
- [ ] **T-03 — Probar ownership y campos requeridos.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Información mínima
**Dado** USER con citas **cuando** las consulta **entonces** cada una muestra los siete campos requeridos por PRD, incluido motivo si existe.
### CA-02 — Filtros
**Dado** criterios estado/fecha **cuando** filtra **entonces** solo aparecen sus coincidencias.
### CA-03 — Privacidad
**Dado** USER **cuando** solicita cita ajena **entonces** se deniega.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Contrato/UI y pruebas de ownership/filtros verificadas; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Ninguna.
