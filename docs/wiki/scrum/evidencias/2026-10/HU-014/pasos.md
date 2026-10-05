# HU-014 — Consultar calendario profesional · Evidencia E2E

Fecha: 2026-10-04 · Sesiones `profe` (profesional E2E) y API de `andrea.ruiz@demo.invalid`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Calendario de bloques, filtro fecha 2026-10-11 + sede HIC | Solo sus bloques publicados | 2 bloques: 08:00–12:00 (8 franjas) y 14:00–17:00 (6 franjas), con nombre de la sede | [01-calendario-filtrado-fecha-sede.png](01-calendario-filtrado-fecha-sede.png) |
| CA-01 | Filtro con otra fecha | Vacío | "No hay bloques para el filtro seleccionado." | — |
| CA-02 | Andrea consulta sus bloques del 2026-10-11 | No ve los ajenos | Ninguno de los bloques E2E aparece en su lista | — |
| CA-02 | Andrea `DELETE` un bloque del profesional E2E; USER consulta bloques | Denegado | `404 "Bloque no encontrado"` (el recurso ajeno no existe para ella); USER → `403` | — |

Pruebas automatizadas: `ProfessionalAgendaIntegrationTest.hu014CalendarShowsOnlyOwnBlocksWithFilters`.
