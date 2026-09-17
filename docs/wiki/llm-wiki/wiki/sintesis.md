# Síntesis

## HECHOS vigentes

- El producto es un sistema ficticio de agendamiento de citas para formación, sin datos privados reales de FCV. Fuente: [PRD](../raw/prd-v1.0.md).
- El workspace contiene dos repositorios independientes: `citas-api` y `citas-web`. Fuente: [README](../raw/readme-workspace.md).
- El backend objetivo es Java 21, Spring Boot 3.5.x, Maven, arquitectura hexagonal, MySQL y Flyway. Fuente: [restricciones](../raw/restricciones-tecnicas.md).
- El frontend será React o Angular con TypeScript y consumirá REST directamente, sin BFF. Fuente: [restricciones](../raw/restricciones-tecnicas.md).
- Esta es la única LLM Wiki global y reside en `citas-api/docs/wiki/llm-wiki/`. Fuente: [README API](../raw/readme-citas-api.md).

## Estado

No hay aplicación inicial, HU/DoD aprobadas ni contrato REST. La implementación funcional permanece fuera del alcance de este bootstrap documental.

Consultar [preguntas abiertas](preguntas-abiertas.md) y [riesgos](riesgos.md) antes de diseñar o implementar.
