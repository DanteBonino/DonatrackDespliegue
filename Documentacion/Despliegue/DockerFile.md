# DockerFile

## Tipo de estructura: multi-stage builds
- La ventaja está en separar la compilación de Maven de la ejecución
- En una primera etapa se usa la imagen "pesada" de Maven con JDK para compilar el código fuente y generar los .jar
- En una segunda etapa se descarta el entorno de desarrollo y sólo copias el .jar resultante a una imagen limpia y pequeña basada únicamente en JRE
- Esto logra:
  - reducir el tamaño final de la imagen
  - minimizar vulnerabilidades

## Copy

### ¿Qué hace?
- copia archivos o carpetas desde un origen y los pega dentro del FS de la imagen Docker que se está construyendo

```dockerfile
Copy [origen] [destino]
```
- donde origen es la ruta de donde se sacan los archivos y destino es la ruta dentro del contenedor donde se van a guardar (si no existe la carpeta, se crea)

### Comando de nuestro dockerFile:
- COPY --from=build /src/donaciones-service/target/*-jar-with-dependencies.jar ./

### ¿Qué es --from=build?
- Le avisa a Docker que no busque en la PC del desarrollador, sino que lo haga desde el contenedor de la etapa anterior llamada "build" (etapa donde se generaron los jars)

### src
- Directorio de trabajado definido en la etapa build

### donaciones-service
- submódulo

### target
- Carpeta estándar que crea Maven cuando compila un proyecto
- Allí guarda los .jar

### *-jar-with-dependencies.jar
- Todos los jars que encuentre 

### Entonces, ¿qué se hace?
- Copiar todos los Jars que se construyeron en la etapa __build__ relacionados a donaciones service y "pegarlos" en la raíz del directorio /app relacionado al contenedero que corre la imagen de donaciones

## CMD

### ¿Qué hace?
- define el comando default que se ejecuta al correr la imagen en un contenedor

### ¿Se ejecuta sí o sí?
- **NO**, se puede pisar con `docker run ... `

### ¿Tiene alguna ventaja usar CMD y no ENTRYPOINT?
- Lo que se logra usando CMD + formato de lista es:
  - Que se ejecute java como el proceso con PID 1 del contenedor
  - Cuando se ejecuta docker stop o la VM se apaga, la señal de SO (SIGTERM) llega a la aplicación de JAVA directamente, permitiendo que se ejecuten los shutdowns hooks.

### Entonces, ¿qué se hace?
- Se ejecuta el comando de forma default que corre el servidor de donaciones o logística.
- java -jar path es la forma estándar de correr un archivo de java ya compilado

## Expose

### ¿Qué hace?
- declaras el puerto que se va a exponer

