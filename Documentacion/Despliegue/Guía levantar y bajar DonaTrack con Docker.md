# Guía: levantar y bajar DonaTrack con Docker

*DonaTrack · Grupo 1 · Preparada el 2 de noviembre de 2026*

## 1. Antes de empezar

- **Docker Desktop abierto y con el motor corriendo.** Abajo a la izquierda tiene que decir *Engine running*. Se verifica con `docker info`, que debe mostrar una sección `Server:` sin errores.
- **Terminal parada en la raíz del repo**, donde están `docker-compose.yml` y `Dockerfile` (en IntelliJ, la pestaña Terminal).
- **Archivo `.env` presente** en esa misma carpeta (git lo ignora). Si falta: `Copy-Item .env.example .env` y completar los valores.
- **Servidores de IntelliJ cerrados.** Si ServidorLogistica corre desde IntelliJ ocupa el puerto 8080, que necesita el compose.

## 2. Levantar los procesos

**Primera vez, o después de cambiar código:**

```powershell
docker compose up --build
```

Construye las imágenes (compila el proyecto con Maven dentro de Docker) y levanta tres servicios: `db` (Postgres), `donaciones` y `logistica`. La primera vez tarda varios minutos. Esta forma queda en primer plano mostrando los logs y ocupa la terminal.

**Mejor, en segundo plano:**

```powershell
docker compose up -d --build
```

`-d` (detached) devuelve la terminal. Si ya lo levantaste en primer plano, la tecla `d` que muestra la terminal lo deja corriendo en segundo plano sin apagarlo.

**Sin cambios de código** (no hace falta reconstruir):

```powershell
docker compose up -d
```

Si cambiaste solo el `.env`, usá `docker compose up -d --force-recreate` para que los contenedores lean los valores nuevos.

**Cómo saber que anda:**

```powershell
docker compose ps
```

Tienen que aparecer los tres servicios en `running`, y `db` en `healthy`. Después probá en el navegador:

- `http://localhost` → página principal de DonaTrack (donaciones). El puerto 80 de tu máquina apunta al 7070 del contenedor.
- `http://localhost:8080` → logística. Un *Endpoint GET / not found* es normal: el servidor responde, pero no tiene una ruta en `/`.
- `http://localhost:7070` **no** funciona y es lo esperado: ese puerto existe solo dentro del contenedor.

La primera vez la base está vacía: cargá el administrador con la sección 5 para poder iniciar sesión.

## 3. Ver estado y logs

```powershell
docker compose ps                      # estado de los servicios de este proyecto
docker ps                              # todo lo que corre en Docker, de cualquier proyecto
docker ps -a                           # incluye los detenidos
docker compose logs -f donaciones      # sigue los logs en vivo; Ctrl+C sale sin apagar nada
docker compose logs logistica          # logs de logística
```

Para buscar errores reales:

```powershell
docker compose logs donaciones | Select-String -Pattern 'SEVERE|Caused by|FATAL|Exception in thread'
```

Buscar la palabra `Exception` a secas da falsos positivos: Hibernate imprime avisos informativos desde una clase llamada `SqlExceptionHelper`, y no son errores.

Para mirar las tablas de la base:

```powershell
docker compose exec db psql -U donatrack -d donatrack -P pager=off -c '\dt donaciones.*'
```

Con `-P pager=off` no queda trabado en la pantalla `(END)`; si igual aparece, se sale con la tecla `q`. Cambiando `donaciones` por `logistica` se ven las tablas de logística.

## 4. Bajar los procesos

| Comando | Qué pasa con los contenedores | Datos de la base |
| --- | --- | --- |
| Ctrl+C (con `up` en primer plano) o `docker compose stop` | Se detienen, pero siguen existiendo, parados | Se conservan |
| `docker compose down` | Se detienen y se eliminan (junto con la red) | Se conservan |
| `docker compose down -v` | Igual que `down`, y además se borra el volumen | Se borran |

Cuál usar:

- **Pausa corta:** Ctrl+C o `docker compose stop`.
- **Terminás por hoy:** `docker compose down`.
- **Querés empezar con la base vacía:** `docker compose down -v` y después `up`.
- **Si usaste `up -d`:** no hay Ctrl+C, usá `stop` o `down`.

