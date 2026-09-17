---
id: HU-009
tipo: historia-de-usuario
titulo: "Gestionar especialidades"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Modelar catálogo, estado, unicidad y Flyway.** Dificultad: Medio.
- [ ] **T-02 — Implementar CRUD ADMIN y cliente.** Dificultad: Alto.
- [ ] **T-03 — Probar catálogo referenciado/inactivo y roles.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — CRUD ADMIN
**Dado** ADMIN **cuando** administra una especialidad válida **entonces** queda disponible para asignación.
### CA-02 — Sin borrado referenciado
**Dado** especialidad usada **cuando** se intenta borrar **entonces** se conserva y se ofrece desactivación.
### CA-03 — Restricción de uso
**Dado** especialidad inactiva o no asociada **cuando** se intenta reservar **entonces** no es elegible.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Migración, referencias 3FN y controles ADMIN verificados.
- [ ] Contrato/pruebas relevantes y trazabilidad actualizados.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Medicina General debe ser un valor administrado/seed claramente identificable.
