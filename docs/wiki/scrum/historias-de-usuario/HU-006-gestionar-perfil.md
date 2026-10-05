---
id: HU-006
tipo: historia-de-usuario
titulo: "Gestionar perfil"
estado: Lista
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
- [x] **T-01 — Delimitar campos editables y validarlos.** Dificultad: Medio.
- [x] **T-02 — Exponer consulta/actualización propia y vista cliente.** Dificultad: Medio.
- [x] **T-03 — Probar ownership y entradas inválidas.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Consulta propia
**Dado** un USER autenticado **cuando** consulta su perfil **entonces** visualiza sus datos permitidos.
### CA-02 — Actualización válida
**Dado** valores permitidos válidos **cuando** los actualiza **entonces** quedan disponibles en su consulta posterior.
### CA-03 — Protección de ownership
**Dado** un USER **cuando** intenta acceder o modificar perfil ajeno **entonces** se deniega.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Contrato REST y pruebas relevantes de backend/cliente verificados.
- [x] No se expone password ni se altera rol; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-006/pasos.md) · 01 | Datos personales visibles; documento y correo de solo lectura |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-006/pasos.md) · 01 | Teléfono válido persiste tras F5; inválido rechazado |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-006/pasos.md) · API | No existe ruta de perfil ajeno (404); el perfil sale del token |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-006/pasos.md)); DoD completo.

## Notas y decisiones
- Los campos editables requieren confirmación al aprobar la HU.
- Decisión aprobada 2026-10-04 · D-08: solo el teléfono es editable.