Para comprobar que no quedó nada corriendo, `docker ps` tiene que mostrar solo los encabezados.

Tené en cuenta que los servicios tienen `restart: unless-stopped`: si cerrás la compu o Docker Desktop mientras corren, vuelven a arrancar solos al abrirlo. Apagalos con `stop` o `down` antes de irte. Además Docker Desktop retiene memoria aunque no haya contenedores: después del `down`, cerralo desde el ícono de la bandeja (*Quit Docker Desktop*).

## 5. Probar escenarios: mains que cargan o borran datos

Estos mains no corren dentro del servidor. Se ejecutan en un **contenedor temporal** que usa la misma imagen, la misma configuración y la misma base que el servidor. La base tiene que estar corriendo (`docker compose ps`).

Plantilla:

```powershell
docker compose run --rm <servicio> java -cp servidor-jar-with-dependencies.jar <paquete.Clase>
```

| Parte | Qué significa |
| --- | --- |
| `docker compose` | La herramienta que lee el `docker-compose.yml` de la carpeta actual |
| `run` | Crea un contenedor **nuevo y temporal** a partir de un servicio, ejecuta un solo comando y muestra su salida. No toca los contenedores que ya están corriendo, y no publica puertos, así que no choca con el servidor |
| `--rm` | Elimina el contenedor temporal al terminar. Sin esto quedarían contenedores parados acumulándose |
| `donaciones` o `logistica` | Servicio del compose a usar. De ahí salen la imagen, las variables de entorno (`.env` y `environment`), la red y el orden de arranque (espera a que `db` esté healthy) |
| `java` | Lanza la JVM dentro del contenedor |
| `-cp servidor-jar-with-dependencies.jar` | Classpath: dónde buscar las clases. Ese jar contiene todas las clases del módulo más sus dependencias, y está en `/app`, la carpeta de trabajo de la imagen |
| `datosBaseDeDatos.CargaDeDatos.CargarAdmin` | Nombre completo (paquete + clase) de la clase cuyo `main` se ejecuta |

**Por qué funciona:**

- Todo lo que va después del nombre del servicio **reemplaza el comando por defecto** de la imagen (que era `java -jar servidor-jar-with-dependencies.jar`, el servidor).
- Con `-cp` elegís cualquier clase con `main`. Con `-jar` Java solo ejecuta la clase que el pom declaró como principal de ese jar.
- La primera línea de cada main es `BaseDeDatos.configurarDesdeEntorno()`, que lee `DONATRACK_DB_URL`, `DONATRACK_DB_USER` y `DONATRACK_DB_PASSWORD`. Compose las inyecta, y la URL apunta a `db:5432`, el nombre del servicio Postgres en la red privada de Docker.
- El contenedor temporal y el servidor son procesos distintos pero hablan con **la misma base**, que vive en el volumen `dbdata`. Por eso, después de cargar el admin con un main, podés iniciar sesión en el servidor que ya estaba corriendo.

**Mains disponibles** (el paquete coincide con las carpetas del proyecto; se confirmó con `CargarAdmin`):

| Servicio | Clase | Qué hace |
| --- | --- | --- |
| `donaciones` | `datosBaseDeDatos.CargaDeDatos.CargarAdmin` | Crea el usuario administrador |
| `donaciones` | `datosBaseDeDatos.CargaDeDatos.CargarCategoriasYSubCategorias` | Carga categorías y subcategorías |
| `donaciones` | `datosBaseDeDatos.CargaDeDatos.CargarNecesidades` | Carga necesidades |
| `donaciones` | `datosBaseDeDatos.CargaDeDatos.CargarLoteConDonaciones` | Carga un lote con donaciones |
| `donaciones` | `datosBaseDeDatos.limpiarBase.EliminarDatos` | Borra todos los datos del esquema `donaciones` |
| `logistica` | `datosBaseDeDatos.CargarDatosDePrueba` | Carga datos de prueba de logística |
| `logistica` | `datosBaseDeDatos.EliminarDatos` | Borra todos los datos del esquema `logistica` |

Ejemplos:

