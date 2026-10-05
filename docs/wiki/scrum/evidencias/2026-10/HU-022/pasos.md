# HU-022 — Decidir reprogramación · Evidencia E2E

Fecha: 2026-10-05 · Sesiones `admin` y `pac` · Solicitudes #2 (cita #4: 08:00 → 11:30) y #3 (cita #6: 09:00 → 14:00).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-03 | Bandeja de reprogramaciones (enlace propio en el menú, contador 3) con sede HIC + profesional E2E + 2026-10-11; luego + especialidad Medicina General | Filtrable por los 4 criterios | 2 solicitudes con paciente, especialidad, profesional, sede, duración, horario actual y propuesto; con la especialidad, 1 | [01-bandeja-reprogramaciones-filtrada.png](01-bandeja-reprogramaciones-filtrada.png) |
| CA-01 | Aprobar #2 | Libera la franja antigua, asigna la nueva y actualiza la cita | "Reprogramación aprobada: la cita de Paciente E2E 042347 quedó el dom, 11 oct 2026 a las 11:30."; BD cita #4 `11:30:00`, slots `11:30`; retenciones vivas 0; historial `APPROVED/ADMIN` "Reprogramada: 11/10/2026 08:00 → 11/10/2026 11:30" | [02-decisiones-reprogramacion.png](02-decisiones-reprogramacion.png) |
| CA-02 | Rechazar #3 sin motivo; luego con "El especialista no atiende esa tarde (E2E)" | Motivo obligatorio (D-16); libera solo la provisional y conserva la original | "El motivo es obligatorio." → "Reprogramación rechazada; Paciente E2E 042347 conserva su cita original."; BD cita #6 sigue `09:00` con slots `09:00, 09:30`; retención de 14:00 liberada | mismo |
| RF-15 | Paciente ve el rechazo y elige | Conservar o cancelar | Tarjeta: "Reprogramación rechazada (propuesta: … 14:00). Motivo: …" con "Conservar mi cita" / "Cancelar cita"; Conservar → "Conservas tu cita en su horario original."; BD `KEEP_APPOINTMENT` | [03-paciente-ve-rechazo-y-decide.png](03-paciente-ve-rechazo-y-decide.png), [04-paciente-conserva-cita.png](04-paciente-conserva-cita.png) |
| Regla | Decidir de nuevo #2 | Solo PENDING | `409 "La reprogramación ya fue decidida"` | — |

Pruebas automatizadas: `RescheduleIntegrationTest` (aprobación, rechazo, retención reutilizable tras el rechazo, conservar, cancelar en cascada y solicitudes heredadas sin retención).
