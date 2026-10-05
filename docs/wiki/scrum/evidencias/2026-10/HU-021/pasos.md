# HU-021 — Solicitar reprogramación · Evidencia E2E

Fecha: 2026-10-05 · Sesión `pac` · Citas #4 (Medicina General 08:00) y #6 (Especialidad E2E 09:00, 60 min) del 2026-10-11.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Cita #4 → "Reprogramar" → fecha 2026-10-11 → "Ver horarios" → 11:30 | `PENDING` y nueva franja retenida | Opciones solo del mismo profesional, especialidad y sede; "Reprogramación solicitada para el dom, 11 oct 2026 a las 11:30. Tu cita actual se conserva hasta la decisión."; 11:30 deja de ofrecerse en la búsqueda | [01-opciones-reprogramacion.png](01-opciones-reprogramacion.png) |
| CA-02 | Consultar la cita #4 con la solicitud pendiente | Conserva su franja | Tarjeta: horario vigente "08:00–08:30" + "Reprogramación pendiente hacia … 11:30"; BD: el slot de 08:00 sigue asignado; el botón Reprogramar desaparece | [02-reprogramacion-pendiente-original-conservada.png](02-reprogramacion-pendiente-original-conservada.png) |
| CA-03 | Reprogramar la cita cancelada #5; segunda solicitud sobre la #4 | Denegado; se indica que cambiar de profesional es una cita nueva | `409 "Solo se puede reprogramar una cita aprobada; cambiar de profesional es una cita nueva"`; `409 "La cita ya tiene una reprogramación pendiente"`; el panel advierte "Cambiar de profesional es una cita nueva" y el contrato no admite cambiar profesional | — |
| — | Cita #6 (60 min) → 14:00 | Para el flujo de rechazo | Opciones de 60 min `10:00 10:30 14:00 …` (11:00 excluida porque 11:30 está retenida); solicitud PENDING | — |

Pruebas automatizadas: `RescheduleIntegrationTest.hu021RequestHoldsNewSlotAndPreservesOriginal` (franja pasada o igual → `400`, otra persona → `403`, especializada `REQUESTED` → `409`).
