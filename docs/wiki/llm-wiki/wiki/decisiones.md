# Decisiones aprobadas

| ID | Decisión | Estado | Evidencia |
|---|---|---|---|
| D-001 | La LLM Wiki global se versiona en `citas-api/docs/wiki/llm-wiki/`. | adoptada | [README workspace](../raw/readme-workspace.md) |
| D-002 | El frontend consume directamente la API REST de Spring Boot; no habrá Express/BFF. | adoptada | [PRD](../raw/prd-v1.0.md), [restricciones](../raw/restricciones-tecnicas.md) |
| D-003 | El modelo de referencia de BD no se consulta ni ingiere antes de la comparación autorizada. | superada (2026-10-04): el usuario autorizó usar `database/reference/db.sql` como base del entorno (D-25) y para las cuentas seed | [plan de cierre](../../scrum/plan-cierre-2026-10.md) |
| D-01 … D-25 | Decisiones del cierre de HU (2026-10-04), aprobadas una por una por el usuario: alcance de arquitectura, ciclo de HU, estrategia E2E y evidencia, eliminación de SSR/Express, reglas de producto por HU, vistas por rol, Git y base de datos. | adoptadas | [plan-cierre-2026-10.md §2](../../scrum/plan-cierre-2026-10.md), [validación final](../../scrum/validacion-final-2026-10.md) |

## Resumen de las decisiones del cierre

- **Arquitectura (D-01):** reglas de cita, reprogramación y slots en el dominio puro `scheduling/domain`; el resto queda como deuda hexagonal.
- **Producto:**
  - Registro D-07.
  - Perfil (solo teléfono) D-08.
  - Afiliación `{epsId, planId}` D-09.
  - Inactivos D-10.
  - Borrado con 409 D-11.
  - Profesional inactivo D-12.
  - Bloques D-13.
  - Retenciones sin expiración D-14.
  - Motivo opcional D-15.
  - Motivo de rechazo obligatorio D-16.
  - Cascadas de reprogramación D-17.
  - `patientName` en la agenda D-18.
  - Cierre aplicable D-19.
  - Lectores del historial D-20.
- **Frontera (D-21, D-23):** contrato oficial en `contratos-rest.md`; rol desde `/api/me`, rutas lazy con `canMatch` y 403 de API por rol.
- **Operación (D-02 a D-05, D-22, D-24, D-25):**
  - HU cerradas en `Lista`.
  - E2E exploratorio con evidencia versionada.
  - SPA sin SSR.
  - UI con tokens existentes.
  - Commits locales con push solo con OK del usuario.
  - `db.sql` como base de Docker.

Las decisiones de implementación deben añadirse solo tras aprobación explícita y con fuente o evidencia trazable.
