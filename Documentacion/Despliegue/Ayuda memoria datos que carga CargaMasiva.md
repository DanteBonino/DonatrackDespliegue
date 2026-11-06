# Ayuda memoria: datos que carga `CargaMasiva`

*DonaTrack · Grupo 1 · Basado en el script `datosBaseDeDatos.CargaDeDatos.CargaMasiva`*

## 1. Cómo se usa

- **Local (IntelliJ):** correr el `main` de `CargaMasiva`, con las variables de entorno de la base cargadas.
- **Con Docker o en la VM:**

```powershell
docker compose run --rm donaciones java -cp servidor-jar-with-dependencies.jar datosBaseDeDatos.CargaDeDatos.CargaMasiva
```

- **Si ya hay entidades beneficiarias, no hace nada:** imprime *Ya existen entidades beneficiarias en la base. Abortando carga.* Para recargar, correr antes `EliminarDatos`.
- Primero carga las categorías y subcategorías que falten (`CargarCategoriasYSubCategorias`) y se asegura de que existan `tomate` (categoría ALIMENTO, perecedera) y `silla` (categoría MOBILIARIO, no perecedera).
- Las donaciones se crean **sin observers**, así que la carga no dispara notificaciones ni mails.
- Crea su propio administrador (`testAdmin`): no hace falta correr `CargarAdmin` antes.

## 2. Resumen en números

| Qué | Cantidad |
| --- | --- |
| Entidades beneficiarias | 4 |
| Donantes | 10 (7 humanos y 3 jurídicos) |
| Usuarios para iniciar sesión | 15 (10 donantes, 4 entidades y 1 administrador) |
| Donaciones | 100 |
| Notificaciones | 10 |

## 3. Usuarios para iniciar sesión

**Contraseña de todos: `test1234`.** Escribí los usuarios tal cual figuran (por ejemplo `elSol`).

| Usuario | Rol | Corresponde a |
| --- | --- | --- |
| `ana` | Donante | D01 Ana Gómez |
| `bruno` | Donante | D02 Bruno Díaz |
| `carla` | Donante | D03 Carla Fernández |
| `diego` | Donante | D04 Diego Sosa |
| `elena` | Donante | D05 Elena Ruiz |
| `facundo` | Donante | D06 Facundo López |
| `gabriela` | Donante | D07 Gabriela Torres |
| `elSol` | Donante | D08 Supermercado El Sol S.A. |
| `manosUnidas` | Donante | D09 Fundación Manos Unidas |
| `textilDelSur` | Donante | D10 Textil del Sur S.R.L. |
| `comedorPorUnFuturoMasUnido` | Entidad | E01 Comedor por un futuro más unido |
| `hogarSanJose` | Entidad | E02 Hogar San José |
| `escuelaRural` | Entidad | E03 Escuela Rural N° 12 |
| `centroComunitarioEsperanza` | Entidad | E04 Centro Comunitario Esperanza |
| `testAdmin` | Administrador | El administrador, con el contacto definido en el script |

## 4. Entidades beneficiarias

| # | Entidad | Dirección | Contacto | Necesidades |
| --- | --- | --- | --- | --- |
| E01 | Comedor por un futuro más unido | Av. Rivadavia 4500 (-34.6085, -58.3838) | testentidadbeneficiaria@donatrack.com · 1150000001 · MAIL | 8, todas vigentes y con condición Básica (acepta cualquier donación vigente de esa subcategoría): remera 1000, pantalón 800, arroz 1000, campera 600, fideos secos 900, tomate 400, mesa 150, silla 400 |
| E02 | Hogar San José | Mitre 1234 (-34.6150, -58.3750) | hogarsanjose@test.org · 1150000002 · WPP | 2: campera USADO 500 y mesa NUEVO 200 |
| E03 | Escuela Rural N° 12 | Ruta 8 120 (-34.5000, -58.7000) | escuelarural12@test.org · 1150000003 · SMS | 1: remera 600 |
| E04 | Centro Comunitario Esperanza | Belgrano 2200 (-34.6200, -58.4000) | esperanza@test.org · 1150000004 · MAIL | 2: arroz 700 y tomate 300 |

## 5. Donantes

**Humanos**

| # | Nombre | Documento | Nacimiento | Género | Dirección | Mail | Teléfono | Medio |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| D01 | Ana Gómez | 30111222 | 12/03/1985 | Femenino | Av. Rivadavia 1001 | testdonante@donatrack.com | 1100000001 | MAIL |
| D02 | Bruno Díaz | 32444555 | 25/07/1990 | Masculino | Av. Corrientes 2450 | bruno.diaz@test.org | 1100000002 | WPP |
| D03 | Carla Fernández | 33888999 | 03/11/1992 | Femenino | Mitre 780 | carla.fernandez@test.org | 1100000003 | SMS |
| D04 | Diego Sosa | 28999000 | 19/01/1978 | Masculino | Belgrano 1450 | diego.sosa@test.org | 1100000004 | MAIL |
| D05 | Elena Ruiz | 35666777 | 30/05/1995 | Femenino | San Martín 620 | elena.ruiz@test.org | 1100000005 | WPP |
| D06 | Facundo López | 31222333 | 14/09/1988 | Masculino | Sarmiento 990 | facundo.lopez@test.org | 1100000006 | MAIL |
| D07 | Gabriela Torres | 27000111 | 08/12/1975 | Femenino | Alsina 330 | gabriela.torres@test.org | 1100000007 | SMS |

