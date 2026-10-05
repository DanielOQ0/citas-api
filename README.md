# citas-api

Backend del sistema ficticio de agendamiento de citas FCV: Java 21, Spring Boot 3.5, Maven, Spring Security con JWT access/refresh, JPA/JDBC, Flyway y MySQL 8.4.

## Ejecutar y probar

- Desde la raíz del workspace, `docker compose up -d` levanta MySQL (inicializado con `database/reference/db.sql`), la API en `http://localhost:8080` (salud en `/actuator/health`) y el frontend.
- Pruebas: `docker compose run --rm --no-deps citas-api-dev mvn -B test` (dominio + integración sobre H2 en modo MySQL).
- Configuración solo por variables de entorno (`.env.example`); nunca se versiona `.env`.

## Documentación compartida

- `docs/wiki/scrum/`: épicas, HU (todas `Lista`), [plan de cierre](docs/wiki/scrum/plan-cierre-2026-10.md), [validación final](docs/wiki/scrum/validacion-final-2026-10.md) y evidencias E2E.
- `docs/wiki/llm-wiki/`: única LLM Wiki global; contrato REST oficial en `wiki/contratos-rest.md`.
- `automations/n8n/`: JSON exportados en S5/S6.
