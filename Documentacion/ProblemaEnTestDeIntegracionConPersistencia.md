# `WithSimplePersistenceUnit` usa un `EntityManager` por hilo

## Qué está pasando

`entityManager()` (el que nos da `WithSimplePersistenceUnit` vía mixin) no es un único `EntityManager` compartido por toda la app. Es **uno por hilo**. Tiene sentido: si fuera uno solo compartido, Javalin (que atiende requests concurrentes en distintos hilos) rompería todo el tiempo, porque `EntityManager` no es thread-safe.

Consecuencia directa: dos hilos distintos que llaman a `entityManager()` obtienen **dos persistence contexts completamente separados**, aunque estén apuntando a la misma base de datos.

## Por qué esto rompe los tests de integración

Cuando un test usa `JavalinTest.test(app, ...)`, la request HTTP corre de verdad, contra un servidor Jetty real — en un hilo del pool de Jetty (`JettyServerThreadPool-N`), **distinto** del hilo del test de JUnit (`main`).

Ejemplo real que nos pasó:

```java
// setUp() — corre en el hilo "main"
camionConRutaEnCurso = new Camion(...);
flotaRepository.registrar(camionConRutaEnCurso); // EntityManager del hilo "main"

// ... se hace un POST HTTP real ...
// El Controller corre en un hilo de Jetty, con SU PROPIO EntityManager
```

La request modifica la base **correctamente** (por eso da 200 OK, sin ninguna excepción). El problema aparece recién al verificar el resultado desde el test:

```java
Camion actualizado = flotaRepository.buscarPorPatente("BBB222");
// Esto corre en el hilo "main" otra vez, con el EntityManager del hilo "main"
```

Acá está la trampa de JPA/Hibernate: si el objeto con ese `@Id` **ya está gestionado** en el persistence context que ejecuta la query (porque `camionConRutaEnCurso` se cargó ahí en el `setUp()`), Hibernate **no pisa el objeto en memoria con los valores frescos de la fila**. La consulta SQL se ejecuta contra la base (que ya tiene el dato actualizado), pero el objeto Java que te devuelve sigue siendo el mismo de siempre, con los valores viejos.

Se puede confirmar imprimiendo el hilo y el `identityHashCode` de `entityManager()` en ambos lados — van a ser distintos.

## Cuándo hace falta un `refresh()` a mano

**Regla práctica:** en cualquier test de integración que dispare una request HTTP real (`JavalinTest.test`, o cualquier cliente HTTP contra el server levantado), si vas a verificar el estado de una entidad que:

1. ya tenías referenciada **antes** de la request (por ejemplo, un objeto creado en `setUp()`), y
2. esa entidad fue modificada **por el lado del servidor** durante la request,

entonces no alcanza con volver a buscarla (`buscarPorPatente`, `find`, etc.) — te va a devolver el mismo objeto cacheado. Hace falta forzar la recarga:

```java
Camion actualizado = flotaRepository.buscarPorPatente("BBB222");
entityManager().refresh(actualizado); // fuerza a pisar el estado en memoria con lo que hay en la base
assertThat(actualizado.getPosicionActual().getLatitud()).isEqualTo(-34.6);
```

No hace falta este paso si:
- El test no dispara HTTP real y todo corre en un solo hilo/transacción (por ejemplo, tests que llaman directo a métodos de dominio o del repositorio sin pasar por Javalin).
- No tenías ninguna referencia previa al objeto — si lo buscás por primera vez después de la request, en el mismo hilo del test, te vas a topar con el mismo problema si CUALQUIER otra entidad con ese id ya estaba cacheada; si no había nada cacheado, la query trae el objeto ya actualizado sin problema (porque no hay nada en el identity map con qué "confundirse").

## ¿Esto es un problema en producción?

En el caso normal, no. Un cliente HTTP real (otro servicio, un front, Bruno) nunca tiene una referencia a un objeto Java del server — solo ve la respuesta. El bug que vimos es un artefacto de que el test comparte el mismo proceso Java que el server y puede quedarse con una referencia vieja entre bastidores.

Sí hay un matiz real para tener en cuenta: Jetty **reutiliza** los hilos de su pool entre requests a lo largo del tiempo. Si el `EntityManager` de un hilo nunca se limpia, ese hilo acumula un persistence context que sobrevive de una request a la siguiente. Si en algún momento ese mismo hilo reciclado atiende una request que necesita leer una fila que él mismo ya había cacheado en una request anterior — y esa fila cambió mientras tanto (por otro hilo, o por un proceso externo) — podría devolver el valor viejo en lugar del actual.

Es un caso borde poco frecuente (requiere que la misma fila sea tocada por dos requests que además caigan en el mismo hilo reciclado), pero es evitable del todo con un cambio chico.

## Recomendación: limpiar el persistence context en el filtro global

La forma más simple de eliminar esta clase de problema por completo — tanto el de los tests como el matiz de producción — es que `FiltroGlobalDeTransacciones` limpie el persistence context al terminar cada request, para que ningún hilo arrastre entidades cacheadas de una request a la siguiente:

```java
config.router.handlerWrapper(endpoint -> {
  if (endpoint.method.isHttpMethod() && !endpoint.method.equals(HandlerType.GET)) {
    return ctx -> withTransaction(() -> {
      try {
        endpoint.handler.handle(ctx);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new TransaccionException(e);
      } finally {
        entityManager().clear(); // cada request empieza "limpio"
      }
      return null;
    });
  }
  return endpoint.handler;
});
```

Con esto, cada request arranca sin nada cacheado de requests anteriores en ese mismo hilo — el equivalente a que cada request tenga su persistence context "virtualmente nuevo", aunque el `EntityManager` de fondo se siga reutilizando por hilo.