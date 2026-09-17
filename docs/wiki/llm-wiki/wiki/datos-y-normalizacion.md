# Datos y normalización

El diseño debe alcanzar 3FN y soportar usuarios/roles, profesionales, especialidades y sedes N:M, afiliaciones, bloques, citas, estados, auditoría, reprogramaciones y tokens compatibles con el PRD.

No se deben repetir nombres de EPS, plan, régimen o especialidad en entidades que ya los refieran por catálogo. Las listas en columnas no son válidas y las relaciones N:M requieren tablas puente.

Las claves, cardinalidades, estrategia de reserva concurrente, representación de citas de 60 minutos, snapshots, índices y preservación de la cita original durante reprogramación requieren justificación documentada.

Fuente: [requisitos de normalización](../raw/requisitos-normalizacion-3fn.md).

El contenido de `database/reference/` está deliberadamente excluido hasta la comparación autorizada por el trainer.
