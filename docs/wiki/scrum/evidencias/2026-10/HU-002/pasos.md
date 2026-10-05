# HU-002 — Registrar usuario · Evidencia E2E

Fecha: 2026-10-04 · Sesión `pac` (`playwright-cli`, 1280×800) · Corrida `042347` · Cuenta creada: `pac.e2e.042347@demo.invalid` (sintética).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-04 | Pestaña Registro; documento `12-3`, correo `correo-invalido`, teléfono `300-abc`, clave `corta`; "Crear cuenta" | El cliente muestra el error de cada campo y no envía | "Revisa los campos marcados." + 4 errores: documento (5-20 alfanumérico), correo, teléfono (7-15 dígitos), contraseña (≥ 8) | [01-validacion-por-campo.png](01-validacion-por-campo.png) |
| CA-04 | `POST /api/auth/register` con `documentType: "NIT"` | El servidor rechaza | `400` `fieldErrors: [{field: documentType, message: "Tipo de documento no válido (CC, CE, TI o PA)"}]` | — |
| CA-01 | Datos válidos sintéticos (tipo `CE`, documento `E2E042347`, teléfono `3105550000`) | Cuenta con rol USER | Redirige a `/paciente/inicio`; header "Paciente E2E 042347 · Paciente"; rol en BD `USER` | [02-registro-exitoso-inicio-paciente.png](02-registro-exitoso-inicio-paciente.png) |
| CA-02 | Nuevo registro con el mismo correo | Se rechaza sin crear cuenta | Mensaje "Email o documento ya registrado"; sigue en `/login`; 1 sola cuenta con ese correo | [03-email-duplicado-rechazado.png](03-email-duplicado-rechazado.png) |
| CA-03 | `password_hash` del usuario creado | Sin texto plano | Hash BCrypt (`$2…`); la contraseña no aparece en BD ni en logs | — |

Pruebas automatizadas: `AuthFlowIntegrationTest.hu002RegistrationValidatesFormatsAndUniqueness`, `AuthIntegrationTest`.
Decisión: CC/CE/TI/PA, documento 5-20 alfanumérico, teléfono 7-15 dígitos, clave ≥ 8 (D-07).
