# HU-006 — Gestionar perfil · Evidencia E2E

Fecha: 2026-10-04 · Sesión `pac` (`pac.e2e.042347@demo.invalid`).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | `/paciente/perfil` | Datos permitidos visibles | Nombre "Paciente E2E 042347", correo, documento (solo lectura) y teléfono editable | [01-telefono-actualizado-persistente.png](01-telefono-actualizado-persistente.png) |
| CA-02 | Teléfono `31-abc` → guardar; luego `3157778899` → guardar; F5 | Inválido rechazado; válido persiste | "Solo dígitos, entre 7 y 15." / "Teléfono actualizado."; tras F5 el campo conserva `3157778899` | mismo |
| CA-03 | `GET /api/v1/users/100` con token del paciente | No hay acceso a perfiles ajenos | `404`: no existe una ruta por id; el perfil siempre es el del token (`/users/me`) | — |
| Alcance | `PATCH /api/v1/users/me` con `email` y `firstName` además de `phone` | Solo cambia el teléfono; no se altera el rol | `200`; correo y nombre intactos | — |

Decisión: solo el teléfono es editable (D-08). Pruebas: `ProfileIntegrationTest.hu006ProfileIsOwnAndOnlyPhoneIsEditable` (incluye `400` con `fieldErrors[phone]` y `401` sin token).
