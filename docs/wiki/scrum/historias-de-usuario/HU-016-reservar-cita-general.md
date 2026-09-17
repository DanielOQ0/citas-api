---
id: HU-016
tipo: historia-de-usuario
titulo: "Reservar cita general"
estado: Pendiente de aprobación
epica: "[[EP-005-busqueda-y-cita-general]]"
esfuerzo: Alto
sprint_sugerido: S3
dependencias: ["[[HU-015-consultar-disponibilidad]]"]
relacionadas: ["[[HU-019-consultar-mis-citas]]"]
---
# HU-016 — Reservar cita general
## Historia de usuario
**COMO** USER **QUIERO** confirmar una cita de Medicina General en un horario elegido **PARA** obtener atención aprobada automáticamente.
## Alcance
- Selección de profesional general y creación `APPROVED` si la franja sigue libre.
## Fuera de alcance
- Revisión ADMIN.
## Reglas de negocio
- RN-01, RN-02 y RN-05: comprobar disponibilidad actual y evitar doble reserva.
## Dependencias y relaciones
- Épica: [[EP-005-busqueda-y-cita-general]]. Depende de [[HU-015-consultar-disponibilidad]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** transacción de ocupación, estado y cliente.
## Tareas de desarrollo
- [ ] **T-01 — Modelar cita/ocupación e invariantes con Flyway.** Dificultad: Alto.
- [ ] **T-02 — Crear caso de uso atómico y respuesta de confirmación.** Dificultad: Alto.
- [ ] **T-03 — Implementar confirmación cliente y pruebas de doble intento.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Autoaprobación
**Dado** USER y franja libre de Medicina General **cuando** confirma **entonces** se crea cita `APPROVED` con profesional elegido.
### CA-02 — Franja ya tomada
**Dado** franja ocupada entre búsqueda y confirmación **cuando** confirma **entonces** no se crea segunda cita y recibe resultado comprensible.
### CA-03 — Duración
**Dado** duración aplicable **cuando** se reserva **entonces** se ocupan todos los slots consecutivos requeridos.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Flyway, control de concurrencia/índices, contrato, UI y pruebas de integración verificadas.
- [ ] Estado inicial auditado conforme a [[HU-025-consultar-historial-de-estados]] al estar disponible; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Medicina General debe quedar inequívoca en catálogo.
