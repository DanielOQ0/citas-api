# HU-025 — Consultar historial de estados · Evidencia E2E

Fecha: 2026-10-05 · Cita de referencia #4 (Medicina General reprogramada 08:00 → 11:30).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Profesional E2E → agenda → "Historial" de la #4 | Estado, actor, fuente, fecha/hora y motivo | "Aprobada · lun, 5 oct 2026 · 00:00 · Sistema · Motivo: Aprobación automática de cita general" → "Aprobada · 00:06 · Administración · Admin Laboratorio · Motivo: Reprogramada: 11/10/2026 08:00 → 11/10/2026 11:30" | [01-historial-vista-profesional.png](01-historial-vista-profesional.png) |
| CA-01 | Paciente dueño → Mis citas → "Historial" de la #4; ADMIN por API | Mismo historial | Paciente: 2 eventos; ADMIN: `[(APPROVED, SYSTEM, None), (APPROVED, ADMIN, Admin Laboratorio)]` | [02-historial-vista-paciente.png](02-historial-vista-paciente.png) |
| CA-01 | Cobertura de transiciones de la corrida | Toda transición genera evento | Eventos por fuente en las citas E2E: `SYSTEM=4`, `USER=5`, `ADMIN=3` (aprobación automática, solicitudes, cancelación, cierres, decisiones y reprogramación) | — |
| CA-02 | `PUT`, `PATCH`, `DELETE`, `POST` sobre `/appointments/4/history` | Sin CRUD de auditoría | `405` en los cuatro | — |
| CA-03 | `paciente2` (tercero) consulta el historial de la #4; Andrea (otro profesional) | Denegado | `403` en ambos (HU-023) | — |

D-20: lo leen el paciente dueño, el profesional de la cita y ADMIN; se muestra el nombre del actor; aprobar una reprogramación se audita. Pruebas: `AppointmentFlowIntegrationTest.hu019And025…`, `RescheduleIntegrationTest.hu022ApprovalMovesAppointmentAndIsAudited`.
