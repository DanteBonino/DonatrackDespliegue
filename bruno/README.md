# Colección de APIs - DonaTrack (DDSI-2026-Grupo_1)

Esta colección utiliza el formato **OpenCollection YAML** de Bruno.

## Estructura
- `donaciones-service/`: Endpoints del microservicio de Donaciones (CRUD Donantes, etc.).
- `logistica-service/`: Endpoints del microservicio de Logística (Camiones, Rutas, Callbacks).
- `environments/`: Variables de entorno para `Local` y `Staging`.

## Cómo levantar los tests
1. Abrir Bruno y seleccionar `Open Collection`.
2. Apuntar a la carpeta `bruno/`.
3. Seleccionar el entorno `Local` en la esquina superior derecha.
4. Para correr tests automatizados, ejecutar la colección o hacer clic en "Tests" dentro de cada request.

## Lista completa de endpoints
### Logística (Puerto 8080)
- `GET /camiones/{patente}`
- `POST /camiones/{patente}/posicion`
- `POST /camiones/{patente}/iniciar-ruta`
- `POST /externo/callback-planificacion`
- `GET /rutas/activas`

### Donaciones (Puerto 7070)
- `POST /donantes`
- `PATCH /donantes/{id}`
