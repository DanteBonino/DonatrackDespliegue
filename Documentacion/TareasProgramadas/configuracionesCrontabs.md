# Configuración de los crontabs de las tareas programadas

## donaciones-service
| Tarea Programada                       | Configuración | Descripción                          |
|----------------------------------------|---------------|--------------------------------------|
| `Reintento de envío de notificaciones` | `0 * * * *`   | Cada hora, en punto                  |
| `Publicar Inactividad`                 | `0 8 * * 1-6` | 8:00 AM, de lunes a sábado           |
| `Crear solicitudes de asignación`      | `0 23 * * *`  | Todos los días a las 23:00           |

## logistica-service
| Tarea Programada                  | Configuración | Descripción                                        |
|-----------------------------------|---------------|----------------------------------------------------|
| `Planificación nocturna de rutas` | `0 2 * * *`   | Todos los días a las 02:00 (fuera de horario pico) |