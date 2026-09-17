---
id: HU-012
tipo: historia-de-usuario
titulo: "Definir duración de especialidad"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Restringir duración a valores permitidos y migrar datos.** Dificultad: Medio.
- [ ] **T-02 — Exponer configuración ADMIN y consumo de disponibilidad.** Dificultad: Medio.
- [ ] **T-03 — Probar equivalencia slot y consecutividad.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Valores válidos
**Dado** ADMIN **cuando** configura una especialidad **entonces** solo puede elegir 30 o 60 minutos.
### CA-02 — Cálculo uniforme
**Dado** una especialidad configurada **cuando** se consulta/reserva **entonces** requiere respectivamente uno o dos slots consecutivos.
### CA-03 — Sin sobrescritura
**Dado** PROFESSIONAL **cuando** gestiona agenda **entonces** no puede cambiar esa duración.
## Definition of Done
- [ ] CA-01 a CA-03 validados con evidencia.
- [ ] Migración, contrato y pruebas de ambas duraciones verificados; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- No se deduce una duración distinta de las fuentes autorizadas.
