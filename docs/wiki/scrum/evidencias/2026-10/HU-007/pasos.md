# HU-007 — Gestionar afiliación · Evidencia E2E

Fecha: 2026-10-04 · Sesión `pac` (`pac.e2e.042347@demo.invalid`, registrado sin plan).

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Perfil → EPS "EPS Demo Salud" → plan "Plan Subsidiado Demo" → Guardar; F5 | Afiliación consultable después | La lista de planes se limita a los de esa EPS (Contributivo Demo / Subsidiado Demo); régimen derivado "Subsidiado"; tras F5: "Vigente: EPS Demo Salud · Plan Subsidiado Demo · régimen Subsidiado" | [01-afiliacion-eps-plan-regimen.png](01-afiliacion-eps-plan-regimen.png) |
| CA-02 | `PUT /api/v1/users/me/affiliation` con `{epsId: 2, insurancePlanId: 1}` (plan de la EPS 1) | Rechazo | `400 "El plan no pertenece a la EPS seleccionada"` | — |
| CA-03 | Columnas de nombre de EPS/plan/régimen en `users` y fila de afiliación | Referencias, sin nombres repetidos | 0 columnas de nombre en `users`; afiliación `plan_id=2, is_current=1` (EPS y régimen se derivan del plan) | — |

Decisiones: `{epsId, insurancePlanId}` validados en servidor; una afiliación vigente y las anteriores quedan como historial; un plan o EPS inactivo no admite afiliaciones nuevas (D-09, D-10). Pruebas: `ProfileIntegrationTest.hu007AffiliationUsesCatalogReferencesAndValidatesEpsPlan`, `CatalogIntegrationTest.hu008EpsAndPlansCrudProtectReferencedRows`.
