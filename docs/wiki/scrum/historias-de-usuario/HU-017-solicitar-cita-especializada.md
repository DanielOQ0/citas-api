---
id: HU-017
tipo: historia-de-usuario
titulo: "Solicitar cita especializada"
estado: Pendiente de aprobación
epica: "[[EP-006-cita-especializada-y-administracion]]"
esfuerzo: Alto
sprint_sugerido: S4
dependencias: ["[[HU-015-consultar-disponibilidad]]"]
relacionadas: ["[[HU-018-decidir-cita-especializada]]"]
---
# HU-017 — Solicitar cita especializada
## Historia de usuario
**COMO** USER **QUIERO** solicitar una cita especializada con sede, profesional y horario **PARA** que ADMIN decida su aprobación.
## Alcance
- Crear solicitud `REQUESTED` y retener todos sus slots.
## Fuera de alcance
- Aprobarla automáticamente.
## Reglas de negocio
- Requiere especialidad activa/asociada y franja disponible; la retención evita doble reserva.
## Dependencias y relaciones
- Épica: [[EP-006-cita-especializada-y-administracion]]. Depende de [[HU-015-consultar-disponibilidad]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** reserva retenida y transición administrada.
## Tareas de desarrollo
- [ ] **T-01 — Extender modelo de cita/retención con Flyway.** Dificultad: Alto.
- [ ] **T-02 — Implementar solicitud atómica y confirmación cliente.** Dificultad: Alto.
- [ ] **T-03 — Probar ocupación, duración y doble intento.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Solicitud retenida
**Dado** una franja especializada libre **cuando** USER solicita **entonces** nace en `REQUESTED` y retiene la franja.
### CA-02 — No doble reserva
**Dado** franja retenida/ocupada **cuando** otro USER solicita **entonces** no se crea solicitud competidora.
### CA-03 — Elegibilidad
**Dado** especialidad/profesional/sede inválidos **cuando** se solicita **entonces** se rechaza.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Persistencia, concurrencia, contrato/UI y pruebas de integración verificados.
- [ ] Auditoría inicial trazable al habilitarse [[HU-025-consultar-historial-de-estados]].
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
