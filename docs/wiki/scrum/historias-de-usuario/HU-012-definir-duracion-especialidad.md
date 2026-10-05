---
id: HU-012
tipo: historia-de-usuario
titulo: "Definir duración de especialidad"
estado: Lista
epica: "[[EP-004-disponibilidad-profesional]]"
esfuerzo: Medio
sprint_sugerido: S3
dependencias: ["[[HU-009-gestionar-especialidades]]"]
relacionadas: ["[[HU-015-consultar-disponibilidad]]"]
---
# HU-012 — Definir duración de especialidad
## Historia de usuario
**COMO** ADMIN **QUIERO** definir duración de 30 o 60 minutos por especialidad **PARA** calcular la capacidad de reserva de modo uniforme.
## Alcance
- Configuración por especialidad y aplicación como uno/dos slots consecutivos.
## Fuera de alcance
- Sobrescritura por profesional.
## Reglas de negocio
- 30 min equivale a un slot; 60 min, dos consecutivos; la especialidad gobierna la duración.
## Dependencias y relaciones
- Épica: [[EP-004-disponibilidad-profesional]]. Depende de [[HU-009-gestionar-especialidades]].
## Esfuerzo
**Nivel:** Medio. **Justificación:** regla de configuración con efecto transversal.
## Tareas de desarrollo
- [x] **T-01 — Restringir duración a valores permitidos y migrar datos.** Dificultad: Medio.
- [x] **T-02 — Exponer configuración ADMIN y consumo de disponibilidad.** Dificultad: Medio.
- [x] **T-03 — Probar equivalencia slot y consecutividad.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Valores válidos
**Dado** ADMIN **cuando** configura una especialidad **entonces** solo puede elegir 30 o 60 minutos.
### CA-02 — Cálculo uniforme
**Dado** una especialidad configurada **cuando** se consulta/reserva **entonces** requiere respectivamente uno o dos slots consecutivos.
### CA-03 — Sin sobrescritura
**Dado** PROFESSIONAL **cuando** gestiona agenda **entonces** no puede cambiar esa duración.
## Definition of Done
- [x] CA-01 a CA-03 validados con evidencia.
- [x] Migración, contrato y pruebas de ambas duraciones verificados; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-012/pasos.md) · E2E | Solo 30 o 60; 45 → 400 |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-012/pasos.md) · + HU-015/HU-017 | 30 min = 1 slot; 60 min = 2 consecutivos |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-012/pasos.md) · API | PROFESSIONAL no cambia la duración (403) |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-012/pasos.md)); DoD completo.

## Notas y decisiones
- No se deduce una duración distinta de las fuentes autorizadas.
- Decisión aprobada 2026-10-04 · La duración mostrada de cada cita es la real (`scheduled_end_at - scheduled_start_at`).
