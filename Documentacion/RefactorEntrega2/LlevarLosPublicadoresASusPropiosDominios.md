# Decisión de diseño

## Contexto
- Los cambios de estado de una donación (asignación, inicio de ruta, entrega con éxito, entrega fallida) disparan notificaciones
vía un patrón Observer. Hasta ahora, los Controllers orquestaban la publicación, cambiaban los estados del dominio y después llamaban 
a los publicadores. Esto convertía a los controllers en services. Queríamos que la donación notifique sus propios cambios de estado.

## Decisión
- Llevar la publicación al dominio: cada método de transición de DonacionIndependiente (asignarA, iniciarTraslado, marcarComoEntregada, marcarComoFallida)
publica su propio evento. Para eso la donación recibe por constructor un único publicador-fachada, PublicadorDeCambiosEstados, que compone los 4 publicadores
existentes y expone métodos como susbibrirA / publicar que delegan. Es un instancia compartida por todas las donaciones.


## Consecuencias
- El controller deja de tener tantas responsabilidad, ahora recibe los contextos y delega al dominio.




