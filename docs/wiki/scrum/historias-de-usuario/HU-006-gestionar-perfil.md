---
id: HU-006
tipo: historia-de-usuario
titulo: "Gestionar perfil"
estado: Pendiente de aprobación
epica: "[[EP-002-perfil-y-catalogos]]"
esfuerzo: Medio
sprint_sugerido: S3
dependencias: ["[[HU-003-iniciar-sesion]]"]
relacionadas: ["[[HU-007-gestionar-afiliacion]]"]
---
# HU-006 — Gestionar perfil
## Historia de usuario
**COMO** USER autenticado **QUIERO** consultar y actualizar mis datos permitidos **PARA** mantener mi información de contacto vigente.
## Alcance
- Consulta del propio perfil y actualización de campos autorizados.
## Fuera de alcance
- Cambio de roles, acceso a perfil ajeno o cambio de contraseña por este flujo.
## Reglas de negocio
- Autorización por ownership y validación server-side.
## Dependencias y relaciones
- Épica: [[EP-002-perfil-y-catalogos]]. Depende de [[HU-003-iniciar-sesion]].
## Esfuerzo
**Nivel:** Medio. **Justificación:** CRUD limitado con ownership y validaciones.
## Tareas de desarrollo
- [ ] **T-01 — Delimitar campos editables y validarlos.** Dificultad: Medio.
- [ ] **T-02 — Exponer consulta/actualización propia y vista cliente.** Dificultad: Medio.
- [ ] **T-03 — Probar ownership y entradas inválidas.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Consulta propia
**Dado** un USER autenticado **cuando** consulta su perfil **entonces** visualiza sus datos permitidos.
### CA-02 — Actualización válida
**Dado** valores permitidos válidos **cuando** los actualiza **entonces** quedan disponibles en su consulta posterior.
### CA-03 — Protección de ownership
**Dado** un USER **cuando** intenta acceder o modificar perfil ajeno **entonces** se deniega.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Contrato REST y pruebas relevantes de backend/cliente verificados.
- [ ] No se expone password ni se altera rol; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Los campos editables requieren confirmación al aprobar la HU.
