# Seguridad

## HECHOS

- Las contraseñas usan hash adaptativo compatible con Spring Security (BCrypt).
- Access token y refresh token JWT están separados (secretos y claim `type` distintos).
- Los secretos solo proceden de variables de entorno o `.env`; nunca se versionan.
- No se registran passwords ni tokens.
- Se exige autorización por rol y ownership, CORS explícito y validación server-side.
- No se usan datos reales de pacientes, profesionales ni credenciales de FCV.

Fuente: [PRD, sección 8](../raw/prd-v1.0.md) y [restricciones técnicas](../raw/restricciones-tecnicas.md).

## Evidencia vigente (cierre 2026-10)

- **Sesión y roles (D-23):**
  - El rol de la sesión lo dicta el backend (`/api/me`); `localStorage` solo guarda tokens.
  - No existe cambio manual de vista.
  - Las rutas por rol usan `canMatch`.
  - La API responde 403 a ADMIN/PROFESSIONAL en endpoints de paciente (`RoleAccessIntegrationTest`).
- **Códigos de error reales:** se abrieron los dispatch `ERROR`/`FORWARD` y se agregó `ApiExceptionHandler`. Antes, cualquier 400/403/404/409 llegaba como 401 vacío y provocaba refresh innecesarios.
- **Refresh y logout:**
  - El refresh rota y revoca el anterior; el logout revoca el vigente.
  - Ante un refresh inválido el cliente limpia la sesión y vuelve al login (HU-004).
- **Recuperación de contraseña (HU-005):**
  - Token SHA-256, de un uso y con 30 min de vigencia según el `Clock` de negocio.
  - Respuesta indistinguible para cuentas inexistentes.
  - El token solo se expone con `EXPOSE_RECOVERY_TOKEN=true`.
- **Ownership y privacidad:**
  - Perfil por token.
  - Citas, cancelaciones y reprogramaciones propias.
  - Historial solo para el paciente dueño, el profesional de la cita y ADMIN.
  - La agenda del profesional expone únicamente `patientName` (D-18).
- **Evidencia E2E:** la clave de laboratorio de las cuentas seed se lee del seed y se enmascara; los tokens nunca se imprimen ([evidencias](../../scrum/evidencias/2026-10/README.md)).
