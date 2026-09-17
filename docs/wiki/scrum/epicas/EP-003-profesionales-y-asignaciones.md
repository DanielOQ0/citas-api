---
id: EP-003
tipo: epica
titulo: "Profesionales y asignaciones operativas"
estado: Pendiente de aprobación
historias: ["[[HU-010-registrar-profesional]]", "[[HU-011-asignar-y-habilitar-profesional]]"]
dependencias: ["[[EP-001-identidad-y-seguridad]]", "[[EP-002-perfil-y-catalogos]]"]
---

# EP-003 — Profesionales y asignaciones operativas

## Objetivo

Permitir al ADMIN administrar profesionales ficticios habilitados para atender.

## Valor esperado

Solo profesionales válidos y configurados participan en la agenda y búsqueda.

## Actores

- ADMIN y PROFESSIONAL.

## Alcance

- Creación de identidad PROFESSIONAL, código/matrícula sintéticos, especialidades, sedes y activación.

## Fuera de alcance

- Aprobación de citas por profesionales.

## Reglas de negocio

- Un profesional tiene una o varias especialidades, una primaria y una o ambas sedes; solo publica en sedes asignadas.

## Dependencias

- [[EP-001-identidad-y-seguridad]], [[EP-002-perfil-y-catalogos]]

## Historias de usuario

- [[HU-010-registrar-profesional]]
- [[HU-011-asignar-y-habilitar-profesional]]

## Criterio de completitud de la épica

- [ ] Sus HU están `Completada` y la elegibilidad de agenda se puede verificar.

## Riesgos e incógnitas

- Definir política para citas ya existentes al desactivar un profesional.
