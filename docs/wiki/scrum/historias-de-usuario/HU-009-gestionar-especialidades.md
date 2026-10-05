---
id: HU-009
tipo: historia-de-usuario
titulo: "Gestionar especialidades"
estado: Lista
epica: "[[EP-002-perfil-y-catalogos]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-001-cargar-catalogos-fijos]]"]
relacionadas: ["[[HU-012-definir-duracion-especialidad]]"]
---
# HU-009 — Gestionar especialidades
## Historia de usuario
**COMO** ADMIN **QUIERO** gestionar especialidades **PARA** configurar la oferta que pueden atender los profesionales.
## Alcance
- CRUD administrativo y activación/desactivación de especialidades.
## Fuera de alcance
- Borrado físico de especialidad usada en transacciones.
## Reglas de negocio
- Especialidad reservable debe estar activa y asociada al profesional; nombre no se repite en citas/profesional.
## Dependencias y relaciones
- Épica: [[EP-002-perfil-y-catalogos]]. Depende de [[HU-001-cargar-catalogos-fijos]]; habilita [[HU-010-registrar-profesional]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** catálogo transversal, integridad y autorización.
## Tareas de desarrollo
- [x] **T-01 — Modelar catálogo, estado, unicidad y Flyway.** Dificultad: Medio.
- [x] **T-02 — Implementar CRUD ADMIN y cliente.** Dificultad: Alto.
- [x] **T-03 — Probar catálogo referenciado/inactivo y roles.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — CRUD ADMIN
**Dado** ADMIN **cuando** administra una especialidad válida **entonces** queda disponible para asignación.
### CA-02 — Sin borrado referenciado
**Dado** especialidad usada **cuando** se intenta borrar **entonces** se conserva y se ofrece desactivación.
### CA-03 — Restricción de uso
**Dado** especialidad inactiva o no asociada **cuando** se intenta reservar **entonces** no es elegible.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Migración, referencias 3FN y controles ADMIN verificados.
- [x] Contrato/pruebas relevantes y trazabilidad actualizados.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-009/pasos.md) · 01 | CRUD ADMIN; especialidad disponible para asignación |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-009/pasos.md) · 02 | En uso → 409 y desactivación |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-009/pasos.md) · + HU-015 | Inactiva o no asociada: no se ofrece y la reserva → 400 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-009/pasos.md)); DoD completo.

## Notas y decisiones
- Medicina General debe ser un valor administrado/seed claramente identificable.
- Decisión aprobada 2026-10-04 · D-11: DELETE con 409 si la usan citas o profesionales.
- Decisión aprobada 2026-10-04 · Medicina General identificada por `is_general` (D-06).
