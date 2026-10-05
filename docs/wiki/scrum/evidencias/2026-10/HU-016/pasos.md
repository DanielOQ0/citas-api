# HU-016 — Reservar cita general · Evidencia E2E

Fecha: 2026-10-04/05 · Sesiones `pac` (`pac.e2e.042347@demo.invalid`) y `pac2` (`paciente2@demo.invalid`).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Medicina General con "Profesional E2E 042347", 2026-10-11 08:00 → Confirmar | Cita `APPROVED` sin ADMIN | Panel "Confirmar cita general"; "Cita general aprobada para el dom, 11 oct 2026 a las 08:00."; historial: `APPROVED` con fuente `SYSTEM` ("Aprobación automática de cita general") | [01-confirmacion-cita-general.png](01-confirmacion-cita-general.png), [02-cita-general-aprobada.png](02-cita-general-aprobada.png) |
| CA-02 | `pac2` abre la confirmación de 10:00; `pac` reserva 10:00 primero; `pac2` confirma | No se crea una segunda cita; mensaje comprensible | `pac`: aprobada; `pac2`: "La franja ya no está disponible" (`409`) y la búsqueda se refresca; en BD hay 1 sola cita a las 10:00 | [03-franja-tomada-conflicto.png](03-franja-tomada-conflicto.png) |
| CA-03 | Duración 30 min | Ocupa el slot requerido | 1 slot por cita general (los de 60 min ocupan 2: HU-017) | — |

Pruebas automatizadas: `AppointmentFlowIntegrationTest.hu016GeneralAppointmentIsApprovedBySystemAndCannotBeDoubleBooked` (incluye fecha pasada → `400`). La reserva bloquea los slots con `SELECT … FOR UPDATE` dentro de la transacción (RN-01).
