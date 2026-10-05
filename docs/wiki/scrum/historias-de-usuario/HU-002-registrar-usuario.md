---
id: HU-002
tipo: historia-de-usuario
titulo: "Registrar usuario"
estado: Lista
epica: "[[EP-001-identidad-y-seguridad]]"
esfuerzo: Alto
sprint_sugerido: S2
dependencias: ["[[HU-001-cargar-catalogos-fijos]]"]
relacionadas: ["[[HU-003-iniciar-sesion]]"]
---
# HU-002 — Registrar usuario
## Historia de usuario
**COMO** visitante **QUIERO** registrar una cuenta USER con mis datos mínimos **PARA** gestionar citas ficticias.
## Alcance
- Nombres, apellidos, tipo/número de documento, email, teléfono y contraseña; asignación USER.
## Fuera de alcance
- Registro autónomo de ADMIN o PROFESSIONAL.
## Reglas de negocio
- Email y documento únicos; contraseña con hash adaptativo; validación server-side; no registrar password en logs.
## Dependencias y relaciones
- Épica: [[EP-001-identidad-y-seguridad]]. Depende de [[HU-001-cargar-catalogos-fijos]].
## Esfuerzo
**Nivel:** Alto. **Justificación:** validaciones, seguridad, persistencia y cliente coordinados.
## Tareas de desarrollo
- [x] **T-01 — Definir datos/validaciones de registro.** Dificultad: Medio. Incluye restricciones únicas.
- [x] **T-02 — Implementar caso de uso y frontera REST.** Dificultad: Alto. Autoriza solo alta USER.
- [x] **T-03 — Crear formulario cliente y manejo de errores.** Dificultad: Medio. Sin exponer contraseña.
- [x] **T-04 — Añadir pruebas de éxito, duplicados y validación.** Dificultad: Medio.
## Criterios de aceptación
### CA-01 — Alta válida
**Dado** datos mínimos válidos y sintéticos **cuando** el visitante se registra **entonces** se crea una cuenta con rol USER.
### CA-02 — Unicidad
**Dado** email o documento ya registrado **cuando** se intenta reutilizar **entonces** se rechaza sin crear cuenta adicional.
### CA-03 — Protección de contraseña
**Dado** un registro exitoso **cuando** se inspecciona el almacenamiento y trazas pertinentes **entonces** la contraseña no aparece en texto plano.
### CA-04 — Errores utilizables
**Dado** un campo inválido **cuando** se envía el formulario **entonces** el cliente muestra el error y el servidor rechaza la solicitud.
## Definition of Done
- [x] CA-01 a CA-04 validados con evidencia.
- [x] Persistencia 3FN y migración Flyway aplicable verificadas.
- [x] Pruebas backend y cliente pertinentes disponibles; contrato REST documentado.
- [x] No se exponen passwords ni secretos; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Verificado | [pasos](../evidencias/2026-10/HU-002/pasos.md) · 02 | Cuenta USER creada y redirigida a su inicio |
| CA-02 | Verificado | [pasos](../evidencias/2026-10/HU-002/pasos.md) · 03 | Email o documento duplicado → 409 sin cuenta adicional |
| CA-03 | Verificado | [pasos](../evidencias/2026-10/HU-002/pasos.md) · BD | Hash BCrypt; sin contraseña en texto plano |
| CA-04 | Verificado | [pasos](../evidencias/2026-10/HU-002/pasos.md) · 01 + API | Errores por campo en el cliente y 400 con `fieldErrors` en el servidor |

## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
- 2026-10-04 — Aprobada por el usuario para cierre ([[plan-cierre-2026-10]]).
- 2026-10-05 — `Lista` (cierre 2026-10): CA-01, CA-02, CA-03, CA-04 verificados con pruebas de integración y E2E exploratorio ([pasos](../evidencias/2026-10/HU-002/pasos.md)); DoD completo.

## Notas y decisiones
- Confirmar validaciones de formato exactas durante contrato, sin persistir datos reales.
- Decisión aprobada 2026-10-04 · D-07: tipo CC/CE/TI/PA, documento 5-20 alfanumérico, teléfono 7-15 dígitos, clave ≥ 8.
