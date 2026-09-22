# Síntesis

## HECHOS vigentes

- El producto es un sistema ficticio de agendamiento de citas para formación, sin datos privados reales de FCV. Fuente: [PRD](../raw/prd-v1.0.md).
- El workspace contiene dos repositorios independientes: `citas-api` y `citas-web`. Fuente: [README](../raw/readme-workspace.md).
- El backend objetivo es Java 21, Spring Boot 3.5.x, Maven, arquitectura hexagonal, MySQL y Flyway. Fuente: [restricciones](../raw/restricciones-tecnicas.md).
- El frontend será React o Angular con TypeScript y consumirá REST directamente, sin BFF. Fuente: [restricciones](../raw/restricciones-tecnicas.md).
- Esta es la única LLM Wiki global y reside en `citas-api/docs/wiki/llm-wiki/`. Fuente: [README API](../raw/readme-citas-api.md).

## Estado

Existe un primer incremento funcional de identidad: Spring Boot 3.5 + JPA/Flyway/JWT en `citas-api` y Angular en `citas-web`. Login consume REST real, persiste access/refresh en cliente, redirige por el rol devuelto por backend y protege el shell con guard. El contrato está documentado en [contratos REST](contratos-rest.md).

HU-003 está marcada como lista con evidencia de integración. HU-002 y HU-004 siguen pendientes por falta de formulario de registro conectado y cierre/renovación integrados en UI, respectivamente. El resto de historias no está implementado.

Consultar [preguntas abiertas](preguntas-abiertas.md) y [riesgos](riesgos.md) antes de diseñar o implementar.
