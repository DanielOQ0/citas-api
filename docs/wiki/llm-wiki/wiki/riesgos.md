# Riesgos

| Riesgo | Impacto | Tratamiento requerido |
|---|---|---|
| No existe aún contrato REST. | Divergencia entre frontend y backend. | Definirlo por HU/CA aprobadas y verificarlo en ambos repositorios. |
| El framework frontend aún no está elegido. | No se puede inferir estructura ni pruebas del cliente. | Esperar handoff aprobado de Stitch/AI Studio. |
| La reserva y reprogramación requieren concurrencia. | Posible doble reserva o liberación incorrecta de slots. | Decisión de datos/transacción y pruebas de integración antes de implementar. |
| El modelo de referencia está visible. | Puede invalidar la actividad de normalización. | Mantenerlo fuera de lectura e ingestión hasta autorización. |
| Recuperación de contraseña en desarrollo. | Riesgo de exposición de tokens. | Acordar mecanismo controlado que no registre ni versione tokens. |
