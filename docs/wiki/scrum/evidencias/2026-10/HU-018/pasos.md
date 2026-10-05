# HU-018 — Decidir cita especializada · Evidencia E2E

Fecha: 2026-10-05 · Sesión `admin` · Solicitudes: #6 (`pac`, 09:00) y #7 (`pac2`, 14:00).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Bandeja con filtros sede HIC + "Profesional E2E 042347" + "Especialidad E2E 042347" + 2026-10-11 | `REQUESTED` filtrables por los 4 criterios | 2 filas con paciente, especialidad, profesional, sede, fecha y hora, duración y motivo del paciente; contador del menú real | [01-bandeja-filtrada.png](01-bandeja-filtrada.png) |
| CA-02 | Aprobar #6 | `APPROVED` y conserva sus slots | "Solicitud de Paciente E2E 042347 aprobada."; BD `6:APPROVED:2slots` | [03-decisiones-registradas.png](03-decisiones-registradas.png) |
| CA-04 | Rechazar #7 sin escribir motivo | Rechazo bloqueado | El diálogo muestra "El motivo es obligatorio." | [02-rechazo-exige-motivo.png](02-rechazo-exige-motivo.png) |
| CA-03 | Rechazar #7 con "Agenda del especialista no disponible (E2E)" | `REJECTED`, motivo registrado, slots liberados | "…rechazada; el horario quedó libre."; BD `7:REJECTED:0slots`; el paciente ve el motivo (HU-019) | [03-decisiones-registradas.png](03-decisiones-registradas.png) |
| Regla | Decidir de nuevo #7; "rechazar" una cita general ya aprobada; PROFESSIONAL decide | Solo `REQUESTED`, solo ADMIN | `409 "La solicitud ya fue decidida"` en ambos casos; PROFESSIONAL → `403` | — |

D-14: no se aprueba una solicitud cuyo horario ya pasó (`409`), pero sí se puede rechazar (`AppointmentFlowIntegrationTest.hu017And018…`).