```powershell
docker compose run --rm donaciones java -cp servidor-jar-with-dependencies.jar datosBaseDeDatos.CargaDeDatos.CargarAdmin
docker compose run --rm donaciones java -cp servidor-jar-with-dependencies.jar datosBaseDeDatos.limpiarBase.EliminarDatos
docker compose run --rm logistica java -cp servidor-jar-with-dependencies.jar datosBaseDeDatos.CargarDatosDePrueba
```

**Cuidados:**

- `EliminarDatos` hace TRUNCATE de todas las tablas del esquema, incluido el administrador. Después de borrar, volvé a ejecutar `CargarAdmin`.
- Donaciones y logística comparten la base, pero cada limpiador solo toca su propio esquema.
- En la salida buscá la línea `using driver ... at URL [jdbc:postgresql://db:5432/...]`. Tiene que decir `db:5432`. Si dijera `render.com`, ese main no tiene `BaseDeDatos.configurarDesdeEntorno()` como primera línea y está usando la base de Render.
- Los mains `Crud*` y `GetAllNotificaciones` se corren igual, cambiando solo la clase.
- Para empezar de cero del todo: `docker compose down -v` y luego `docker compose up -d --build`.

## 6. Tareas programadas

Cada tarea es un jar independiente. Se prueba a mano con:

```powershell
docker compose run --rm -T donaciones java -jar reintento-envio-notificaciones-jar-with-dependencies.jar
```

Diferencias con la sección 5:

- **`-jar` en lugar de `-cp`:** ejecuta la clase main que el pom declaró para ese jar, por eso no hay que indicarla.
- **`-T`:** desactiva la terminal interactiva que `run` abre por defecto. Cron no tiene terminal, así que el comando del crontab la necesita. A mano se podría omitir, pero conviene probar el comando exacto que usará cron.

| Tarea | Servicio | Jar | Cron | Cuándo |
| --- | --- | --- | --- | --- |
| Reintento de envío de notificaciones | `donaciones` | `reintento-envio-notificaciones-jar-with-dependencies.jar` | `0 * * * *` | Cada hora en punto |
| Publicar inactividad de donante | `donaciones` | `publicar-inactividad-de-donante-jar-with-dependencies.jar` | `0 8 * * 1-6` | 8:00, de lunes a sábado |
| Crear solicitudes de asignación | `donaciones` | `crear-solicitudes-de-asignacion-jar-with-dependencies.jar` | `0 23 * * *` | Todos los días a las 23:00 |
| Planificación nocturna de rutas | `logistica` | `planificacion-nocturna-jar-with-dependencies.jar` | `0 2 * * *` | Todos los días a las 02:00 |

```powershell
docker compose run --rm -T donaciones java -jar reintento-envio-notificaciones-jar-with-dependencies.jar
docker compose run --rm -T donaciones java -jar publicar-inactividad-de-donante-jar-with-dependencies.jar
docker compose run --rm -T donaciones java -jar crear-solicitudes-de-asignacion-jar-with-dependencies.jar
docker compose run --rm -T logistica java -jar planificacion-nocturna-jar-with-dependencies.jar
```

En local no hay planificador: las tareas se disparan a mano. En la VM de Azure los horarios se configuran en el crontab de la VM (`crontab -e`), después de poner la zona horaria de Buenos Aires con `sudo timedatectl set-timezone America/Argentina/Buenos_Aires`. Ejemplo de una línea:

```
0 * * * * cd ~/<carpeta-del-repo> && docker compose run --rm -T donaciones java -jar reintento-envio-notificaciones-jar-with-dependencies.jar >> ~/cron.log 2>&1
```

## 7. Problemas frecuentes

