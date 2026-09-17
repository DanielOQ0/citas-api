---
id: HU-026
tipo: historia-de-usuario
titulo: "Documentar contrato REST"
estado: Pendiente de aprobación
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

- [ ] **T-01 — Inventariar capacidades aprobadas y sus recursos/operaciones.**  
  Dificultad: Medio. Describir intención, autorización, payload y respuesta sin inventar endpoints prematuros.
- [ ] **T-02 — Definir convenciones JSON y errores verificables.**  
  Dificultad: Alto. Incluir validación, conflicto de slot, autenticación y autorización donde apliquen.
- [ ] **T-03 — Alinear documentación con `citas-api` y `citas-web`.**  
  Dificultad: Alto. Aplicar el plan cross-repo antes de modificar un contrato.
- [ ] **T-04 — Añadir verificación de contrato en funcionalidades clave.**  
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

- [ ] CA-01 a CA-03 están validados con evidencia para todas las funcionalidades implementadas dentro del alcance aprobado.
- [ ] La documentación no prescribe Express/BFF y refleja REST JSON directo a `citas-api`.
- [ ] Se verifican coherencia de autenticación, autorización, errores de validación y conflictos de reserva.
- [ ] Cualquier cambio contractual tiene plan cross-repo, pruebas/evidencia aplicables y trazabilidad Scrum actualizada.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| DoD | Pendiente | — | — |

## Historial de validación

- 2026-09-17 — Creada en `Pendiente de aprobación`.

## Notas y decisiones

- Ubicación y formato del contrato quedan pendientes de aprobación; no se modifica ningún contrato durante esta planificación.
