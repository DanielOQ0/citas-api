# Log de la LLM Wiki

| Fecha | Operación | Resultado |
|---|---|---|
| 2026-09-15 | INGEST | Se incorporaron las fuentes normativas y de gobierno iniciales; se excluyó `database/reference/` para preservar la actividad de 3FN. |
| 2026-09-15 | LEARN | Se registró que ambos repositorios parten en `main`, sin `develop`; la rama de trabajo de `citas-api` fue creada para documentar esta Wiki. |
| 2026-09-22 | BUILDER/VERIFIER | Se implementó y verificó el incremento de identidad: registro USER, login JWT access/refresh, `/api/me`, refresh rotado, logout revocable y consumo Angular. `mvn test` y `npm run build` pasan. HU-003 lista; HU-002/HU-004 pendientes. |
| 2026-09-24 | BUILDER/VERIFIER | Se corrigió el Compose de desarrollo: los servicios de API y Angular dejaron de quedar ociosos con `tail -f /dev/null`, se añadieron healthchecks y se definió `localhost:4200` como acceso web. |
| 2026-09-24 | BUILDER/VERIFIER | Se añadió V2 de agenda: catálogos, perfiles profesionales, bloques/slots, reserva general/especializada, decisión ADMIN, afiliación opcional, perfil, cancelación e historial. La prueba focalizada de doble reserva quedó verde tras dos correcciones; `mvn test` y `npm run build` se ejecutaron en Docker. |
| 2026-09-29 | BUILDER/VERIFIER | S2–S4: refresh automático y logout real en Angular, recuperación/cambio de contraseña, sincronización del usuario autenticado, filtros de solicitudes, motivo y retención de citas especializadas, historial/motivo de rechazo en Mis citas, ownership/cancelación, agenda profesional y edición/eliminación segura de bloques. `npm run build`, `npm test -- --watch=false` y `mvn test` en Docker quedan verdes tras hacer V5–V7 compatibles con H2/MySQL. HU-021–HU-026 siguen fuera del cierre S4. |
