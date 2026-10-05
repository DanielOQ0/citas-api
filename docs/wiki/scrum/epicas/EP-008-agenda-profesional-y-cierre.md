---
id: EP-008
tipo: epica
titulo: "Agenda profesional y cierre de atención"
estado: Completada
historias: ["[[HU-023-consultar-agenda-propia]]", "[[HU-024-cerrar-atencion]]"]
dependencias: ["[[EP-006-cita-especializada-y-administracion]]"]
---

# EP-008 — Agenda profesional y cierre de atención

## Objetivo

Dar al PROFESSIONAL visibilidad limitada de su agenda y capacidad de cerrar atenciones aplicables.

## Valor esperado

Operación asistencial ficticia con privacidad y estados finales verificables.

## Actores

- PROFESSIONAL.

## Alcance

- Agenda propia `APPROVED` por día/semana/sede; transición a `COMPLETED` o `NO_SHOW`.

## Fuera de alcance

- Acceso a citas ajenas, diagnósticos o tratamientos.

## Reglas de negocio

- El profesional solo ve sus propias citas y el cierre se aplica a una cita pasada/aplicable.

## Dependencias

- [[EP-006-cita-especializada-y-administracion]]

## Historias de usuario

- [[HU-023-consultar-agenda-propia]]
- [[HU-024-cerrar-atencion]]

## Criterio de completitud de la épica

- [x] Se comprueba ownership y cada cierre queda auditado.

## Riesgos e incógnitas

- Precisar el criterio “aplicable” de cierre si difiere de una cita pasada.

## Cierre 2026-10

Todas sus HU quedaron `Lista` con evidencia (pruebas de integración + E2E) el 2026-10-05; ver [[plan-cierre-2026-10]] y `evidencias/2026-10/`.
