# HU-010 — Registrar profesional · Evidencia E2E

Fecha: 2026-10-04 · Sesión `admin` · Corrida `042347` · Profesional sintético `prof.e2e.042347@demo.invalid`, código `E2E-042347`, matrícula `RM-E2E-042347` (ficticios).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | "Registrar profesional" con datos ficticios válidos | Cuenta con rol PROFESSIONAL | "Profesional Profesional E2E 042347 registrado (E2E-042347)…"; rol en BD `PROFESSIONAL`; inicia sesión y cae en `/profesional/mi-agenda` (HU-013) | [01-profesional-registrado.png](01-profesional-registrado.png) |
| CA-02 | Mismo código `E2E-042347` con otros datos; luego misma matrícula con otro código | Rechazo | "El código profesional ya existe" / "La matrícula ya existe" (`409`) | — |
| CA-03 | `POST /api/v1/admin/professionals` con token USER | Denegado | `403` | — |

Pruebas automatizadas: `ProfessionalAgendaIntegrationTest.hu010OnlyAdminCreatesProfessionalsWithUniqueCodeAndLicense`. Validaciones de documento, teléfono y clave según D-07.
