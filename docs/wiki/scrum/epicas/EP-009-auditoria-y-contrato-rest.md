---
id: EP-009
tipo: epica
titulo: "Auditoría y contrato REST"
estado: Pendiente de aprobación
historias: ["[[HU-025-consultar-historial-de-estados]]", "[[HU-026-documentar-contrato-rest]]"]
dependencias: ["[[EP-001-identidad-y-seguridad]]", "[[EP-008-agenda-profesional-y-cierre]]"]
---

# EP-009 — Auditoría y contrato REST

## Objetivo

Hacer verificables los cambios de estado y mantener una frontera REST documentada para el cliente directo.

## Valor esperado

Trazabilidad operativa y coordinación sin BFF entre `citas-web` y `citas-api`.

## Actores

- USER, PROFESSIONAL, ADMIN y equipo de desarrollo.

## Alcance

- Historial inmutable de estados y documentación del contrato JSON.

## Fuera de alcance

- Cambios de código, BFF, Express y automatizaciones n8n del núcleo.

## Reglas de negocio

- Auditoría guarda cita, estado, actor cuando exista, fuente, fecha/hora y motivo opcional; no es CRUD normal.

## Dependencias

- [[EP-001-identidad-y-seguridad]], [[EP-008-agenda-profesional-y-cierre]]

## Historias de usuario

- [[HU-025-consultar-historial-de-estados]]
- [[HU-026-documentar-contrato-rest]]

## Criterio de completitud de la épica

- [ ] Las transiciones son trazables y el contrato describe las capacidades aprobadas sin secretos.

## Riesgos e incógnitas

- La ubicación/formato final del contrato debe acordarse al iniciar la HU, sin crear BFF.
