# Validación S2–S4

Fecha: 2026-09-29 · Alcance: HU-001 a HU-020.

| Incremento | HU | Evidencia automatizada | Evidencia UI/contrato | Estado |
|---|---|---|---|---|
| S2 | HU-001–HU-010 | `AuthIntegrationTest`; autorización ADMIN en servicios; migraciones Flyway validadas | Login/registro/recuperación, usuario real, catálogo y afiliación consumen REST | Validado en build/pruebas; CRUD de EPS/planes requiere datos de entorno |
| S3 | HU-011–HU-016 | `SchedulingIntegrationTest`: slots, reserva, doble reserva, cierre; ownership de bloques | Disponibilidad, bloques editables/eliminables y agenda profesional | Validado en build/pruebas |
| S4 | HU-017–HU-020 | reserva general/especializada, decisión ADMIN, liberación de slots, cancelación e historial | motivo especializado, bandeja con filtros, Mis citas con datos REST, estados loading/empty/error | Validado en build/pruebas |

Comandos reproducibles:

```text
docker compose run --rm citas-api-dev mvn test -q
cd citas-web; npm install; npm run build; npm test -- --watch=false
```

Fuera de alcance S4: HU-021 solicitar reprogramación, HU-022 decidir reprogramación, HU-023 agenda propia completa, HU-024 cierre de atención, HU-025 historial de estados como HU independiente y HU-026 contrato REST definitivo.
