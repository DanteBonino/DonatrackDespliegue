# Cómo agregar una nueva tarea programada

## 1. Crear la clase de entrada (`main`)

Cada tarea programada necesita una clase con un método `main` dentro del paquete `TareasProgramadas`. Esta clase debe ser mínima: solo construye el `AppContext` y delega el trabajo real a una clase inyectable.

```java
package TareasProgramadas;

public class NombreDeLaNuevaTareaMain {

  public static void main(String[] args) {
    AppContext appContext = new AppContext();
    new NombreDeLaNuevaTarea(
        appContext.getAlgunRepository()
    ).ejecutar();
  }

}
```

La lógica de negocio va en una clase separada, con sus dependencias inyectadas por constructor (no en el `main`), para que sea testeable sin necesidad de `AppContext`.

## 2. Agregar una `execution` en el `pom.xml`

Dentro del `maven-assembly-plugin`, agregar una nueva `<execution>` con:

- Un `id` único y descriptivo (no reusar uno existente).
- El `mainClass` apuntando al paquete y nombre completo de la clase creada en el paso 1.
- Un `finalName` descriptivo para identificar el jar generado.

```xml
<execution>
    <id>nombre-de-la-nueva-tarea</id>
    <phase>package</phase>
    <goals>
        <goal>single</goal>
    </goals>
    <configuration>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <archive>
            <manifest>
                <mainClass>TareasProgramadas.NombreDeLaNuevaTareaMain</mainClass>
            </manifest>
        </archive>
        <finalName>nombre-de-la-nueva-tarea</finalName>
    </configuration>
</execution>
```

Esta `execution` se agrega **dentro del mismo `<executions>`** que ya tienen las demás tareas, no en un plugin nuevo.

## 3. Generar los jars

Desde la raíz del proyecto (donde está el `pom.xml`):

```bash
mvn clean package
```

Si los tests frenan el build y solo se necesita generar los jars rápido:

```bash
mvn clean package -DskipTests
```

Esto genera **todos** los jars declarados en el `pom.xml` (no solo el nuevo), en `target/`.

## 4. Verificar que el jar se generó

En linux:
```bash
ls -la target/*.jar
```
En windows:
```cmd
dir target\*.jar
```

Debería aparecer `target/nombre-de-la-nueva-tarea-jar-with-dependencies.jar` junto a los jars de las demás tareas.

## 5. Probar el jar manualmente

Antes de programarlo en el cron, correrlo a mano para descartar errores de configuración:

```bash
java -jar target/nombre-de-la-nueva-tarea-jar-with-dependencies.jar
```

Si tira `ClassNotFoundException` o similar, revisar que el `mainClass` del `pom.xml` coincida exactamente (paquete completo, mayúsculas/minúsculas) con la clase real.

## 6. Agregar la entrada al `crontab`

```bash
crontab -e
```

Agregar una línea con la ruta **absoluta** al `.jar` (el cron no usa el `$PATH` ni el directorio de trabajo de una shell interactiva):

```
* * * * * /ruta/absoluta/a/java -jar /ruta/absoluta/target/nombre-de-la-nueva-tarea-jar-with-dependencies.jar
```

Para obtener la ruta absoluta de `java`:

```bash
which java
```

## 7. (Recomendado) Redirigir el output a un log

Para poder revisar después si la tarea corrió bien, redirigir stdout y stderr a un archivo:

```
* * * * * /ruta/absoluta/a/java -jar /ruta/absoluta/target/nombre-de-la-nueva-tarea-jar-with-dependencies.jar >> /ruta/absoluta/logs/nombre-de-la-nueva-tarea.log 2>&1
```