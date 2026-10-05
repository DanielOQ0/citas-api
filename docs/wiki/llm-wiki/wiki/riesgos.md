# Riesgos

| Riesgo | Impacto | Tratamiento | Estado |
|---|---|---|---|
| Divergencia del contrato REST entre repositorios. | Integración rota. | Contrato oficial (D-21) contrastado contra backend (52/52 endpoints) y cliente (37 rutas). | mitigado |
| Concurrencia en reserva y reprogramación. | Doble reserva o liberación incorrecta. | `SELECT … FOR UPDATE` sobre slots y citas; retenciones liberadas al decidir; pruebas de conflicto e2e e integración. | mitigado |
| Exposición del token de recuperación en desarrollo. | Fuga de credenciales. | Solo con `EXPOSE_RECOVERY_TOKEN=true`; hash SHA-256 en BD; un uso; 30 min; respuesta indistinguible para cuentas inexistentes. | mitigado |
| Arquitectura hexagonal incompleta (D-01). | Incumple parcialmente RESTRICCIONES (dominio/aplicación/puertos/adaptadores). | Reglas de negocio ya en `scheduling/domain`; pendiente extraer puertos de persistencia (JDBC/JPA) y casos de uso por puerto. | deuda aceptada |
| Flyway no construye el esquema desde cero (V4 y V7 asumen el esquema de referencia). | Una base vacía sin `db.sql` no arranca. | D-25: `db.sql` montado como init de MySQL y reconstrucción verificada; reescribir V4/V7 por proveedor queda pendiente. | deuda aceptada |
| Las pruebas corren en H2 hasta V4 y el E2E en MySQL. | Diferencias de dialecto. | SQL portable (sin `UPDATE … JOIN`) y smoke/E2E sobre MySQL real. | mitigado |
| Datos E2E sintéticos acumulados en la BD de desarrollo. | Ruido en listados. | Correos `*.e2e.<run>@demo.invalid`; reinicio con `scripts/reset-db.ps1`. | aceptado |
