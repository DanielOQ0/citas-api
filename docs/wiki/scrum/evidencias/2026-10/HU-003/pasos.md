# HU-003 — Iniciar sesión · Re-verificación E2E (cambio de sesión y vistas por rol)

Fecha: 2026-10-04 · Corrida `042347`.

| CA | Paso | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|
| CA-01 | Login por API de `admin@demo.invalid`; decodificación de claims | Access y refresh distintos, con contexto de rol | access `type=access`, `roles=[ADMIN]`, vida 900 s; refresh `type=refresh`, 7 días; tokens distintos | — |
| CA-01 | Login en la UI de ADMIN, PROFESSIONAL y USER | Cada uno en su inicio, con su rol | `/administrador/solicitudes`, `/profesional/mi-agenda`, `/paciente/inicio` | [roles/pasos.md](../roles/pasos.md) |
| CA-02 | UI: `admin@demo.invalid` con clave errónea | Rechazo genérico sin tokens | "Credenciales inválidas."; sigue en `/login`; sin tokens en `localStorage` | [01-credenciales-invalidas.png](01-credenciales-invalidas.png) |
| CA-02 | API: correo inexistente vs. clave errónea | Misma respuesta (no revela cuál falló) | Ambos `401 {"message":"Credenciales inválidas"}` | — |
| CA-03 | Capacidades privadas por rol y ownership | Solo si rol/ownership lo permiten | 403 de API para roles ajenos; rutas ajenas redirigen; ownership verificado en HU-006, HU-014, HU-019 y HU-023 | [roles/pasos.md](../roles/pasos.md) |

Pruebas automatizadas: `AuthIntegrationTest`, `RoleAccessIntegrationTest`, `citas-web` `auth.guard.spec.ts`, `session.service.spec.ts`.
