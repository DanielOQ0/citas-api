---
id: HU-015
tipo: historia-de-usuario
titulo: "Consultar disponibilidad"
estado: Lista
epica: "[[EP-005-busqueda-y-cita-general]]"
esfuerzo: Alto
sprint_sugerido: S3
dependencias: ["[[HU-012-definir-duracion-especialidad]]", "[[HU-013-publicar-bloques-disponibilidad]]"]
relacionadas: ["[[HU-016-reservar-cita-general]]", "[[HU-017-solicitar-cita-especializada]]"]
---
# HU-015 — Consultar disponibilidad
## Historia de usuario
**COMO** USER **QUIERO** filtrar horarios por sede, tipo, especialidad, profesional y fecha **PARA** elegir una franja apta.
## Alcance
- Búsqueda de disponibilidad de citas generales/especializadas y duración completa.
## Fuera de alcance
- Crear una cita o revelar slots retenidos/ocupados.
## Reglas de negocio
- Solo especialidad activa/asociada y profesional habilitado; 60 min requiere slots consecutivos.
## Dependencias y relaciones
- Épica: [[EP-005-busqueda-y-cita-general]]. Depende de [[HU-012-definir-duracion-especialidad]] y [[HU-013-publicar-bloques-disponibilidad]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** filtros combinados e invariantes de agenda.
## Tareas de desarrollo
- [x] **T-01 — Implementar consulta con filtros, elegibilidad e índices.** Dificultad: Alto.
- [x] **T-02 — Exponer respuesta REST y filtros cliente.** Dificultad: Medio.
- [x] **T-03 — Probar slots libres, retenidos, 30/60 y asociaciones.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Filtros
**Dado** criterios de búsqueda válidos **cuando** USER filtra **entonces** obtiene horarios de sede/tipo/especialidad/profesional/fecha solicitados.
### CA-02 — Duración completa
**Dado** especialidad de 60 minutos **cuando** se muestran opciones **entonces** solo aparecen dos slots consecutivos disponibles.
### CA-03 — Elegibilidad y reserva
**Dado** profesional inactivo, no asociado o slot retenido/ocupado **cuando** se busca **entonces** no se ofrece como disponible.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Consultas/índices, contrato, UI y pruebas de concurrencia lógica verificadas.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-015/pasos.md) · 01 | Filtros sede, tipo, especialidad, profesional y fecha |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-015/pasos.md) · 01 | 60 min solo con dos slots consecutivos (sin 11:30 ni 16:30) |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-015/pasos.md) · + HU-009/HU-011 | Retenidos, ocupados, pasados, inactivos y no asociados excluidos |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-015/pasos.md)); DoD completo.

## Notas y decisiones
- La consulta no garantiza la franja hasta confirmación de reserva.
- Decisión aprobada 2026-10-04 · D-06: el filtro de tipo usa `requiresAdminApproval`.
