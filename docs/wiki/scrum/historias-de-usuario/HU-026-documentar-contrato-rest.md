---
id: HU-026
tipo: historia-de-usuario
titulo: "Documentar contrato REST"
estado: Lista
epica: "[[EP-009-auditoria-y-contrato-rest]]"
esfuerzo: Alto
sprint_sugerido: S6
dependencias: ["[[HU-003-iniciar-sesion]]", "[[HU-016-reservar-cita-general]]", "[[HU-018-decidir-cita-especializada]]", "[[HU-022-decidir-reprogramacion]]", "[[HU-024-cerrar-atencion]]"]
relacionadas: ["[[HU-025-consultar-historial-de-estados]]"]
---
# HU-026 — Documentar contrato REST

## Historia de usuario

**COMO** equipo de desarrollo **QUIERO** mantener documentado el contrato REST JSON entre cliente y API **PARA** integrar las capacidades aprobadas sin BFF.

## Contexto y descripción

El frontend consume directamente `citas-api`; esta HU consolida la especificación de recursos, autenticación, autorizaciones, entradas, salidas y errores de las capacidades que lleguen aprobadas. No autoriza cambiar contratos ni código sin la secuencia cross-repo correspondiente.

## Alcance

- Contrato documentado para autenticación, perfiles/catálogos, agenda, citas, reprogramaciones, administración y auditoría aprobadas.
- Recursos JSON, semántica de errores, autenticación JWT y límites de autorización/ownership.

## Fuera de alcance

- Express/BFF, contratos para funcionalidades no aprobadas, secretos y generación automática no solicitada.

## Reglas de negocio

- REST directo frontend-backend; CORS explícito; ningún token/credential se documenta como valor real.

## Dependencias y relaciones

- Épica: [[EP-009-auditoria-y-contrato-rest]].
- Dependencias: [[HU-003-iniciar-sesion]], [[HU-016-reservar-cita-general]], [[HU-018-decidir-cita-especializada]], [[HU-022-decidir-reprogramacion]], [[HU-024-cerrar-atencion]].
- Relacionadas: [[HU-025-consultar-historial-de-estados]].

## Esfuerzo

**Nivel:** Alto.

**Justificación de dificultad:** frontera transversal que debe permanecer consistente entre dos repositorios, seguridad y estados del ciclo de cita.

## Tareas de desarrollo

- [x] **T-01 — Inventariar capacidades aprobadas y sus recursos/operaciones.**  
  Dificultad: Medio. Describir intención, autorización, payload y respuesta sin inventar endpoints prematuros.
- [x] **T-02 — Definir convenciones JSON y errores verificables.**  
  Dificultad: Alto. Incluir validación, conflicto de slot, autenticación y autorización donde apliquen.
- [x] **T-03 — Alinear documentación con `citas-api` y `citas-web`.**  
  Dificultad: Alto. Aplicar el plan cross-repo antes de modificar un contrato.
- [x] **T-04 — Añadir verificación de contrato en funcionalidades clave.**  
  Dificultad: Alto. Evidencia estática o pruebas disponibles sin alterar el repositorio durante validación.

## Criterios de aceptación

### CA-01 — Cobertura de capacidades aprobadas

**Dado** una HU funcional aprobada que expone interacción cliente-API  
**Cuando** se revisa el contrato  
**Entonces** documenta actor autorizado, operación, datos de entrada, resultado y errores relevantes.

### CA-02 — Seguridad y límites

**Dado** una operación privada  
**Cuando** se documenta  
**Entonces** indica la autenticación JWT y las restricciones de rol/ownership, sin publicar secretos ni credenciales.

### CA-03 — Coherencia cross-repo

**Dado** una modificación de contrato propuesta  
**Cuando** se planifica su entrega  
**Entonces** identifica primero los repositorios/archivos afectados y exige actualizar contrato, backend, cliente y evidencia en ese orden.

## Definition of Done

- [x] CA-01 a CA-03 están validados con evidencia para todas las funcionalidades implementadas dentro del alcance aprobado.
- [x] La documentación no prescribe Express/BFF y refleja REST JSON directo a `citas-api`.
- [x] Se verifican coherencia de autenticación, autorización, errores de validación y conflictos de reserva.
- [x] Cualquier cambio contractual tiene plan cross-repo, pruebas/evidencia aplicables y trazabilidad Scrum actualizada.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-026/pasos.md) · E2E | 52/52 endpoints documentados con actor, entrada, salida y errores |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-026/pasos.md) · E2E | JWT y rol/ownership por operación; sin secretos |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-026/pasos.md) · E2E | Plan → contrato + backend → cliente → evidencia |

## Historial de validación

- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-026/pasos.md)); DoD completo.

## Notas y decisiones

- Ubicación y formato del contrato quedan pendientes de aprobación; no se modifica ningún contrato durante esta planificación.
- Decisión aprobada 2026-10-04 · D-21: contrato oficial en `llm-wiki/wiki/contratos-rest.md`.
