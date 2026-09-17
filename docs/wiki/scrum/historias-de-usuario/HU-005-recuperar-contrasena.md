---
id: HU-005
tipo: historia-de-usuario
titulo: "Recuperar contraseña"
estado: Pendiente de aprobación
epica: "[[EP-001-identidad-y-seguridad]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-002-registrar-usuario]]"]
relacionadas: ["[[HU-003-iniciar-sesion]]"]
---
# HU-005 — Recuperar contraseña
## Historia de usuario
**COMO** usuario registrado **QUIERO** solicitar y completar recuperación de contraseña **PARA** recuperar acceso sin exponer mi cuenta.
## Alcance
- Solicitud por email, token temporal de un uso y cambio de contraseña.
## Fuera de alcance
- SMTP obligatorio; en desarrollo puede haber respuesta/log controlado y seguro.
## Reglas de negocio
- Token temporal, de un uso; al cambiar password queda consumido/inválido; nunca exponer secretos.
## Dependencias y relaciones
- Épica: [[EP-001-identidad-y-seguridad]]. Depende de [[HU-002-registrar-usuario]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** flujo sensible con persistencia, expiración y cliente.
## Tareas de desarrollo
- [ ] **T-01 — Modelar token temporal de un uso.** Dificultad: Alto. Compatible con 3FN y Flyway.
- [ ] **T-02 — Implementar solicitud, cambio y entrega segura de desarrollo.** Dificultad: Alto.
- [ ] **T-03 — Crear vistas y pruebas de flujo, expiración y reuso.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Solicitud controlada
**Dado** un email registrado **cuando** solicita recuperación **entonces** se genera un token temporal sin revelar secretos.
### CA-02 — Cambio de un uso
**Dado** token vigente **cuando** define una contraseña válida **entonces** se actualiza con hash y el token se consume.
### CA-03 — Token no válido
**Dado** token vencido, consumido o inválido **cuando** intenta cambiar contraseña **entonces** la operación se rechaza.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Migración/persistencia, pruebas y manejo seguro de desarrollo verificados.
- [ ] No se almacenan ni registran token o password en claro; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Definir explícitamente la redacción segura para cuentas inexistentes.
