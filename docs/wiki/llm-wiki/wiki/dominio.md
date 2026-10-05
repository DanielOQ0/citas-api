# Dominio

## Actores

- **USER:** se registra, administra perfil/afiliación, busca disponibilidad y gestiona sus citas.
- **PROFESSIONAL:** creado por ADMIN; gestiona bloques de disponibilidad y consulta su propia agenda.
- **ADMIN:** gestiona catálogos configurables, profesionales, solicitudes especializadas y reprogramaciones.

Fuente: [PRD, sección 2](../raw/prd-v1.0.md).

## Reglas nucleares

- No puede existir doble reserva o retención de slots.
- La cita general se aprueba automáticamente; la especializada requiere decisión ADMIN.
- Una especialidad dura 30 o 60 minutos y los slots de 60 minutos deben ser consecutivos.
- No se crean bloques ni citas en el pasado.
- Rechazar, cancelar o rechazar una reprogramación libera la reserva que corresponda.
- La cita original se mantiene durante una reprogramación pendiente.
- Todo cambio de estado de cita debe auditarse.

Fuente: [PRD, RF-08 a RF-19 y RN-01 a RN-12](../raw/prd-v1.0.md).
