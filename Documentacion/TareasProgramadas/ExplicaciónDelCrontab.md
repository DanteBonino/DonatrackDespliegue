## Explicación del significado de cada posición en el crontab

El crontab define cuándo se ejecuta una tarea programada usando **5 campos**, separados por espacios, antes del comando a ejecutar.

```
* * * * * comando
│ │ │ │ │
│ │ │ │ └── día de la semana (0-7)
│ │ │ └──── mes (1-12)
│ │ └────── día del mes (1-31)
│ └──────── hora (0-23)
└────────── minuto (0-59)
```

## Detalle de cada posición

| Posición | Campo | Rango de valores | Notas |
|---|---|---|---|
| 1 | Minuto | `0-59` | `0` = en punto |
| 2 | Hora | `0-23` | Formato 24hs. `8` = 8 AM, `20` = 8 PM |
| 3 | Día del mes | `1-31` | |
| 4 | Mes | `1-12` | También acepta nombres abreviados en inglés (`jan`, `feb`, etc.), según la implementación |
| 5 | Día de la semana | `0-7` | `0` y `7` son ambos domingo. `1` = lunes, ..., `6` = sábado |

## Símbolos especiales

| Símbolo | Significado | Ejemplo |
|---|---|---|
| `*` | Cualquier valor (sin restricción) | `* * * * *` → cada minuto |
| `*/N` | Cada N unidades | `*/6` en el campo hora → cada 6 horas |
| `N-M` | Rango de valores | `1-5` en día de la semana → de lunes a viernes |
| `N,M` | Lista de valores específicos | `0,30` en minuto → al minuto 0 y al 30 |

## Ejemplos

| Crontab | Significado |
|---|---|
| `0 8 * * *` | Todos los días a las 8:00 AM |
| `0 */6 * * *` | Cada 6 horas (00:00, 06:00, 12:00, 18:00) |
| `0 * * * *` | Cada hora, en punto |
| `*/15 * * * *` | Cada 15 minutos |
| `0 8 * * 1-5` | A las 8:00 AM, solo de lunes a viernes |
| `0 0 1 * *` | El día 1 de cada mes, a la medianoche |

## Consideraciones al desplegar

- **Zona horaria del servidor**: el cron usa la hora del sistema donde corre, no la hora local de quien lo configuró. Verificar con `timedatectl` en el servidor antes de asumir el horario.
- **Rutas absolutas**: el cron no usa el `$PATH` ni el directorio de trabajo de una shell interactiva, así que tanto el ejecutable (`java`) como el `.jar` deben especificarse con ruta absoluta.
- **Redirección de logs**: agregar `>> /ruta/al/log.log 2>&1` al final del comando para poder revisar después si la tarea corrió bien y qué output generó.
