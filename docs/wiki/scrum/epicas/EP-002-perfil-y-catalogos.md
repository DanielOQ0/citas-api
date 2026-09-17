---
id: EP-002
tipo: epica
titulo: "Perfil, afiliación y catálogos configurables"
estado: Pendiente de aprobación
historias: ["[[HU-006-gestionar-perfil]]", "[[HU-007-gestionar-afiliacion]]", "[[HU-008-gestionar-eps-y-planes]]", "[[HU-009-gestionar-especialidades]]"]
dependencias: ["[[EP-001-identidad-y-seguridad]]"]
---

# EP-002 — Perfil, afiliación y catálogos configurables

## Objetivo

Mantener datos personales permitidos y catálogos administrativos sin duplicación.

## Valor esperado

Datos consistentes para reservar y operar citas.

## Actores

- USER y ADMIN.

## Alcance

- Perfil, afiliación EPS/plan/régimen, CRUD de EPS/planes y especialidades.

## Fuera de alcance

- Datos clínicos, facturación y borrado físico de catálogos referenciados.

## Reglas de negocio

- EPS, régimen y plan no se duplican por usuario; catálogos referenciados se desactivan.

## Dependencias

- [[EP-001-identidad-y-seguridad]]

## Historias de usuario

- [[HU-006-gestionar-perfil]]
- [[HU-007-gestionar-afiliacion]]
- [[HU-008-gestionar-eps-y-planes]]
- [[HU-009-gestionar-especialidades]]

## Criterio de completitud de la épica

- [ ] Sus HU están `Completada` y la persistencia mantiene 3FN para catálogos y afiliación.

## Riesgos e incógnitas

- Definir campos de perfil que el USER puede modificar sin alterar su identidad verificada.
