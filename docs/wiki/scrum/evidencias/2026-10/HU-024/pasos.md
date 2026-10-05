# HU-024 — Cerrar atención · Evidencia E2E

Fecha: 2026-10-05 · Sesión `profe` (profesional E2E).

**Fixture SQL (D-19):** como la API no permite citas en el pasado, se reservaron por API dos citas generales reales (#8 y #9, 2026-10-11 15:00 y 15:30) y luego se movieron a ayer liberando sus slots futuros:

```sql
update appointments set scheduled_start_at='2026-10-04 15:00:00', scheduled_end_at='2026-10-04 15:30:00' where id=8;
update appointments set scheduled_start_at='2026-10-04 15:30:00', scheduled_end_at='2026-10-04 16:00:00' where id=9;
update professional_slots set appointment_id=null where appointment_id in (8,9);
```

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Agenda, vista "Día" 2026-10-04: #8 → "Completada"; #9 → "No asistió" | Pasa a `COMPLETED` / `NO_SHOW` | Ambas muestran los botones de cierre; "Atención registrada como completada." / "Inasistencia registrada."; salen de la agenda APPROVED; BD `8:COMPLETED 9:NO_SHOW` | [01-citas-terminadas-con-cierre.png](01-citas-terminadas-con-cierre.png), [02-cierres-registrados.png](02-cierres-registrados.png) |
| CA-02 | Cerrar la cita futura #6; Andrea cierra la #8; cerrar de nuevo la #8 | Denegado | `409 "La cita solo puede cerrarse después de finalizar"`; `403`; `409 "La cita no está aprobada o ya fue cerrada"`; las citas futuras no muestran botones de cierre | — |
| CA-03 | Historial de la #8 | Nuevo estado, actor, fuente y fecha | `COMPLETED`, fuente `USER`, actor "Profesional E2E 042347", `2026-10-05T00:09:06` | — |

Aplicable = cita propia, APPROVED y con hora de fin pasada (zona de Bogotá) (D-19). Pruebas: `AppointmentFlowIntegrationTest.hu023And024ProfessionalAgendaAndClosing` (reloj de negocio controlado), `AppointmentRulesTest`.
