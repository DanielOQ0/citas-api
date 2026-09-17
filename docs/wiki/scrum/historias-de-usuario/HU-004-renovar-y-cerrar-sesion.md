---
id: HU-004
tipo: historia-de-usuario
titulo: "Renovar y cerrar sesión"
estado: Pendiente de aprobación
epica: "[[EP-001-identidad-y-seguridad]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-003-iniciar-sesion]]"]
relacionadas: []
---
# HU-004 — Renovar y cerrar sesión
## Historia de usuario
**COMO** usuario autenticado **QUIERO** renovar mi acceso y cerrar sesión **PARA** mantener una sesión segura y controlable.
## Alcance
- Refresh con token válido y revocación/logout.
## Fuera de alcance
- Gestión de dispositivos o SSO.
## Reglas de negocio
- Refresh token y access token son distintos; logout revoca/inutiliza el refresh aplicable.
## Dependencias y relaciones
- Épica: [[EP-001-identidad-y-seguridad]]. Depende de [[HU-003-iniciar-sesion]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** ciclo de seguridad y sincronía cliente-backend.
## Tareas de desarrollo
- [ ] **T-01 — Modelar ciclo/estado de refresh revocable.** Dificultad: Alto.
- [ ] **T-02 — Exponer renovación y logout seguros.** Dificultad: Medio.
- [ ] **T-03 — Integrar expiración, renovación y salida en cliente.** Dificultad: Medio.
- [ ] **T-04 — Probar token revocado, expirado y repetido.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Renovación válida
**Dado** un refresh válido no revocado **cuando** vence o se renueva el access **entonces** se obtiene un nuevo access sin volver a enviar contraseña.
### CA-02 — Rechazo seguro
**Dado** refresh expirado, inválido o revocado **cuando** se usa **entonces** no se emite access.
### CA-03 — Logout
**Dado** sesión autenticada **cuando** el usuario cierra sesión **entonces** el refresh deja de permitir renovaciones posteriores.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Revocación persiste de modo compatible con el modelo 3FN y su migración Flyway.
- [ ] Pruebas de ciclo de token y manejo de cliente disponibles; sin token en logs.
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
- Acordar rotación de refresh si se adopta; no es requisito explícito del PRD.
