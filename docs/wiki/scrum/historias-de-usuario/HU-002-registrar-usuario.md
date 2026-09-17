---
id: HU-002
tipo: historia-de-usuario
titulo: "Registrar usuario"
estado: Pendiente de aprobación
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
- [ ] **T-01 — Definir datos/validaciones de registro.** Dificultad: Medio. Incluye restricciones únicas.
- [ ] **T-02 — Implementar caso de uso y frontera REST.** Dificultad: Alto. Autoriza solo alta USER.
- [ ] **T-03 — Crear formulario cliente y manejo de errores.** Dificultad: Medio. Sin exponer contraseña.
- [ ] **T-04 — Añadir pruebas de éxito, duplicados y validación.** Dificultad: Medio.
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
- [ ] CA-01 a CA-04 validados con evidencia.
- [ ] Persistencia 3FN y migración Flyway aplicable verificadas.
- [ ] Pruebas backend y cliente pertinentes disponibles; contrato REST documentado.
- [ ] No se exponen passwords ni secretos; trazabilidad actualizada.
## Evidencia de validación
| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| CA-04 / DoD | Pendiente | — | — |
## Historial de validación
- 2026-09-17 — Creada en `Pendiente de aprobación`.
## Notas y decisiones
- Confirmar validaciones de formato exactas durante contrato, sin persistir datos reales.
