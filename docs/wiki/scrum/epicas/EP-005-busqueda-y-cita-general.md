---
id: EP-005
tipo: epica
titulo: "Búsqueda y cita general"
estado: Completada
historias: ["[[HU-015-consultar-disponibilidad]]", "[[HU-016-reservar-cita-general]]"]
dependencias: ["[[EP-004-disponibilidad-profesional]]"]
---

# EP-005 — Búsqueda y cita general

## Objetivo

Permitir al USER encontrar franjas aptas y obtener una cita de Medicina General confirmada automáticamente.

## Valor esperado

Autogestión inmediata sin intervención administrativa y sin doble reserva.

## Actores

- USER.

## Alcance

- Filtros de disponibilidad y creación de cita general `APPROVED`.

## Fuera de alcance

- Aprobación manual de citas generales.

## Reglas de negocio

- Solo se muestran slots suficientes y consecutivos; la reserva verifica disponibilidad al confirmar.

## Dependencias

- [[EP-004-disponibilidad-profesional]]

## Historias de usuario

- [[HU-015-consultar-disponibilidad]]
- [[HU-016-reservar-cita-general]]

## Criterio de completitud de la épica

- [x] Sus HU están `Completada` y una carrera por el mismo slot no crea dos citas.

## Riesgos e incógnitas

- Definir la identificación de la especialidad fija Medicina General en el catálogo.

## Cierre 2026-10

Todas sus HU quedaron `Lista` con evidencia (pruebas de integración + E2E) el 2026-10-05; ver [[plan-cierre-2026-10]] y `evidencias/2026-10/`.
