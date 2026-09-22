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
| CA-01 | Verificado | `citas-api/src/test/java/co/fcv/citas/AuthIntegrationTest.java`; `citas-web/src/app/services/auth.service.ts` | Login devuelve access/refresh separados y roles; Angular consume `/api/auth/login`. |
| CA-02 | Verificado | `AuthIntegrationTest.invalidCredentialsDoNotIssueTokens`; `login.ts` | Backend responde `401`; UI muestra mensaje genérico. |
| CA-03 / DoD | Verificado | `GET /api/me`, `authGuard.ts`, `npm run build` | Recurso privado exige Bearer; shell web redirige a login sin access token. |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-09-22 — Builder/Verifier cross-repo: marcada `Lista` tras pasar pruebas REST Maven y build Angular.
## Notas y decisiones
- El mecanismo de almacenamiento del token en cliente queda sujeto al framework y revisión de seguridad.
