---
id: HU-001
tipo: historia-de-usuario
titulo: "Cargar catálogos fijos"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Modelar catálogos y claves de referencia.** Dificultad: Medio. Sin listas ni textos de estado duplicados.
- [ ] **T-02 — Incorporar seed/migración y consulta de solo lectura.** Dificultad: Medio. Compatible con Flyway.
- [ ] **T-03 — Verificar datos sintéticos y restricciones.** Dificultad: Bajo. Sin datos personales reales.
## Criterios de aceptación
### CA-01 — Catálogos disponibles
**Dado** una instalación inicial **cuando** se consulta un catálogo fijo **entonces** contiene los valores del PRD y las dos sedes indicadas.
### CA-02 — Inmutabilidad funcional
**Dado** un actor autenticado **cuando** intenta crear, editar o borrar un catálogo fijo **entonces** la operación no está permitida.
### CA-03 — Referencias coherentes
**Dado** una entidad que usa rol, régimen o estado **cuando** se persiste **entonces** usa referencia de catálogo y no texto divergente.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Existe migración Flyway/seed coherente y reversible por reconstrucción de entorno.
- [ ] Se verifican claves, unicidad y datos sintéticos.
- [ ] Trazabilidad Scrum actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- La forma de identificar Medicina General debe documentarse al implementar catálogo de especialidades.
