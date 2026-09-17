# Convenciones de la LLM Wiki

## Autoridad y precedencia

1. PRD aprobado.
2. Restricciones técnicas y requisitos de normalización aprobados.
3. HU, criterios de aceptación y DoD aprobados.
4. Decisiones registradas con evidencia.
5. Código, pruebas, migraciones y contratos implementados.

Una fuente de menor precedencia no puede modificar ni reinterpretar una de mayor precedencia. Las contradicciones se registran en `../wiki/riesgos.md` o `../wiki/preguntas-abiertas.md` hasta recibir decisión explícita.

## Tipos de conocimiento

- **HECHO:** afirmación verificable con fuente o evidencia enlazada.
- **DECISIÓN:** elección aprobada; debe incluir estado, fecha, motivación y fuente.
- **PREFERENCIA:** pauta reversible de trabajo o presentación.
- **PREGUNTA ABIERTA:** información necesaria que aún no tiene respuesta aprobada.

## Escritura

- RAW contiene copias curadas e inmutables de fuentes aprobadas.
- WIKI contiene síntesis, nunca conversaciones completas.
- Cada página WIKI enlaza su fuente RAW o evidencia vigente.
- `wiki/log.md` es append-only.
- No registrar secretos, tokens, contraseñas, PII ni datos privados reales de FCV.
