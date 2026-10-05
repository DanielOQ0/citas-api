# HU-005 — Recuperar contraseña · Evidencia E2E

Fecha: 2026-10-04 · Sesión `rec` · Cuenta sintética dedicada `rec.e2e.042347@demo.invalid` · Entorno de desarrollo con `EXPOSE_RECOVERY_TOKEN=true` (token devuelto en la respuesta y nunca registrado en logs).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| Nota | "Recuperar contraseña" con un correo inexistente | Redacción segura | Mismo mensaje que para una cuenta real: "Si la cuenta existe, recibirás instrucciones…" (no permite enumerar cuentas) | — |
| CA-01 | Solicitud con el correo registrado | Token temporal sin revelar secretos | Mismo mensaje; en desarrollo el token (72 caracteres) se autocompleta; en BD solo su hash SHA-256; `expires_at` = solicitud + 30 min (23:51:46 → 00:21:46, hora de Bogotá) | [01-solicitud-token-desarrollo.png](01-solicitud-token-desarrollo.png) |
| CA-02 | Nueva contraseña + "Cambiar contraseña" | Se actualiza con hash y el token se consume | "Contraseña actualizada. Ya puedes iniciar sesión."; `used_at` registrado; login con la clave anterior → "Credenciales inválidas."; con la nueva → `/paciente/inicio` | [02-contrasena-actualizada.png](02-contrasena-actualizada.png) |
| CA-03 | `POST /api/auth/password-reset` reutilizando el mismo token | Rechazo | `400 {"message":"Token inválido o vencido"}` | — |
| CA-03 | Token vencido (prueba automatizada con reloj de negocio +31 min) | Rechazo | `AuthFlowIntegrationTest.hu005RecoveryTokenIsTemporaryAndSingleUse`: `400` | — |

Decisión: en desarrollo el token también se devuelve para correos inexistentes (señuelo inútil), de modo que las respuestas son indistinguibles; en producción `EXPOSE_RECOVERY_TOKEN=false` (por defecto) no lo devuelve.
