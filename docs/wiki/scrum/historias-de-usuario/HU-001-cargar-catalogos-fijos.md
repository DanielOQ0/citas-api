---
id: HU-001
tipo: historia-de-usuario
titulo: "Cargar catálogos fijos"
estado: Lista
epica: "[[EP-001-identidad-y-seguridad]]"
esfuerzo: Medio
sprint_sugerido: S2
dependencias: []
relacionadas: ["[[HU-008-gestionar-eps-y-planes]]", "[[HU-009-gestionar-especialidades]]"]
---
# HU-001 — Cargar catálogos fijos
## Historia de usuario
**COMO** sistema **QUIERO** disponer de roles, estados de cita/reprogramación, regímenes y sedes fijos **PARA** aplicar reglas consistentes.
## Alcance
- Precargar y exponer como solo lectura los catálogos definidos, incluidas HIC e ICV.
## Fuera de alcance
- CRUD administrativo de catálogos fijos.
## Reglas de negocio
- Son datos de seed y no se alteran desde operaciones de negocio.
## Dependencias y relaciones
- Épica: [[EP-001-identidad-y-seguridad]]; habilita [[HU-002-registrar-usuario]] y [[HU-010-registrar-profesional]].
## Esfuerzo
**Nivel:** Medio. **Justificación:** datos de referencia compartidos por seguridad, agenda y afiliación.
## Tareas de desarrollo
- [x] **T-01 — Modelar catálogos y claves de referencia.** Dificultad: Medio. Sin listas ni textos de estado duplicados.
- [x] **T-02 — Incorporar seed/migración y consulta de solo lectura.** Dificultad: Medio. Compatible con Flyway.
- [x] **T-03 — Verificar datos sintéticos y restricciones.** Dificultad: Bajo. Sin datos personales reales.
## Criterios de aceptación
### CA-01 — Catálogos disponibles
**Dado** una instalación inicial **cuando** se consulta un catálogo fijo **entonces** contiene los valores del PRD y las dos sedes indicadas.
### CA-02 — Inmutabilidad funcional
**Dado** un actor autenticado **cuando** intenta crear, editar o borrar un catálogo fijo **entonces** la operación no está permitida.
### CA-03 — Referencias coherentes
**Dado** una entidad que usa rol, régimen o estado **cuando** se persiste **entonces** usa referencia de catálogo y no texto divergente.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Existe migración Flyway/seed coherente y reversible por reconstrucción de entorno.
- [x] Se verifican claves, unicidad y datos sintéticos.
- [x] Trazabilidad Scrum actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-001/pasos.md) · `GET /catalogs/*` | Roles, estados, regímenes y sedes HIC/ICV del PRD |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-001/pasos.md) · `CatalogIntegrationTest` | POST/PUT/DELETE sobre catálogos fijos → 405 |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-001/pasos.md) · `information_schema` | Referencias por FK a roles, estados, regímenes y sedes |
| DoD | Verificado | [pasos](../evidencias/2026-10/HU-001/pasos.md) · D-25 · MySQL temporal | Reconstrucción `db.sql` + Flyway baseline/V2–V7 verificada |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-001/pasos.md)); DoD completo.

## Notas y decisiones
- La forma de identificar Medicina General debe documentarse al implementar catálogo de especialidades.
- Decisión aprobada 2026-10-04 · D-06: `GET /api/v1/catalogs/roles`; Medicina General = `is_general`/`MEDICINA_GENERAL`; filtro tipo = `requiresAdminApproval`.
- Decisión aprobada 2026-10-04 · D-25: `db.sql` montado como init de MySQL; que Flyway construya el esquema desde cero queda como deuda documentada.
