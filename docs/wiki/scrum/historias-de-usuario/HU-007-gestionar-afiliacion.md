---
id: HU-007
tipo: historia-de-usuario
titulo: "Gestionar afiliación"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Modelar afiliación y relación EPS-plan-régimen.** Dificultad: Medio. Requiere Flyway.
- [ ] **T-02 — Implementar validación de catálogo activo y ownership.** Dificultad: Medio.
- [ ] **T-03 — Construir selector cliente y pruebas de combinaciones inválidas.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Asociación válida
**Dado** catálogos activos compatibles **cuando** USER guarda su afiliación **entonces** puede consultarla posteriormente.
### CA-02 — Consistencia EPS-plan
**Dado** un plan que no pertenece a la EPS elegida **cuando** se intenta guardar **entonces** se rechaza.
### CA-03 — Sin duplicación
**Dado** una afiliación guardada **cuando** se inspeccionan datos **entonces** se usan referencias y no nombres repetidos en usuario.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Migración Flyway, claves/índices y 3FN justificados.
- [ ] Contrato, cliente y pruebas relevantes verificados; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- La cardinalidad final de afiliación debe documentarse al diseñar el modelo.
