# HU-026 — Documentar contrato REST · Evidencia

Fecha: 2026-10-05 · Contrato oficial: [`llm-wiki/wiki/contratos-rest.md`](../../../llm-wiki/wiki/contratos-rest.md) (D-21).

| CA | Verificación | Resultado |
|---|---|---|
| CA-01 | Script que extrae las rutas de `*Controller.java` (`@RequestMapping` + `@Get/Post/Put/Patch/DeleteMapping`) y las busca en el contrato | **52 endpoints, 52 documentados, 0 faltantes**; cada fila indica actor/rol, entrada, respuesta y errores (`400/401/403/404/405/409`) |
| CA-01 | Rutas consumidas por `citas-web` (`scheduling-api.service.ts`, `auth.service.ts`, `session.service.ts`) contra el contrato | 37 rutas, todas documentadas (`…/reschedule-requests/{requestId}/keep` figura con su nombre de parámetro) |
| CA-02 | Operaciones privadas: autenticación y límites | Convenciones: Bearer access JWT, access/refresh separados, CORS explícito; cada sección declara el rol (ADMIN, PROFESSIONAL, USER) y el ownership; el contrato no contiene tokens, claves ni credenciales reales (se retiró el ejemplo de login con clave) |
| CA-02 | Formato de error verificable | `{status, error, message, fieldErrors, path}` probado sobre el contenedor servlet real (`RoleAccessIntegrationTest.errorsKeepStatusAndUniformBody`) y en cada flujo E2E |
| CA-03 | Secuencia cross-repo de los cambios de contrato | Plan con repos y archivos afectados ([plan-cierre-2026-10.md](../../../plan-cierre-2026-10.md) §4 "Cambios de contrato") → contrato + backend (`citas-api` `83a884d`) → cliente (`citas-web` `bb6546e`) → evidencia E2E (esta carpeta) |
| DoD | Sin Express/BFF | El contrato prescribe REST directo; `citas-web` ya no incluye Express ni SSR (D-05, `citas-web` `a2835a8`) |
| DoD | Conflictos de reserva y autorización | `409` de franja tomada o retenida (HU-016, HU-017), `403` por rol y ownership (roles, HU-019, HU-023, HU-025) |
