---
id: HU-015
tipo: historia-de-usuario
titulo: "Consultar disponibilidad"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Implementar consulta con filtros, elegibilidad e índices.** Dificultad: Alto.
- [ ] **T-02 — Exponer respuesta REST y filtros cliente.** Dificultad: Medio.
- [ ] **T-03 — Probar slots libres, retenidos, 30/60 y asociaciones.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Filtros
**Dado** criterios de búsqueda válidos **cuando** USER filtra **entonces** obtiene horarios de sede/tipo/especialidad/profesional/fecha solicitados.
### CA-02 — Duración completa
**Dado** especialidad de 60 minutos **cuando** se muestran opciones **entonces** solo aparecen dos slots consecutivos disponibles.
### CA-03 — Elegibilidad y reserva
**Dado** profesional inactivo, no asociado o slot retenido/ocupado **cuando** se busca **entonces** no se ofrece como disponible.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Consultas/índices, contrato, UI y pruebas de concurrencia lógica verificadas.
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
- La consulta no garantiza la franja hasta confirmación de reserva.
