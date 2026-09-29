# Contratos REST

## Estado

Contrato vigente de los incrementos S2–S4 (2026-09-29). HU-021 a HU-026 permanecen fuera de cierre S4.

## Identidad

Base URL local: `http://localhost:8080`.

| Método | Ruta | Entrada | Respuesta | Seguridad |
|---|---|---|---|---|
| POST | `/api/auth/register` | `firstName`, `lastName`, `documentType`, `documentNumber`, `email`, `phone`, `password` | `201` con `accessToken`, `refreshToken`, `expiresIn`, `user` | pública; crea `USER` |
| POST | `/api/auth/login` | `email`, `password` | `200` con tokens separados y roles | pública |
| POST | `/api/auth/refresh` | `refreshToken` | `200` con tokens rotados | pública; refresh previo queda revocado |
| POST | `/api/auth/logout` | `refreshToken` | `204` | pública; revoca el token |
| POST | `/api/auth/password-recovery` | `email` | `200` con `accepted`; token solo si `EXPOSE_RECOVERY_TOKEN=true` en desarrollo | pública |
| POST | `/api/auth/password-reset` | `token`, `password` | `204` | pública; token de un solo uso |
| GET | `/api/me` | — | `200` con `id`, `name`, `email`, `roles` | Bearer access JWT |

Los errores de credenciales devuelven `401` sin diferenciar email de contraseña. Validación de payload devuelve `400`; duplicados de email/documento devuelven `409`. El frontend consume directamente estas rutas mediante `AuthService` y permite cambiar la URL con `localStorage.fcv_api_url` (por defecto `http://localhost:8080`).

### Ejemplo de login

```json
{ "email": "carlos.perez@fcv.edu.co", "password": "password123" }
```

Las cuentas demo son sintéticas y solo se siembran cuando `SEED_DEMO_USERS=true`.

## Agenda S3

La API de agenda usa `/api/v1`. Las fechas usan `YYYY-MM-DD`, las horas `HH:mm` y la zona de negocio es `America/Bogota`.

| Método | Ruta | Actor | Resultado |
|---|---|---|---|
| GET | `/api/v1/catalogs/*` | público | sedes, regímenes, estados, planes activos y especialidades activas |
| GET/POST/PATCH | `/api/v1/admin/specialties` | ADMIN | consulta y gestión de especialidades de 30/60 min |
| GET/POST | `/api/v1/admin/professionals` | ADMIN | consulta y alta de profesionales sintéticos |
| PUT/PATCH | `/api/v1/admin/professionals/{id}/assignments|active` | ADMIN | asignaciones y habilitación |
| GET/POST/PATCH/DELETE | `/api/v1/professional/availability-blocks[/{id}]` | PROFESSIONAL | bloques propios futuros; editar/eliminar rechaza citas comprometidas |
| GET | `/api/v1/catalogs/professionals` | público | profesionales activos para mostrar datos de citas |
| GET | `/api/v1/availability` | público | franjas libres según sede, especialidad y fecha |
| POST | `/api/v1/appointments` | USER | `APPROVED` general o `REQUESTED` especializada; especializada exige `reason` y retiene slots |
| GET | `/api/v1/admin/appointments` | ADMIN | solicitudes pendientes con filtros `locationId`, `professionalId`, `specialtyId`, `date` |
| POST | `/api/v1/admin/appointments/{id}/decision` | ADMIN | aprobación o rechazo con motivo obligatorio |
| GET/PATCH | `/api/v1/users/me` | autenticado | perfil propio; solo teléfono editable |
| PUT | `/api/v1/users/me/affiliation` | USER | afiliación por plan activo |
| GET | `/api/v1/appointments` | USER | citas propias con filtros opcionales |
| POST | `/api/v1/appointments/{id}/cancel` | USER | cancelación propia futura no terminal |
| GET | `/api/v1/appointments/{id}/history` | autorizado | historial inmutable por ownership/rol |
| GET | `/api/v1/professional/appointments` | PROFESSIONAL | citas `APPROVED` propias, con filtros `from`, `to`, `locationId` |
| POST | `/api/v1/professional/appointments/{id}/close` | PROFESSIONAL | cierre posterior a la hora final como `COMPLETED` o `NO_SHOW` |

`POST /api/auth/register` acepta adicionalmente `insurancePlanId` opcional. Un plan inexistente o inactivo devuelve `400`; email o documento duplicados devuelven `409`. Una franja tomada entre búsqueda y reserva devuelve `409`.

`AppointmentItem` incluye `rejectionReason`, obtenido del último evento `REJECTED`, además de sede, profesional, especialidad, fecha, hora, duración y estado. Los endpoints de citas aplican ownership: el paciente solo ve y cancela sus propias citas.

## Restricciones que gobernarán el contrato

- REST/JSON entre `citas-web` y `citas-api`, sin BFF.
- Autorización por roles y ownership.
- Validación server-side y CORS explícito.
- El contrato debe documentarse durante el proyecto y cualquier cambio exige evidencia en ambos repositorios.

Fuente: [PRD, RF-20 y seguridad](../raw/prd-v1.0.md), [restricciones](../raw/restricciones-tecnicas.md).

No se deben inventar rutas, payloads, códigos de error ni paginación antes de HU/CA aprobadas.
