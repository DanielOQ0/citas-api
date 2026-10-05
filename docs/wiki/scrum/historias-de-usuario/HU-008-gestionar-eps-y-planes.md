---
id: HU-008
tipo: historia-de-usuario
titulo: "Gestionar EPS y planes"
estado: Lista
epica: "[[EP-002-perfil-y-catalogos]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-001-cargar-catalogos-fijos]]"]
relacionadas: ["[[HU-007-gestionar-afiliacion]]"]
---
# HU-008 — Gestionar EPS y planes
## Historia de usuario
**COMO** ADMIN **QUIERO** gestionar EPS y sus planes **PARA** ofrecer opciones de afiliación vigentes.
## Alcance
- CRUD de EPS/planes y activación/desactivación cuando hay referencias.
## Fuera de alcance
- Borrado físico de catálogo referenciado.
## Reglas de negocio
- Cada plan pertenece a EPS; un catálogo referenciado se desactiva, no se elimina.
## Dependencias y relaciones
- Épica: [[EP-002-perfil-y-catalogos]]. Depende de [[HU-001-cargar-catalogos-fijos]]; habilita [[HU-007-gestionar-afiliacion]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** administración, integridad referencial y UI.
## Tareas de desarrollo
- [x] **T-01 — Modelar EPS-plan con unicidad, estado y Flyway.** Dificultad: Alto.
- [x] **T-02 — Crear operaciones ADMIN, autorización y vistas.** Dificultad: Alto.
- [x] **T-03 — Probar referencias, desactivación y denegación a no ADMIN.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Administración autorizada
**Dado** ADMIN autenticado **cuando** crea, edita o consulta EPS/planes **entonces** los cambios válidos quedan disponibles.
### CA-02 — Relación válida
**Dado** un plan **cuando** se guarda **entonces** pertenece a una EPS existente y activa según la operación.
### CA-03 — Protección de referencias
**Dado** catálogo usado por una afiliación **cuando** ADMIN intenta eliminarlo **entonces** no se borra físicamente y puede desactivarse.
### CA-04 — Restricción de rol
**Dado** USER o PROFESSIONAL **cuando** invoca administración **entonces** se deniega.
## Definition of Done
- [x] CA-01 a CA-04 validados con evidencia.
- [x] Migración Flyway y restricciones de integridad verificadas.
- [x] Pruebas REST/cliente/rol relevantes disponibles; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-008/pasos.md) · 01 | Crear, editar y consultar EPS y planes |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-008/pasos.md) · 01 | Plan único por EPS y referencias válidas |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-008/pasos.md) · 02/03 | En uso → 409 y desactivación; sin uso → borrado físico |
| CA-04 | Verificado | [pasos](../evidencias/2026-10/HU-008/pasos.md) · API | USER/PROFESSIONAL → 403 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03, CA-04 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-008/pasos.md)); DoD completo.

## Notas y decisiones
- Definir si “inactivo” impide nuevas afiliaciones; se recomienda que sí, sujeto a aprobación.
- Decisión aprobada 2026-10-04 · D-10: inactivo bloquea solo afiliaciones nuevas; la EPS inactiva inhabilita sus planes.
- Decisión aprobada 2026-10-04 · D-11: DELETE con 409 cuando está referenciado.
