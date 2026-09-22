# Contratos REST

## Estado

Contrato inicial implementado y verificado para identidad (incremento S2, 2026-09-22). El resto del dominio permanece pendiente.

## Identidad

Base URL local: `http://localhost:8080`.

| Método | Ruta | Entrada | Respuesta | Seguridad |
|---|---|---|---|---|
| POST | `/api/auth/register` | `firstName`, `lastName`, `documentType`, `documentNumber`, `email`, `phone`, `password` | `201` con `accessToken`, `refreshToken`, `expiresIn`, `user` | pública; crea `USER` |
| POST | `/api/auth/login` | `email`, `password` | `200` con tokens separados y roles | pública |
| POST | `/api/auth/refresh` | `refreshToken` | `200` con tokens rotados | pública; refresh previo queda revocado |
| POST | `/api/auth/logout` | `refreshToken` | `204` | pública; revoca el token |
| GET | `/api/me` | — | `200` con `id`, `name`, `email`, `roles` | Bearer access JWT |

Los errores de credenciales devuelven `401` sin diferenciar email de contraseña. Validación de payload devuelve `400`; duplicados de email/documento devuelven `409`. El frontend consume directamente estas rutas mediante `AuthService` y permite cambiar la URL con `localStorage.fcv_api_url` (por defecto `http://localhost:8080`).

### Ejemplo de login

```json
{ "email": "carlos.perez@fcv.edu.co", "password": "password123" }
```

Las cuentas demo son sintéticas y solo se siembran cuando `SEED_DEMO_USERS=true`.

## Restricciones que gobernarán el contrato

- REST/JSON entre `citas-web` y `citas-api`, sin BFF.
- Autorización por roles y ownership.
- Validación server-side y CORS explícito.
- El contrato debe documentarse durante el proyecto y cualquier cambio exige evidencia en ambos repositorios.

Fuente: [PRD, RF-20 y seguridad](../raw/prd-v1.0.md), [restricciones](../raw/restricciones-tecnicas.md).

No se deben inventar rutas, payloads, códigos de error ni paginación antes de HU/CA aprobadas.
