# Evidencias E2E — Cierre de HU (2026-10)

Evidencia exploratoria (D-03, D-04 del [plan de cierre](../../plan-cierre-2026-10.md)) tomada con `playwright-cli` contra el stack Docker de desarrollo. Cada carpeta contiene `pasos.md` (pasos, esperado, obtenido) y capturas PNG a 1280×800; las HU enlazan aquí desde su tabla "Evidencia de validación".

## Cómo repetirla

1. `docker compose up -d` en la raíz del workspace (MySQL con `database/reference/db.sql`, API y web).
2. `npm install -g @playwright/cli@latest`.
3. En Git Bash, desde la raíz: `source citas-api/docs/wiki/scrum/evidencias/2026-10/e2e-helpers.sh`.
4. Seguir los pasos de cada `pasos.md` con `start`, `login`, `go`, `text`, `shot`, `token` y `apicall`.

Los helpers leen la clave de laboratorio del seed y la enmascaran; los tokens nunca se imprimen.
Los datos creados por una corrida usan correos `*.e2e.<run>@demo.invalid` y fechas a +7..+10 días.

## Índice

| Carpeta | Alcance |
|---|---|
| [roles/](roles/pasos.md) | Vistas atadas al rol: sin selector, rutas por rol, F5, logout, 403 de API (Fase 1, D-23) |
