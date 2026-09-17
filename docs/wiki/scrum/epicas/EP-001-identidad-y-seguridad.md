---
id: EP-001
tipo: epica
titulo: "Identidad y seguridad de acceso"
estado: Pendiente de aprobación
historias: ["[[HU-001-cargar-catalogos-fijos]]", "[[HU-002-registrar-usuario]]", "[[HU-003-iniciar-sesion]]", "[[HU-004-renovar-y-cerrar-sesion]]", "[[HU-005-recuperar-contrasena]]"]
dependencias: []
---

# EP-001 — Identidad y seguridad de acceso

## Objetivo

Permitir que personas ficticias creen y administren acceso autenticado y autorizado al sistema.

## Valor esperado

Base segura para todas las capacidades privadas de USER, PROFESSIONAL y ADMIN.

## Actores

- USER, PROFESSIONAL, ADMIN y visitante.

## Alcance

- Roles y estados fijos, registro, JWT access/refresh, logout y recuperación de contraseña.

## Fuera de alcance

- Proveedores externos de identidad y envío SMTP obligatorio.

## Reglas de negocio

- Email y documento son únicos; contraseñas no se almacenan en texto plano; roles pertenecen al contexto de autorización.

## Dependencias

- Ninguna; habilita [[EP-002-perfil-y-catalogos]] y las épicas posteriores.

## Historias de usuario

- [[HU-001-cargar-catalogos-fijos]]
- [[HU-002-registrar-usuario]]
- [[HU-003-iniciar-sesion]]
- [[HU-004-renovar-y-cerrar-sesion]]
- [[HU-005-recuperar-contrasena]]

## Criterio de completitud de la épica

- [ ] Todas sus HU están `Completada` con evidencia.
- [ ] No existe ruta privada sin autenticación, rol y ownership aplicables.

## Riesgos e incógnitas

- Definir la forma segura de exponer el token de recuperación en desarrollo.
