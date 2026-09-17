---
id: EP-006
tipo: epica
titulo: "Cita especializada y administración"
estado: Pendiente de aprobación
historias: ["[[HU-017-solicitar-cita-especializada]]", "[[HU-018-decidir-cita-especializada]]", "[[HU-019-consultar-mis-citas]]", "[[HU-020-cancelar-cita]]"]
dependencias: ["[[EP-005-busqueda-y-cita-general]]"]
---

# EP-006 — Cita especializada y administración

## Objetivo

Gestionar solicitudes especializadas retenidas y su decisión administrativa, incluida la consulta y cancelación por el USER.

## Valor esperado

Proceso administrado con transparencia de estado y liberación correcta de capacidad.

## Actores

- USER y ADMIN.

## Alcance

- Solicitud `REQUESTED`, bandeja, aprobar/rechazar, mis citas y cancelación futura.

## Fuera de alcance

- Historia clínica o reasignación de profesional dentro de una cita.

## Reglas de negocio

- Rechazo exige motivo y libera slots; cancelar libera slots y no reactiva directamente la cita.

## Dependencias

- [[EP-005-busqueda-y-cita-general]]

## Historias de usuario

- [[HU-017-solicitar-cita-especializada]]
- [[HU-018-decidir-cita-especializada]]
- [[HU-019-consultar-mis-citas]]
- [[HU-020-cancelar-cita]]

## Criterio de completitud de la épica

- [ ] Todas las decisiones reflejan estado, motivo cuando aplica, ocupación/liberación y trazabilidad.

## Riesgos e incógnitas

- Acordar qué estados constituyen “terminal” para cancelar, sin contradecir transiciones explícitas.
