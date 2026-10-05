# HU-004 — Renovar y cerrar sesión · Evidencia E2E

Fecha: 2026-10-04 · Sesión `pac` (paciente `pac.e2e.042347@demo.invalid`) · Los tokens nunca se imprimen.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Con sesión activa, se reemplaza el access en `localStorage` por un valor inválido y se navega a `/paciente/mis-citas` | Nuevo access sin reenviar contraseña | El interceptor recibe 401, renueva con el refresh y reintenta; la página carga con el usuario; el access quedó reemplazado por un JWT válido | [01-refresh-transparente.png](01-refresh-transparente.png) |
| CA-03 | Se captura el refresh vigente (sin mostrarlo), "Cerrar sesión" y `POST /api/auth/refresh` con ese refresh | El refresh ya no renueva | `/login`; refresh anterior → `401` | — |
| CA-02 | `POST /api/auth/refresh` con un token inválido | No se emite access | `401`, sin `accessToken` | — |
| CA-02 | Access y refresh inválidos en `localStorage` + recarga de una ruta privada | Sesión descartada | Vuelve a `/login` y se limpian ambos tokens | [02-refresh-invalido-vuelve-a-login.png](02-refresh-invalido-vuelve-a-login.png) |

Rotación (decisión adoptada): cada refresh revoca el anterior (`AuthFlowIntegrationTest.hu004RefreshRotatesAndLogoutRevokes`: el refresh rotado ya no sirve y el logout revoca el vigente). Revocación persistida en `refresh_tokens.revoked_at`.
