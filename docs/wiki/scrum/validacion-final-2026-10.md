# Validación final — Cierre de HU (2026-10)

Fecha: 2026-10-05 · Alcance: HU-001 a HU-026 · Plan: [[plan-cierre-2026-10]] · Reemplaza como resumen vigente a [[validacion-s2-s4]], que se conserva como histórico.

## Resultado

Las **26 HU están en `Lista`** y las 9 épicas en `Completada`. Cada HU tiene su tabla de evidencia, con CA verificados y enlace a `evidencias/2026-10/HU-0XX/pasos.md`, el DoD marcado, el historial y las decisiones D-xx aplicadas.

| Incremento | HU | Evidencia automatizada (`citas-api`) | Evidencia E2E (`playwright-cli`) |
|---|---|---|---|
| Vistas por rol | transversal (D-23) | `RoleAccessIntegrationTest` (contenedor servlet real) | [roles/](evidencias/2026-10/roles/pasos.md) |
| S2 — Fundaciones | HU-001 a HU-010 | `CatalogIntegrationTest`, `AuthIntegrationTest`, `AuthFlowIntegrationTest`, `ProfileIntegrationTest`, `ProfessionalAgendaIntegrationTest` | HU-001 a HU-010 |
| S3 — Agenda reservable | HU-011 a HU-016 | `ProfessionalAgendaIntegrationTest`, `AppointmentFlowIntegrationTest`, `SlotPolicyTest` | HU-011 a HU-016 |
| S4 — Flujo administrativo | HU-017 a HU-020 | `AppointmentFlowIntegrationTest`, `AppointmentRulesTest` | HU-017 a HU-020 |
| S5 — Continuidad asistencial | HU-021 a HU-024 | `RescheduleIntegrationTest`, `AppointmentFlowIntegrationTest` | HU-021 a HU-024 (HU-024 con fixture SQL, D-19) |
| S6 — Trazabilidad y frontera | HU-025, HU-026 | `AppointmentFlowIntegrationTest`, `RescheduleIntegrationTest`, verificación de cobertura del contrato | HU-025, HU-026 |

## Números

- `citas-api`: **45 pruebas, 0 fallos**. Son 12 unitarias de dominio y 33 de integración (H2 en modo MySQL, migraciones V1–V4 y reloj de negocio controlado).
- `citas-web`: **12 pruebas unitarias** (sesión, guards y utilidades), `ng build` de producción dentro de los presupuestos y `ng lint` sin hallazgos (incluye reglas de accesibilidad de plantillas).
- Contrato: **52/52 endpoints** documentados en `contratos-rest.md`; las 37 rutas consumidas por el cliente están documentadas.
- E2E: corrida `042347` sobre el stack Docker (MySQL real), con 27 carpetas de evidencia y capturas por HU.

## Defectos corregidos durante el cierre (descubiertos en la verificación)

1. Selector de desarrollo "Vista" y usuarios mock: tras F5 el rol volvía a "Paciente" (D-23).
2. Todo 4xx de un endpoint autenticado llegaba al cliente como `401` vacío, por el reenvío a `/error` bloqueado por Spring Security.
3. Endpoints de paciente sin control de rol: ADMIN podía reservar citas.
4. Decisiones ADMIN sin validar el estado (`REQUESTED`/`PENDING`), y reprogramación sin auditoría, sin unicidad PENDING, sin validar fecha futura y con SQL no portable.
5. Retenciones de reprogramación que nunca se liberaban (`UNIQUE slot_id`) y cancelación que no liberaba la reprogramación pendiente.
6. Disponibilidad de 60 min sin exigir el segundo slot consecutivo, con slots pasados, especialidades inactivas y sedes no asignadas.
7. Desfase de 5 h entre la API (UTC) y MySQL (Bogotá) en reservas, cierres y tokens de recuperación.
8. Duración de la cita tomada de la especialidad vigente y no de la propia cita.
9. Reconstrucción del entorno imposible (`db.sql` no montado; Flyway desde cero falla en V4); corregido según D-25.

## Comandos reproducibles

```text
docker compose run --rm --no-deps citas-api-dev mvn -B clean test
docker exec fcv-citas-web-dev sh -lc "cd /workspace && npm run build && npm test -- --watch=false && npm run lint"
source citas-api/docs/wiki/scrum/evidencias/2026-10/e2e-helpers.sh   # E2E exploratorio (Git Bash)
```

## Deuda y riesgos aceptados

- Arquitectura hexagonal completa pendiente: solo las reglas tocadas se extrajeron al dominio puro (D-01).
- Flyway no construye el esquema desde cero: la base oficial es `db.sql` montado en Docker (D-25).
- La BD de desarrollo acumula datos E2E sintéticos de la corrida `042347` (aceptado en D-03; se reinicia con `scripts/reset-db.ps1`).
