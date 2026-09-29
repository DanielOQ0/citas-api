# Arquitectura

## HECHOS

- `citas-api` concentra negocio, persistencia, seguridad y API REST JSON con arquitectura hexagonal.
- `citas-web` concentra UI TypeScript; su framework se decidirá tras el flujo Stitch → Google AI Studio.
- No existe Express ni BFF: el navegador consumirá directamente Spring Boot por REST.
- La URL del backend debe configurarse por environment en el frontend.
- MySQL 8.4, JPA y Flyway son parte de la arquitectura objetivo.
- El entorno Docker de desarrollo arranca MySQL, Spring Boot y Angular con `docker compose up -d`; Angular se publica en `localhost:4200` y la API en `localhost:8080`.
- La disponibilidad operativa de la API se comprueba con `GET /actuator/health`.

Fuente: [restricciones técnicas](../raw/restricciones-tecnicas.md) y [README workspace](../raw/readme-workspace.md).

## Coordinación

Un cambio REST exige plan previo, actualización de contrato, backend y frontend, más evidencia verificable en ambos repositorios.

## Incremento de identidad verificado

- `citas-api`: `AuthController`, `AuthService`, filtro JWT, entidades `users`/`roles`/`user_roles`/`refresh_tokens` y Flyway `V1__auth.sql`.
- `citas-web`: `AuthService`, `authGuard`, formulario de login y `provideHttpClient`.
- Verificación: `docker compose run --rm citas-api-dev mvn -q test` y `npm run build`.
