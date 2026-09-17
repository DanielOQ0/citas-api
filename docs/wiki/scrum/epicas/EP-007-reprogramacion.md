---
id: EP-007
tipo: epica
titulo: "Reprogramación preservando la cita original"
estado: Pendiente de aprobación
historias: ["[[HU-021-solicitar-reprogramacion]]", "[[HU-022-decidir-reprogramacion]]"]
dependencias: ["[[EP-006-cita-especializada-y-administracion]]"]
---

# EP-007 — Reprogramación preservando la cita original

## Objetivo

Permitir que un USER proponga nueva franja para una cita aprobada sin perder la original hasta la decisión ADMIN.

## Valor esperado

Continuidad de atención con retención segura de la alternativa.

## Actores

- USER y ADMIN.

## Alcance

- Solicitud `PENDING`, retención, aprobación/rechazo y liberación correspondiente.

## Fuera de alcance

- Cambio de profesional; ese caso es una nueva cita.

## Reglas de negocio

- Solo citas futuras aprobadas; mantiene profesional/especialidad; rechazo conserva la cita original.

## Dependencias

- [[EP-006-cita-especializada-y-administracion]]

## Historias de usuario

- [[HU-021-solicitar-reprogramacion]]
- [[HU-022-decidir-reprogramacion]]

## Criterio de completitud de la épica

- [ ] La cita original nunca se destruye antes de aprobación y cada resultado libera la franja correcta.

## Riesgos e incógnitas

- Se debe definir de forma explícita la atomicidad de cambio de slots durante la aprobación.
