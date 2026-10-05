# HU-020 — Cancelar cita · Evidencia E2E

Fecha: 2026-10-05 · Sesión `pac` · Cita #5 (Medicina General, 2026-10-11 10:00).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | "Cancelar" → confirmación en línea → "Sí, cancelar" | `CANCELLED`, slots liberados, historial | "Cita cancelada. El horario quedó disponible."; estado "Cancelada"; la franja de 10:00 vuelve a ofrecerse; historial `APPROVED/SYSTEM → CANCELLED/USER`; el contador del menú pasa de 3 a 2 | [01-confirmar-cancelacion.png](01-confirmar-cancelacion.png), [02-cita-cancelada-sin-acciones.png](02-cita-cancelada-sin-acciones.png) |
| CA-02 | Cita terminal: sin botón Cancelar; `POST …/cancel` de nuevo; cita ajena (`pac2`) | Denegado | Solo queda "Historial"; `409 "La cita no es cancelable: debe ser futura y no estar finalizada"`; ajena `403` (HU-019) | mismo |
| CA-03 | Reactivar la cancelada (`POST /admin/appointments/{id}/decision` APPROVE) | No permitido | `409 "La solicitud ya fue decidida"`; el dominio no admite `CANCELLED → APPROVED` | — |

D-17: cancelar una cita con reprogramación PENDING la pasa a `CANCELLED` y libera su retención; tras un rechazo, cancelar registra `CANCEL_APPOINTMENT` (`RescheduleIntegrationTest.hu020…`). Cita pasada → `409` (`AppointmentFlowIntegrationTest.hu020CancellationReleasesSlotsAndIsRestricted`).
