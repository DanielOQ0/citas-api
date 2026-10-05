---
id: HU-007
tipo: historia-de-usuario
titulo: "Gestionar afiliación"
estado: Lista
epica: "[[EP-002-perfil-y-catalogos]]"
esfuerzo: Medio
sprint_sugerido: S3
dependencias: ["[[HU-001-cargar-catalogos-fijos]]", "[[HU-006-gestionar-perfil]]", "[[HU-008-gestionar-eps-y-planes]]"]
relacionadas: []
---
# HU-007 — Gestionar afiliación
## Historia de usuario
**COMO** USER autenticado **QUIERO** asociar mi EPS, plan y régimen **PARA** mantener mi afiliación sin información duplicada.
## Alcance
- Crear/actualizar y consultar afiliación propia por referencias de catálogo.
## Fuera de alcance
- Datos de cobertura, facturación o creación de EPS desde perfil.
## Reglas de negocio
- No repetir nombres de EPS/régimen/plan dentro de USER; el plan debe corresponder a su EPS.
## Dependencias y relaciones
- Épica: [[EP-002-perfil-y-catalogos]]. Depende de [[HU-001-cargar-catalogos-fijos]], [[HU-006-gestionar-perfil]] y [[HU-008-gestionar-eps-y-planes]].
## Esfuerzo
**Nivel:** Medio. **Justificación:** integridad referencial, 3FN y ownership.
## Tareas de desarrollo
- [x] **T-01 — Modelar afiliación y relación EPS-plan-régimen.** Dificultad: Medio. Requiere Flyway.
- [x] **T-02 — Implementar validación de catálogo activo y ownership.** Dificultad: Medio.
- [x] **T-03 — Construir selector cliente y pruebas de combinaciones inválidas.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Asociación válida
**Dado** catálogos activos compatibles **cuando** USER guarda su afiliación **entonces** puede consultarla posteriormente.
### CA-02 — Consistencia EPS-plan
**Dado** un plan que no pertenece a la EPS elegida **cuando** se intenta guardar **entonces** se rechaza.
### CA-03 — Sin duplicación
**Dado** una afiliación guardada **cuando** se inspeccionan datos **entonces** se usan referencias y no nombres repetidos en usuario.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Migración Flyway, claves/índices y 3FN justificados.
- [x] Contrato, cliente y pruebas relevantes verificados; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-007/pasos.md) · 01 | EPS → plan → régimen guardado y consultable tras F5 |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-007/pasos.md) · API | Plan de otra EPS → 400 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-007/pasos.md) · BD | Afiliación por `plan_id`; sin nombres de catálogo en `users` |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-007/pasos.md)); DoD completo.

## Notas y decisiones
- La cardinalidad final de afiliación debe documentarse al diseñar el modelo.
- Decisión aprobada 2026-10-04 · D-09: `{epsId, insurancePlanId}` validados; una afiliación vigente y el resto como historial.
