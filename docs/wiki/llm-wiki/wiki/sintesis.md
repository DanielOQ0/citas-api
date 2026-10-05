# Síntesis

## HECHOS vigentes

- El producto es un sistema ficticio de agendamiento de citas para formación, sin datos privados reales de FCV. Fuente: [PRD](../raw/prd-v1.0.md).
- El workspace contiene dos repositorios independientes: `citas-api` y `citas-web`. Fuente: [README](../raw/readme-workspace.md).
- El backend es Java 21, Spring Boot 3.5, Maven, JPA/JDBC, Flyway y MySQL 8.4; el frontend es Angular 21 (SPA) con TypeScript, que consume REST directamente, sin BFF. Fuente: [restricciones](../raw/restricciones-tecnicas.md).
- Esta es la única LLM Wiki global y reside en `citas-api/docs/wiki/llm-wiki/`. Fuente: [README API](../raw/readme-citas-api.md).

## Estado (2026-10-05)

- **HU-001 a HU-026 en `Lista`** y las 9 épicas en `Completada`, con evidencia de pruebas automatizadas y E2E exploratorio ([validación final](../../scrum/validacion-final-2026-10.md), [evidencias](../../scrum/evidencias/2026-10/README.md)).
- Las vistas están atadas al rol autenticado: sin selector de desarrollo; el rol se restaura desde `/api/me`; las rutas son lazy por rol con `canMatch` y la API devuelve 403 por rol (D-23).
- El contrato oficial está en [contratos REST](contratos-rest.md) (D-21). Las decisiones del cierre están en [decisiones](decisiones.md).
- Deuda vigente: hexagonal completa y Flyway desde cero ([riesgos](riesgos.md)).
