---
tipo: indice-scrum
estado: Pendiente de aprobación
fuentes:
  - "PRD.md"
  - "RESTRICCIONES_TECNICAS.md"
  - "database/REQUISITOS_NORMALIZACION_3FN.md"
---

# Mapa Scrum / Spec-Driven Development — Sistema de Citas

## Propósito y límites

Mapa trazable del producto ficticio de agendamiento. Cubre el PRD completo, las restricciones técnicas y los requisitos de datos hasta 3FN. No constituye implementación ni contrato REST definitivo. Todas las HU están en **Pendiente de aprobación**; ninguna puede pasar a `Aprobada` sin revisión explícita del usuario.

## Arquitectura de referencia

- Backend: Java 21, Spring Boot 3.5.x, Maven, arquitectura hexagonal, Spring Data JPA, MySQL 8.4, Flyway, Spring Security y JWT access/refresh.
- Frontend: TypeScript con React o Angular, decidido por la exportación aprobada de Stitch/Google AI Studio; REST directo a `citas-api`, sin Express/BFF.
- Datos: 3FN, catálogos fijos por seed y datos exclusivamente sintéticos.

## Épicas

- [[EP-001-identidad-y-seguridad]]
- [[EP-002-perfil-y-catalogos]]
- [[EP-003-profesionales-y-asignaciones]]
- [[EP-004-disponibilidad-profesional]]
- [[EP-005-busqueda-y-cita-general]]
- [[EP-006-cita-especializada-y-administracion]]
- [[EP-007-reprogramacion]]
- [[EP-008-agenda-profesional-y-cierre]]
- [[EP-009-auditoria-y-contrato-rest]]

## Incrementos sugeridos (sin estimación temporal)

| Incremento / sesión candidata | Resultado verificable | HU candidatas en orden |
|---|---|---|
| S2 — Fundaciones | Identidad segura, catálogos y administración mínima de profesionales | [[HU-001-cargar-catalogos-fijos]], [[HU-002-registrar-usuario]], [[HU-003-iniciar-sesion]], [[HU-004-renovar-y-cerrar-sesion]], [[HU-005-recuperar-contrasena]], [[HU-008-gestionar-eps-y-planes]], [[HU-009-gestionar-especialidades]], [[HU-010-registrar-profesional]] |
| S3 — Agenda reservable | Perfil, afiliación, disponibilidad y cita general automática | [[HU-006-gestionar-perfil]], [[HU-007-gestionar-afiliacion]], [[HU-011-asignar-y-habilitar-profesional]], [[HU-012-definir-duracion-especialidad]], [[HU-013-publicar-bloques-disponibilidad]], [[HU-014-consultar-calendario-profesional]], [[HU-015-consultar-disponibilidad]], [[HU-016-reservar-cita-general]] |
| S4 — Flujo administrativo | Solicitud especializada, decisión, consulta y cancelación | [[HU-017-solicitar-cita-especializada]], [[HU-018-decidir-cita-especializada]], [[HU-019-consultar-mis-citas]], [[HU-020-cancelar-cita]] |
| S5 — Continuidad asistencial | Reprogramación y operación del profesional | [[HU-021-solicitar-reprogramacion]], [[HU-022-decidir-reprogramacion]], [[HU-023-consultar-agenda-propia]], [[HU-024-cerrar-atencion]] |
| S6 — Trazabilidad y frontera | Auditoría verificable y contrato REST consolidado; automatizaciones n8n posteriores, sin alterar el núcleo | [[HU-025-consultar-historial-de-estados]], [[HU-026-documentar-contrato-rest]] |

**Regla de selección:** S2, S3 y S4 seleccionan solo HU cuyo estado haya sido cambiado explícitamente a `Aprobada`. La fila describe secuencia, no autorización automática.

## Decisiones e incógnitas a revisar

- El framework web queda pendiente de la selección/exportación de Stitch/Google AI Studio; las HU usan el término “cliente TypeScript”.
- El mecanismo concreto de retención concurrente de slots y su índice/constraint debe justificarse en el diseño 3FN y comprobarse durante validación.
- El canal real de entrega del token de recuperación es opcional; el modo de desarrollo debe ser seguro y no exponer secretos.
- n8n queda fuera de los incrementos núcleo: su entrada posterior no cambia reglas ni estados funcionales.
