# Agente principal de `citas-api`

## Estado observado

Este repositorio está en fase de bootstrap: no hay `pom.xml`, código fuente ni HU/DoD generadas todavía. Solo existen documentación, plantillas y material de automatización. No supongas paquetes, módulos, endpoints, esquema ni comandos de build que el repositorio aún no declare.

## Alcance

Este agente implementa exclusivamente el backend `citas-api`:

- Java 21, Spring Boot 3.5.x y Maven cuando se inicialice el proyecto.
- REST/JSON, Spring Security, JWT de access y refresh token.
- MySQL 8.4, Spring Data JPA y migraciones Flyway.
- Reglas de negocio, pruebas y contratos que correspondan al backend.

No modificar `citas-web`, ni acoplar decisiones del backend a React o Angular.

## Fuentes de verdad

Leer antes de cualquier cambio, en este orden:

1. `../PRD.md` y `../RESTRICCIONES_TECNICAS.md`.
2. La HU y el DoD aprobados en `docs/wiki/scrum/`.
3. `README.md` y este archivo.
4. `docs/wiki/llm-wiki/wiki/index.md` y las páginas pertinentes, solo como contexto global.

Si no existe una HU/DoD aprobada para el cambio, no inventar alcance funcional: informar el bloqueo o solicitar la especificación. Actualmente las carpetas de Scrum no contienen HU ni DoD.

La LLM Wiki es global y la mantiene el orquestador. Este agente puede consultarla, pero no crearla ni actualizarla.

## Arquitectura obligatoria

- El dominio expresa reglas y modelos de negocio sin depender de Spring, JPA, HTTP ni adaptadores.
- La aplicación contiene los casos de uso y orquesta las reglas de dominio.
- Los puertos expresan dependencias de entrada y salida; las implementaciones externas son adaptadores.
- REST/HTTP y persistencia JPA son adaptadores. Los controladores traducen HTTP, validan el borde y delegan; no concentran reglas de negocio.
- La autenticación/autorización se integra como infraestructura, sin filtrar detalles de framework al dominio.

La estructura concreta de paquetes se decide al inicializar el proyecto y debe permitir comprobar estas dependencias, no al revés.

## Datos, seguridad y contratos

- Cada cambio de esquema requiere una migración Flyway y justificación ligada a su HU/DoD y reglas de 3FN.
- Usar datos sintéticos; no consultar ni copiar la solución de `../database/reference/` antes de la comparación autorizada.
- Secretos solo por variables de entorno; no abrir, mostrar ni versionar `.env`.
- Nunca registrar contraseñas, JWT, refresh tokens ni tokens de recuperación.
- Passwords usan hash adaptativo compatible con Spring Security; access y refresh token permanecen separados.
- Aplicar autorización por rol y ownership, validación server-side y CORS explícito según el contrato aprobado.
- No crear ni cambiar contratos REST sin coordinación con el orquestador cuando afecten al frontend.

## Flujo de trabajo por HU

1. Localizar la HU, criterios de aceptación y DoD aprobados.
2. Identificar reglas del PRD, puertos, adaptadores, persistencia y contrato afectados.
3. Publicar un plan con archivos y migraciones afectados antes de editar.
4. Implementar el mínimo coherente, manteniendo las fronteras hexagonales.
5. Ejecutar pruebas relevantes: dominio, aplicación y la integración REST/persistencia aplicable.
6. Verificar arquitectura, reglas, seguridad y DoD; documentar qué quedó sin verificar.

Mientras no exista proyecto Maven, no inventar comandos de prueba. Tras inicializarlo, ejecutar los comandos definidos por el propio repositorio y reportar sus resultados.

## Git y cambios ajenos

- Trabajar en `develop`; `main` representa puntos estables.
- Mantener commits trazables y no reescribir historial.
- Preservar cambios ajenos no relacionados. En particular, `.gitignore` tiene un cambio no atribuido a este agente y no debe incluirse ni revertirse sin instrucción explícita.
