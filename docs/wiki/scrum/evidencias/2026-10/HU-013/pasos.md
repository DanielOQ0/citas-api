# HU-013 — Publicar bloques de disponibilidad · Evidencia E2E

Fecha: 2026-10-04/05 · Sesión `profe` (`prof.e2e.042347@demo.invalid`) · Fecha de prueba D+7 = 2026-10-11.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Publicar 08:00–12:00 y 14:00–16:00 en HIC; editar el segundo a 14:00–17:00 | Discretización en slots de 30 min | "Bloque publicado en franjas de 30 minutos."; BD: 8 y 4 slots; tras editar, 6 franjas | [01-bloques-publicados-y-rechazos.png](01-bloques-publicados-y-rechazos.png) |
| CA-02 | 11:00–13:00 (solapa), ICV (sede no asignada), 2026-10-03 (pasado), 08:15 (no alineado) | Rechazo | "El bloque se solapa con otro existente" (`409`), "La sede no está asignada al profesional" (`400`), "El bloque debe iniciar en el futuro" (`400`), "Los límites del bloque deben ser múltiplos de 30 minutos" (`400`) | mismo |
| CA-03 | Bloque 08:00–12:00 con 2 citas aprobadas y una solicitud que retiene 2 slots | No se edita ni se borra | Fila "8 franjas · 4 reservadas/retenidas" con Editar y Eliminar deshabilitados; `PATCH`/`DELETE` → `409 "No se puede editar/eliminar un bloque con citas reservadas o retenidas"` | [02-bloque-comprometido-protegido.png](02-bloque-comprometido-protegido.png) |

D-13: también se permite publicar hoy si el inicio es futuro, y una retención de reprogramación PENDING también protege el bloque (`ProfessionalAgendaIntegrationTest.hu013BlocksAreFutureAlignedNonOverlappingAndProtectedWhenCommitted`, `AvailabilityService.requireNotCommitted`).
