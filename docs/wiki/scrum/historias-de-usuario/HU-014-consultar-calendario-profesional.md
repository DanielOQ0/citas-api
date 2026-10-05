---
id: HU-014
tipo: historia-de-usuario
titulo: "Consultar calendario profesional"
estado: Lista
epica: "[[EP-004-disponibilidad-profesional]]"
esfuerzo: Bajo
sprint_sugerido: S3
dependencias: ["[[HU-013-publicar-bloques-disponibilidad]]"]
relacionadas: []
---
# HU-014 — Consultar calendario profesional
## Historia de usuario
**COMO** PROFESSIONAL **QUIERO** consultar mi calendario de bloques **PARA** revisar mi disponibilidad publicada.
## Alcance
- Vista de bloques propios por fecha/sede.
## Fuera de alcance
- Agenda de citas aprobadas (ver [[HU-023-consultar-agenda-propia]]).
## Reglas de negocio
- Solo el propietario consulta sus bloques.
## Dependencias y relaciones
- Épica: [[EP-004-disponibilidad-profesional]]. Depende de [[HU-013-publicar-bloques-disponibilidad]].
## Esfuerzo
**Nivel:** Bajo. **Justificación:** lectura con filtro y ownership.
## Tareas de desarrollo
- [x] **T-01 — Exponer consulta propia filtrable.** Dificultad: Bajo.
- [x] **T-02 — Crear calendario cliente y pruebas de ownership.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Calendario propio
**Dado** PROFESSIONAL autenticado **cuando** consulta una fecha/sede **entonces** visualiza únicamente sus bloques publicados.
### CA-02 — Sin exposición ajena
**Dado** un profesional **cuando** intenta consultar bloques de otro **entonces** se deniega.
## Definition of Done
- [x] CA-01 y CA-02 validados con evidencia.
- [x] Contrato, UI y prueba de ownership verificados; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-014/pasos.md) · 01 | Solo bloques propios, filtrables por fecha y sede |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-014/pasos.md) · API | Bloques ajenos invisibles; operar sobre ellos → 404 |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-014/pasos.md)); DoD completo.

## Notas y decisiones
- Ninguna.
