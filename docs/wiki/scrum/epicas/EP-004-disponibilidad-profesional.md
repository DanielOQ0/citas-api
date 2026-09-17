---
id: EP-004
tipo: epica
titulo: "Disponibilidad y slots de agenda"
estado: Pendiente de aprobación
historias: ["[[HU-012-definir-duracion-especialidad]]", "[[HU-013-publicar-bloques-disponibilidad]]", "[[HU-014-consultar-calendario-profesional]]"]
dependencias: ["[[EP-003-profesionales-y-asignaciones]]"]
---

# EP-004 — Disponibilidad y slots de agenda

## Objetivo

Publicar y consultar disponibilidad profesional por sede sin solapamientos.

## Valor esperado

Franja consistente y discretizada que sustenta reservas de 30 o 60 minutos.

## Actores

- PROFESSIONAL, USER y ADMIN.

## Alcance

- Duración por especialidad, bloques futuros, slots de 30 minutos y calendario propio.

## Fuera de alcance

- Duraciones elegidas por profesional y bloques en el pasado.

## Reglas de negocio

- Bloques no se solapan; requieren sede asignada; no se edita/elimina un bloque futuro con citas comprometidas.

## Dependencias

- [[EP-003-profesionales-y-asignaciones]]

## Historias de usuario

- [[HU-012-definir-duracion-especialidad]]
- [[HU-013-publicar-bloques-disponibilidad]]
- [[HU-014-consultar-calendario-profesional]]

## Criterio de completitud de la épica

- [ ] Sus HU están `Completada` y se evidencia la invariabilidad de disponibilidad comprometida.

## Riesgos e incógnitas

- La estrategia de persistencia para evitar carrera entre disponibilidad y reserva requiere decisión 3FN.
