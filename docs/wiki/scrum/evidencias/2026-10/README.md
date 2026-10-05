# Evidencias E2E — Cierre de HU (2026-10)

Evidencia exploratoria (D-03 y D-04 del [plan de cierre](../../plan-cierre-2026-10.md)) tomada con `playwright-cli` contra el stack Docker de desarrollo (MySQL con `db.sql` + Flyway). Cada carpeta contiene `pasos.md` (paso, esperado, obtenido) y capturas PNG de 1280 px de ancho; cada HU enlaza su carpeta desde la tabla "Evidencia de validación".

## Cómo repetirla

1. `docker compose up -d` en la raíz del workspace (MySQL con `database/reference/db.sql`, API y web).
2. `npm install -g @playwright/cli@latest`.
3. En Git Bash, desde la raíz: `source citas-api/docs/wiki/scrum/evidencias/2026-10/e2e-helpers.sh`.
4. Seguir los pasos de cada `pasos.md` con `start`, `login`, `go`, `text`, `shot`, `token` y `apicall`.

Los helpers leen la clave de laboratorio del seed y la enmascaran; los tokens nunca se imprimen. La corrida documentada es `042347`: sus datos sintéticos usan correos `*.e2e.042347@demo.invalid`, la EPS `E2E042347`, la especialidad "Especialidad E2E 042347" y el profesional `E2E-042347`, con citas el 2026-10-11 (D+7). La HU-024 usa un fixture SQL documentado en su carpeta.

## Índice

| Carpeta | Alcance |
|---|---|
| [roles/](roles/pasos.md) | Vistas atadas al rol: sin selector, rutas por rol, F5, logout, 403 de API (D-23) |
| [HU-001](HU-001/pasos.md) | Catálogos fijos de solo lectura, referencias por FK, reconstrucción del entorno |
| [HU-002](HU-002/pasos.md) | Registro con validación por campo, unicidad y hash |
| [HU-003](HU-003/pasos.md) | Login, rechazo genérico, claims de access/refresh |
| [HU-004](HU-004/pasos.md) | Refresh transparente, logout que revoca, refresh inválido → login |
| [HU-005](HU-005/pasos.md) | Recuperación con token temporal de un solo uso |
| [HU-006](HU-006/pasos.md) | Perfil propio, solo teléfono editable |
| [HU-007](HU-007/pasos.md) | Afiliación EPS → plan → régimen validada |
| [HU-008](HU-008/pasos.md) | CRUD de EPS y planes con protección de referencias |
| [HU-009](HU-009/pasos.md) | CRUD de especialidades, en uso → desactivar, inactiva no elegible |
| [HU-010](HU-010/pasos.md) | Alta de profesional con código y matrícula únicos |
| [HU-011](HU-011/pasos.md) | Asignaciones con una primaria, sedes, habilitación |
| [HU-012](HU-012/pasos.md) | Duración 30/60 gobernada por la especialidad |
| [HU-013](HU-013/pasos.md) | Bloques futuros, alineados, sin solape, protegidos si comprometidos |
| [HU-014](HU-014/pasos.md) | Calendario propio filtrable y aislado |
| [HU-015](HU-015/pasos.md) | Búsqueda con 5 filtros y 60 min consecutivos |
| [HU-016](HU-016/pasos.md) | Cita general auto-aprobada y conflicto de franja |
| [HU-017](HU-017/pasos.md) | Solicitud especializada que retiene sus slots |
| [HU-018](HU-018/pasos.md) | Bandeja ADMIN filtrable, aprobar, rechazar con motivo |
| [HU-019](HU-019/pasos.md) | Mis citas: siete campos, filtros, privacidad |
| [HU-020](HU-020/pasos.md) | Cancelación con liberación y sin reactivación |
| [HU-021](HU-021/pasos.md) | Solicitud de reprogramación con la original conservada |
| [HU-022](HU-022/pasos.md) | Decisión de reprogramación y respuesta del paciente |
| [HU-023](HU-023/pasos.md) | Agenda propia por día/semana/sede |
| [HU-024](HU-024/pasos.md) | Cierre COMPLETED/NO_SHOW con fixture SQL |
| [HU-025](HU-025/pasos.md) | Historial con tres lectores e inmutabilidad |
| [HU-026](HU-026/pasos.md) | Contrato REST contrastado contra backend y cliente |
