---
id: HU-019
tipo: historia-de-usuario
titulo: "Consultar mis citas"
estado: Lista
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
- [x] **T-01 — Crear consulta propia y filtros.** Dificultad: Medio.
- [x] **T-02 — Crear lista/detalle cliente con estados y motivos.** Dificultad: Medio.
- [x] **T-03 — Probar ownership y campos requeridos.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Información mínima
**Dado** USER con citas **cuando** las consulta **entonces** cada una muestra los siete campos requeridos por PRD, incluido motivo si existe.
### CA-02 — Filtros
**Dado** criterios estado/fecha **cuando** filtra **entonces** solo aparecen sus coincidencias.
### CA-03 — Privacidad
**Dado** USER **cuando** solicita cita ajena **entonces** se deniega.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Contrato/UI y pruebas de ownership/filtros verificadas; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-019/pasos.md) · 01/02 | Siete campos y motivo de rechazo |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-019/pasos.md) · 02 | Filtros por estado y rango de fechas |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-019/pasos.md) · API | Cita ajena → 403 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-019/pasos.md)); DoD completo.

## Notas y decisiones
- Ninguna.
