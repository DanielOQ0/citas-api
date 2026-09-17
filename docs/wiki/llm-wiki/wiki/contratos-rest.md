# Contratos REST

## Estado

No existe contrato REST aprobado ni implementado.

## Restricciones que gobernarán el contrato

- REST/JSON entre `citas-web` y `citas-api`, sin BFF.
- Autorización por roles y ownership.
- Validación server-side y CORS explícito.
- El contrato debe documentarse durante el proyecto y cualquier cambio exige evidencia en ambos repositorios.

Fuente: [PRD, RF-20 y seguridad](../raw/prd-v1.0.md), [restricciones](../raw/restricciones-tecnicas.md).

No se deben inventar rutas, payloads, códigos de error ni paginación antes de HU/CA aprobadas.
