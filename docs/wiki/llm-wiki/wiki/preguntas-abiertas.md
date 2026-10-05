# Preguntas abiertas

Todas las preguntas registradas se resolvieron el 2026-10-04 con decisión explícita del usuario ([plan de cierre](../../scrum/plan-cierre-2026-10.md)).

| # | Pregunta | Resolución |
|---|---|---|
| 1 | ¿Cuáles estados de cita y reprogramación son terminales, y cuáles son todas las transiciones válidas? | Cita: `REQUESTED → APPROVED/REJECTED/CANCELLED`, `APPROVED → CANCELLED/COMPLETED/NO_SHOW`; terminales `REJECTED, CANCELLED, COMPLETED, NO_SHOW`. Reprogramación: solo `PENDING` transiciona a `APPROVED/REJECTED/CANCELLED`. Codificado en `AppointmentStatus`/`RescheduleStatus`. |
| 2 | ¿Una solicitud `REQUESTED` o una reprogramación `PENDING` impide editar/eliminar su bloque? | Sí (D-13): cualquier slot reservado, retenido o con reprogramación PENDING protege el bloque (409). |
| 3 | ¿Qué política de expiración aplica a slots retenidos? | Ninguna automática (D-14); ADMIN no aprueba lo vencido pero puede rechazarlo. |
| 4 | ¿Cómo se identifica y precarga Medicina General? | `specialties.is_general = TRUE`, código `MEDICINA_GENERAL`, seed de `db.sql`; el tipo general/especializada usa `requiresAdminApproval` (D-06). |
| 5 | ¿Mecanismo seguro para completar la recuperación en desarrollo? | Token en la respuesta solo con `EXPOSE_RECOVERY_TOKEN=true` (por defecto `false`), nunca en logs; misma respuesta para cuentas inexistentes (HU-005). |
| 6 | ¿Qué framework frontend resultó del handoff? | Angular 21 (SPA, sin SSR desde D-05). |

No hay preguntas abiertas vigentes. Las nuevas se registran aquí hasta recibir una decisión explícita.
