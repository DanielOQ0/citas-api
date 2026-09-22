# Seguridad

## HECHOS

- Las contraseñas requieren hash adaptativo compatible con Spring Security.
- Access token y refresh token JWT deben estar separados.
- Secretos solo pueden proceder de variables de entorno o `.env`; nunca se versionan.
- No se deben registrar passwords ni tokens.
- Se exige autorización por rol y ownership, CORS explícito y validación server-side.
- No se usan datos reales de pacientes, profesionales ni credenciales de FCV.

Fuente: [PRD, sección 8](../raw/prd-v1.0.md) y [restricciones técnicas](../raw/restricciones-tecnicas.md).

La recuperación de contraseña en desarrollo requiere decisión segura adicional: el PRD permite una respuesta controlada, mientras que la política prohíbe logging de tokens.

## Evidencia del incremento de identidad

- BCrypt almacena únicamente `password_hash`; no se devuelven hashes ni contraseñas.
- Access y refresh usan secretos separados y claims `type` distintos.
- Refresh tokens se almacenan por `jti`, con expiración y `revoked_at`; refresh rota y logout revoca.
- `/api/me` exige access JWT válido; las credenciales inválidas responden `401` genérico.
- Secretos y conexión se leen desde variables de entorno; no se abrió ni modificó ningún `.env`.
