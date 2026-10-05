---
id: HU-003
tipo: historia-de-usuario
titulo: "Iniciar sesión"
estado: Lista
epica: "[[EP-001-identidad-y-seguridad]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-002-registrar-usuario]]"]
relacionadas: ["[[HU-004-renovar-y-cerrar-sesion]]"]
---
# HU-003 — Iniciar sesión
## Historia de usuario
**COMO** usuario registrado **QUIERO** iniciar sesión con email y contraseña **PARA** acceder a mis capacidades según rol.
## Alcance
- Credenciales, emisión separada de access y refresh token, y contexto de rol.
## Fuera de alcance
- Login social y persistencia insegura de tokens.
## Reglas de negocio
- Credenciales inválidas no revelan información sensible; access es de corta duración; roles autorizan acceso.
## Dependencias y relaciones
- Épica: [[EP-001-identidad-y-seguridad]]. Depende de [[HU-002-registrar-usuario]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** cruza autenticación, autorización, contrato y sesión cliente.
## Tareas de desarrollo
- [x] **T-01 — Configurar autenticación y generación JWT separada.** Dificultad: Alto.
- [x] **T-02 — Definir respuesta segura y consumo cliente.** Dificultad: Medio.
- [x] **T-03 — Proteger rutas/capacidades por rol y probar fallos.** Dificultad: Alto.
## Criterios de aceptación
### CA-01 — Credenciales válidas
**Dado** una cuenta activa **cuando** envía email y contraseña correctos **entonces** obtiene tokens access y refresh diferenciados y su contexto de rol.
### CA-02 — Credenciales inválidas
**Dado** credenciales incorrectas **cuando** inicia sesión **entonces** se rechaza sin emitir tokens ni revelar cuál dato falló.
### CA-03 — Autorización
**Dado** un token válido **cuando** accede a una capacidad privada **entonces** solo se permite si su rol y ownership la autorizan.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Secretos proceden de entorno y no están versionados ni registrados.
- [x] Pruebas de autenticación/autorización relevantes y contrato cliente-backend verificados.
- [x] Trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-003/pasos.md) · claims + roles/ | Access/refresh distintos con rol; inicio por rol |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-003/pasos.md) · 01 | 401 genérico idéntico para correo o clave erróneos |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-003/pasos.md) · evidencias roles/ | 403 por rol/ownership; rutas ajenas redirigen al inicio del rol |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-09-22 — Builder/Verifier cross-repo: marcada `Lista` tras pasar pruebas REST Maven y build Angular.
- 2026-10-04 — Incluida en el plan de cierre para re-verificación por el cambio de sesión y vistas por rol ([[plan-cierre-2026-10]]).
- 2026-10-05 — Re-verificada tras el cambio de sesión y vistas por rol (`Lista` (cierre 2026-10)): CA-01, CA-02, CA-03 con evidencia ([pasos](../evidencias/2026-10/HU-003/pasos.md)).

## Notas y decisiones
- El mecanismo de almacenamiento del token en cliente queda sujeto al framework y revisión de seguridad.
- Decisión aprobada 2026-10-04 · D-23: el rol de la sesión se restaura desde `/api/me`; no existe selector de vista.
