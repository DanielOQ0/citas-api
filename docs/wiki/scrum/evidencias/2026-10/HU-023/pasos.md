# HU-023 — Consultar agenda propia · Evidencia E2E

Fecha: 2026-10-05 · Sesión `profe` (profesional E2E) · API de `andrea.ruiz@demo.invalid`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Vista "Semana" con fecha 2026-10-11 + sede HIC; luego "Día"; luego sede ICV | Citas `APPROVED` propias coincidentes | Semana (lun 5 – dom 11 oct): 09:00–10:00 Especialidad E2E y 11:30–12:00 Medicina General, con nombre del paciente; Día: 2 citas; ICV: "No hay citas aprobadas en el periodo seleccionado." | [01-agenda-semana-sede.png](01-agenda-semana-sede.png) |
| CA-02 | Andrea consulta su agenda del 2026-10-11 y el historial de una cita del profesional E2E | Denegado | Ninguna cita E2E en su agenda; historial ajeno → `403` | — |
| CA-03 | Citas cancelada (10:00), rechazada (14:00) y la franja antigua de 08:00 | No figuran | La API de agenda devuelve solo `[('09:00','APPROVED',…), ('11:30','APPROVED',…)]` | — |
| D-18 | Datos del paciente expuestos | Solo el nombre | La única clave de paciente en la respuesta es `patientName` | — |

Índices de consulta: `ix_appointments_professional (professional_id, scheduled_start_at)` en el esquema de referencia; el filtro usa un rango sobre `scheduled_start_at`. Pruebas: `AppointmentFlowIntegrationTest.hu023And024ProfessionalAgendaAndClosing`.
