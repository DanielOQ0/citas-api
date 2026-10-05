# HU-017 — Solicitar cita especializada · Evidencia E2E

Fecha: 2026-10-04/05 · Sesiones `pac` y `pac2`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | "Especialidad E2E 042347 · 60 min", 09:00 → panel con motivo opcional → Confirmar sin motivo | `REQUESTED` y franja retenida | "Solicitud especializada registrada y pendiente de aprobación (dom, 11 oct 2026, 09:00)."; BD: "2 slots, estado REQUESTED"; las franjas siguientes ya no incluyen 08:30, 09:00 ni 09:30 | [01-confirmacion-especializada-motivo-opcional.png](01-confirmacion-especializada-motivo-opcional.png), [02-solicitud-registrada-franja-retenida.png](02-solicitud-registrada-franja-retenida.png) |
| CA-02 | `pac2` pide la misma franja por API | No se crea solicitud competidora | `409 "La franja ya no está disponible"` | — |
| CA-03 | Solicitud en una sede no habilitada (ICV) | Rechazo | `400 "El profesional no está habilitado para esa sede y especialidad"` | — |
| — | `pac2` solicita 14:00 con motivo "Control anual E2E" | El motivo es opcional y visible para ADMIN | Registrada; el motivo aparece en la bandeja (HU-018) | [../HU-018/01-bandeja-filtrada.png](../HU-018/01-bandeja-filtrada.png) |

Decisión D-15: el motivo del paciente es opcional. Pruebas: `AppointmentFlowIntegrationTest.hu017And018SpecializedRequestHoldsSlotsUntilAdminDecides`.
