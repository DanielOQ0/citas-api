# HU-019 — Consultar mis citas · Evidencia E2E

Fecha: 2026-10-05 · Sesiones `pac` y `pac2`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | `pac2` → Mis citas, cita #7 rechazada | Siete campos + motivo de rechazo | "Especialidad E2E 042347 · Rechazada · dom, 11 oct 2026 · 14:00–15:00 · 60 min · Profesional E2E 042347 · Hospital Internacional de Colombia (HIC) · Motivo de rechazo: Agenda del especialista no disponible (E2E)"; historial: Solicitada (Usuario, Paciente Dos) → Rechazada (Administración, Admin Laboratorio, con motivo) | [01-rechazada-con-motivo-e-historial.png](01-rechazada-con-motivo-e-historial.png) |
| CA-02 | `pac`: estado "Aprobada" + rango 2026-10-11 a 2026-10-11 | Solo coincidencias | 3 citas aprobadas de ese día; con "Rechazada": "No hay citas para los filtros seleccionados."; el filtro ofrece los 6 estados | [02-filtro-estado-fecha-siete-campos.png](02-filtro-estado-fecha-siete-campos.png) |
| CA-03 | `pac` pide el historial de la cita #7 (de `pac2`); `pac2` cancela la #6 (de `pac`) | Denegado | `403` en ambos | — |

Pruebas automatizadas: `AppointmentFlowIntegrationTest.hu019And025MyAppointmentsHistoryAndPrivacy`.
