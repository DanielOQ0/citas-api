# HU-015 — Consultar disponibilidad · Evidencia E2E

Fecha: 2026-10-04/05 · Sesión `pac` (`pac.e2e.042347@demo.invalid`) · Fecha consultada 2026-10-11.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Filtros: sede HIC, tipo "Especializada", especialidad "Especialidad E2E 042347 · 60 min", profesional, fecha | Horarios del filtro | Sedes HIC e ICV; el tipo Especializada lista 12 especialidades sin Medicina General; el selector de profesional solo ofrece a quien atiende esa especialidad en HIC ("Profesional E2E 042347") | [01-busqueda-60min-consecutivos.png](01-busqueda-60min-consecutivos.png) |
| CA-02 | Resultado 60 min sobre bloques 08:00–12:00 y 14:00–17:00 | Solo inicios con dos slots consecutivos libres | `08:00 … 11:00` y `14:00 … 16:00`; no aparecen 11:30 ni 16:30 (sin segundo slot) | mismo |
| CA-01 | Tipo "General" → Medicina General · 30 min | Franjas de 30 min | `08:00 … 11:30` y `14:00 … 16:30` (14 franjas) | [../HU-016/01-confirmacion-cita-general.png](../HU-016/01-confirmacion-cita-general.png) |
| CA-03 | Tras la solicitud especializada de 09:00 (retiene 09:00 y 09:30) y las citas de 08:00 y 10:00 | Retenidos u ocupados no se ofrecen | 60 min: `10:30 11:00 14:00 …` (sin 08:00, 08:30, 09:00, 09:30 ni 10:00) | [../HU-017/02-solicitud-registrada-franja-retenida.png](../HU-017/02-solicitud-registrada-franja-retenida.png) |
| CA-03 | Profesional inactivo, especialidad inactiva o no asociada | No se ofrece | 0 franjas (HU-011, HU-009) | [../HU-011/pasos.md](../HU-011/pasos.md), [../HU-009/pasos.md](../HU-009/pasos.md) |

Pruebas automatizadas: `AppointmentFlowIntegrationTest.hu015SixtyMinuteOptionsNeedTwoConsecutiveFreeSlots`, `hu015SearchFiltersAndExcludesPastAndIneligibleOptions` (excluye franjas pasadas), `SlotPolicyTest`.