| Síntoma | Causa probable | Qué hacer |
| --- | --- | --- |
| `docker info` da error de conexión | Docker Desktop cerrado, o el motor todavía está arrancando | Abrirlo y esperar *Engine running* |
| `port is already allocated`, o no abre el puerto 80 u 8080 | Otro programa (por ejemplo un servidor de IntelliJ) usa ese puerto | Cerrarlo, o cambiar el número de la izquierda en `ports` del compose |
| `password authentication failed` | Se cambió `DONATRACK_DB_PASSWORD` con el volumen ya creado; Postgres conserva la clave vieja | `docker compose down -v` y volver a levantar (borra los datos de prueba) |
| `Unable to access jarfile` | El módulo no generó ese jar | `mvn -DskipTests package` y revisar las carpetas `target`; revisar el pom |
| `ClassNotFoundException` al correr un main | El paquete o la clase no coinciden, o la clase está en `src/test` | Copiar el nombre completo (clic derecho > Copy Reference) y confirmar que esté en `src/main` |
| Mi cambio de código no se nota | Faltó reconstruir la imagen | `docker compose up -d --build` |
| Cambié el `.env` y no se nota | Los contenedores siguen con las variables viejas | `docker compose up -d --force-recreate` |
| Aviso `variable is not set. Defaulting to a blank string` | Falta esa variable en el `.env` | Agregarla (por ejemplo `URL_PUBLICA_LOGISTICA`) |
| Un servidor se cae al arrancar con error de conexión a la base | La base todavía no estaba lista, o falta `DONATRACK_DB_URL` en el compose | `docker compose logs db` y revisar el `environment` del servicio |
|  |  |  |

## 8. Glosario mínimo

- **Imagen:** programa empaquetado con todo lo necesario para correr, como una foto congelada. Se construye con el Dockerfile.
- **Contenedor:** una imagen en ejecución.
- **Servicio:** cada entrada del `docker-compose.yml` (`db`, `donaciones`, `logistica`).
- **Volumen:** almacenamiento que sobrevive a que se recree un contenedor. `dbdata` guarda la base.
- **Red:** Compose crea una red privada donde los servicios se encuentran por nombre (`db`, `donaciones`, `logistica`).
- **Context y target:** `context` es la carpeta que se entrega al constructor de imágenes; `target` es la etapa del Dockerfile que se usa como imagen final.

*Próximo paso: crear la VM en Azure, instalar Docker, clonar el repo, armar el `.env` de la VM y levantar con `docker compose up -d --build`.*

## 9. Despliegue en Azure: lista de preparación

*Qué tener listo y en qué orden hacerlo. Lo que va entre `<...>` se completa con tus datos.*

### A. Esta noche (20 minutos), para no tener sorpresas mañana

1. **Commit y push de la branch** con todo lo de Docker (`Dockerfile`, `docker-compose.yml`, `init.sql`, `.dockerignore`, `.env.example` y los cambios de código). Antes corré `git status`: el `.env` no tiene que aparecer. La VM solo puede clonar lo que esté subido.
2. **Mirar qué te deja hacer tu suscripción de Azure for Students.** Es el mayor riesgo del plan: estas suscripciones suelen tener pocas regiones permitidas (alrededor de cinco, distintas para cada persona) y algunos tamaños de VM bloqueados.
   - Regiones permitidas: Portal de Azure → Policy → Authoring → Assignments → *Allowed locations*.
   - Tamaños: empezá el asistente *Create a virtual machine* (sin terminarlo) y en *See all sizes* mirá cuáles no dicen *not available* ni *blocked*.
   - Alternativa desde Cloud Shell: `az vm list-skus --location <region> --output table`.
3. **Qué tamaño buscar.** Conviene tener al menos 4 GB de RAM (dos JVM, Postgres y Docker): por ejemplo `Standard_B2s`, `Standard_B2ls_v2` o `Standard_B2als_v2`; con 8 GB (`B2as_v2`, `B2s_v2`) sobra. Con 2 GB (`B1ms`) quedaría muy justo. Los tamaños de 1 GB (`B1s`, `B2ats_v2`, `B2ts_v2`), que son los que más se recomiendan en cuentas de estudiantes, no alcanzan. Verificá la RAM exacta de cada tamaño en el portal.
4. **Token de GitHub de solo lectura**, si el repo es privado (GitHub → Settings → Developer settings → Fine-grained tokens, con permiso de lectura del contenido solo para ese repo). El `git clone` de la VM lo va a pedir.
5. **Claves nuevas.** Regenerá las de SendGrid y Twilio (avisale a tu compañera) y guardalas, junto con una contraseña larga para la base, en un administrador de contraseñas: las vas a pegar en el `.env` de la VM.
6. **Chequear SSH en Windows:** `ssh -V` en PowerShell tiene que responder (el cliente viene incluido en Windows 10 y 11).

### B. Crear la VM en el portal