**Jurídicos**

| # | Razón social | CUIT | Tipo | Rubro | Mail | Teléfono | Medio |
| --- | --- | --- | --- | --- | --- | --- | --- |
| D08 | Supermercado El Sol S.A. | 30-71234567-8 | EMPRESA | ALIMENTOS | donaciones@elsol.test | 1140000008 | MAIL |
| D09 | Fundación Manos Unidas | 30-70987654-3 | ONG | ASISTENCIA SOCIAL | contacto@manosunidas.test | 1140000009 | MAIL |
| D10 | Textil del Sur S.R.L. | 30-70111222-4 | EMPRESA | INDUMENTARIA | donaciones@textildelsur.test | 1140000010 | WPP |

## 6. Donaciones (100)

**Por estado**

| Estado | Cantidad | Entidad asignada |
| --- | --- | --- |
| EN\_DEPOSITO | 14 | No |
| PENDIENTE\_DE\_CONFIRMACION\_DE\_ASIGNACION | 11 | No |
| ASIGNACION\_REALIZADA | 13 | Sí |
| LISTA\_PARA\_ENTREGAR | 13 | Sí |
| EN\_TRASLADO | 13 | Sí, con id de entrega |
| ENTREGADA | 24 | Sí, con comprobante |
| ENTREGA\_FALLIDA | 10 | Sí, con justificación |
| VENCIDA | 2 | No |

**Por donante**

| Donante | Donaciones |
| --- | --- |
| D01 Ana Gómez | 30 |
| D02 a D07 (Bruno, Carla, Diego, Elena, Facundo, Gabriela) | 8 cada uno |
| D08 Supermercado El Sol | 7 |
| D09 Fundación Manos Unidas | 8 |
| D10 Textil del Sur | 7 |

**Por entidad** (73 donaciones tienen entidad asignada)

| Entidad | Con asignación | De las cuales, entregadas |
| --- | --- | --- |
| E01 Comedor | 43 | 20 |
| E02 Hogar San José | 14 | 1 |
| E03 Escuela Rural | 10 | 1 |
| E04 Centro Esperanza | 6 | 2 |

El mapa de `/donaciones/entregadas` parte de las 24 entregadas: **24 marcadores** en **4 ubicaciones**, y 20 de ellos se superponen en el Comedor.

**Bienes**

- **No perecederos:** remera, pantalón, campera, silla (nuevos o usados) y mesa (solo nueva).
- **Perecederos:** arroz (0,5), fideos secos (1) y tomate (1).
- **Vencimientos:** arroz y fideos vigentes, 31/01/2027; tomate vigente, 20/11/2026; los vencidos, 15/10/2026. Las 2 donaciones VENCIDA son de Ana: un tomate y unos fideos vencidos.

**Entregas y fallos**

- Las entregadas llevan un código como `AAA-111`; las que están en traslado o fallidas, uno como `ENT-001`.
- Justificaciones de las 10 fallidas: Dirección incorrecta; Sin transporte disponible (2); Entidad cerrada; No había responsable; Donación incompleta; Rechazada por la entidad (2); Camión averiado; Faltante de documentación.

## 7. Notificaciones (10)

- **Ana (D01):** 5 por mail, 3 entregadas y 2 pendientes.
- **E01 Comedor:** 5 por mail, 3 entregadas y 2 pendientes.
- Las 4 pendientes las toma la tarea de reintento de envío.

## 8. Cosas para tener en cuenta

1. Los mensajes finales del script (`println`) están desactualizados: dicen que son 3 usuarios con contraseña `test`; lo real son 15 usuarios con `test1234`.
2. Las notificaciones hablan de **Comedor Los Pibes**, pero la entidad se llama *Comedor por un futuro más unido*.
3. Los tomates vigentes vencen el **20/11/2026**: después de esa fecha dejan de ser vigentes y los datos cargados quedan desactualizados.
4. Los ids de entrega `ENT-003` a `ENT-009` aparecen dos veces (una en una donación en traslado y otra en una fallida). Además, el script solo carga en donaciones: no crea las entregas correspondientes en logística.
5. La tarea de reintento (cada hora, por SendGrid) intentará enviar las 4 notificaciones pendientes a `testdonante@donatrack.com` y `testentidadbeneficiaria@donatrack.com`; si esos buzones no existen, rebotan.
6. El administrador se crea con un mail y un teléfono personales escritos en el script: va a recibir notificaciones y, si el repo se publica, quedan expuestos.