| Campo | Valor sugerido | Por qué |
| --- | --- | --- |
| Resource group | Uno nuevo, por ejemplo `donatrack-rg` | Al terminar borrás el grupo y desaparece todo lo que cobra |
| Región | Una de las permitidas; la más cercana si podés elegir | Si falla el despliegue, probá con otra región permitida |
| Availability options | Sin redundancia (*No infrastructure redundancy required*) | Las zonas de disponibilidad a veces causan errores en cuentas de estudiantes |
| Imagen | Ubuntu Server 24.04 LTS (x64) | Es la más documentada y Docker la soporta |
| Tamaño | Ver A.3 |  |
| Autenticación | Clave SSH pública, usuario `azureuser`, *Generate new key pair* | El archivo `.pem` se descarga una sola vez: guardalo |
| Disco | Standard SSD, tamaño por defecto (30 GB) | Alcanza para las imágenes y la base de prueba |
| IP pública | Nueva, SKU Standard (estática) | La IP no cambia si algún día detenés la VM |
| Puertos de entrada | Solo SSH (22) por ahora | El resto se abre después |
| Management | Apagado automático *desactivado* | Las tareas programadas necesitan la VM prendida |

### C. Después de crearla

1. **Nombre DNS:** en la IP pública → Configuration → *DNS name label* (por ejemplo `donatrack`). Queda `<label>.<region>.cloudapp.azure.com`; ese es el nombre público del sitio.
2. **Firewall** (la VM → Networking → Inbound port rules): el 22 solo desde tu IP, y abrí el 80, el 443 y el 8080 (la logística, que abre el link de tracking de los mails). **Nunca abras el 5432.**
3. **Conectarte:** `ssh -i <ruta\al\archivo.pem> azureuser@<nombre-dns>`. Si SSH protesta por los permisos del `.pem`, guardalo en `C:\Users\<vos>\.ssh\` y corré `icacls <archivo.pem> /inheritance:r /grant:r "$($env:USERNAME):(R)"`.
4. **Zona horaria** (para que cron use la hora de Buenos Aires): `sudo timedatectl set-timezone America/Argentina/Buenos_Aires`.
5. **Instalar Docker:** `curl -fsSL https://get.docker.com | sudo sh`, después `sudo usermod -aG docker $USER`, cerrar la sesión SSH y volver a entrar. Verificá con `docker --version` y `docker compose version`.
6. **Probar la salida a internet** con el `curl` de USIG y con el dominio de `planificador.externo.url`.
7. **Clonar:** `git clone -b <branch> <url-del-repo>` (con el token si es privado) y entrar a la carpeta.
8. **Armar el `.env`:** `cp .env.example .env` y `nano .env`. Valores: la contraseña nueva de la base, `URL_PUBLICA_LOGISTICA=http://<nombre-dns>:8080` y las claves nuevas. Se guarda con Ctrl+O y Enter, y se sale con Ctrl+X.
9. **Levantar:** `docker compose up -d --build`. El primer build tarda y usa bastante memoria: miralo con `free -h` desde otra sesión SSH.
10. **Verificar:** `docker compose ps`, abrir `http://<nombre-dns>`, cargar el admin (sección 5) y correr una tarea a mano (sección 6).
11. **Programar las tareas:** las cuatro líneas de `crontab -e` (sección 6), ajustando la carpeta del repo.
12. **HTTPS con Caddy:** recién cuando todo lo anterior ande.

### D. Plan B si Azure no te deja crear una VM de 4 GB

- Probá otra región permitida, otro tamaño de la serie B, o quitá las zonas de disponibilidad.
- Si todos los tamaños aparecen bloqueados, se puede pedir a soporte que habiliten cuota de VM en una región, o pasar la suscripción a pago por uso (requiere tarjeta).
- Con tarjeta, AWS u Oracle siguen siendo opciones, y el mismo `docker-compose.yml` sirve en cualquier VM Linux. Sin tarjeta habría que rediseñar para un servicio PaaS, que es bastante más trabajo. Conviene decidirlo temprano.

### E. Costos y cierre

- Una VM de la serie B de 4 GB cuesta del orden de centavos de dólar por hora. Con $100 de crédito alcanza de sobra, pero mirá el consumo en *Cost Management* durante la semana.
- Después de la entrega, borrá el *resource group* completo.
