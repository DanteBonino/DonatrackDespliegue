# Cambios para Despliegue
- Dejar de usar properties:
```java
public class Configuracion {

  public String getApiKeySendGrid() {
    return Entorno.requerida("SENDGRID_API_KEY");
  }

  public String getEmisorSendGrid() {
    return Entorno.requerida("SENDGRID_EMISOR");
  }

  public String getAccountSidTwilio() { return Entorno.requerida("TWILIO_ACCOUNT_SID");}

  public String getAuthTokenTwilio() { return Entorno.requerida("TWILIO_AUTH_TOKEN");}

  public String getEmisorWhatsAppTwilio() { return Entorno.requerida("TWILIO_EMISOR_WHATSAPP");}

  public String getLogisticaServiceUrl() {
    return Entorno.requerida("URL_PUBLICA_LOGISTICA");
  }
  public String getEmisorSmsTwilio() {
    return Entorno.requerida("TWILIO_EMISOR_SMS");
  }
}
```
- Puntualmente en el Inyector de Logística hay que cambiar el uso de ConfigLoader
- Sacar de los persistence.xml las credenciales de la base de datos de Render y que sí o sí haya que configurar BaseDeDatos, obligando a que se defina en el entorno:
  - "DONATRACK_DB_URL"
  - "DONATRACK_DB_USER"
  - "DONATRACK_DB_PASSWORD"
```java
public final class BaseDeDatos {
  private BaseDeDatos() {

  }

  public static void configurarDesdeEntorno() {
    String url = Entorno.requerida("DONATRACK_DB_URL");
    String user = Entorno.requerida("DONATRACK_DB_USER");
    String password = Entorno.requerida("DONATRACK_DB_PASSWORD");
    WithSimplePersistenceUnit.configure(p -> p
        .set("hibernate.connection.url", url)
        .set("hibernate.connection.username", user)
        .set("hibernate.connection.password", password));
  }

}
```

- Crear la clase Entorno, que abstrae lógica repetida:
```java
public final class Entorno {
  private Entorno() {}

  public static String requerida(String nombre) {
    String valor = System.getenv(nombre);
    if (valor == null || valor.isBlank()) {
      throw new IllegalStateException("Falta la variable de entorno obligatoria: " + nombre);
    }
    return valor;
  }
}
```

- El .env está bien como estaba

- Cambiar el compose, puntualmente agregar en Logistica:
```yml
      DONACIONES_SERVICE_URL: http://donaciones:7070
      PLANIFICADOR_EXTERNO_URL: http://planificador-externo-de-rutas.com/planificar
      DEPOSITO_DIRECCION: "Av. Medrano 951, CABA, Argentina"
      DEPOSITO_LATITUD: "-34.59855526311623"
      DEPOSITO_LONGITUD: "-58.42013582713193"
```

- Si se quisiera hacer que no rompan los tests habría que cambiar los tests que hacen new Inyector en Logística

- Cambiar la FabricaDeDatos para tener un usuarie cuyos datos de contacto sean verificables

- Crear el concepto de Telefono (tranquilamente el código de area podría ser un enum porque son valores fijos que dificilmente cambien):
  - Clase (X)
  - Builder (X)
  - Factory de TelefonoArgentino (X)
  - Hacerlo embedabble (X)
  - Agregarlo a los persistence (X)
  - Cambiar todo aquel que usa DatosDeContacto (X)
  - Cambiar los medios de notificación de SMS y WPP para que pidan el número adecuado
- En realidad deberíamos dejar que puedan elegir el código de área que quieran, pero por simplicidad vamos a asumir siempre teléfono argentino

## Configuracion crontab
*/5 * * * *   cd /home/donatrack_azure/donatrack && (date; docker compose run --rm -T donaciones java -jar reintento-envio-notificaciones-jar-with-dependencies.jar) >> /home/donatrack_azure/logs/reintento-notificaciones.log 2>&1
0 8 * * 1-6 cd /home/donatrack_azure/donatrack && (date; docker compose run --rm -T donaciones java -jar publicar-inactividad-de-donante-jar-with-dependencies.jar) >> /home/donatrack_azure/logs/publicar-inactividad.log 2>&1
0 23 * * *  cd /home/donatrack_azure/donatrack && (date; docker compose run --rm -T donaciones java -jar crear-solicitudes-de-asignacion-jar-with-dependencies.jar) >> /home/donatrack_azure/logs/solicitudes-asignacion.log 2>&1
0 2 * * *   cd /home/donatrack_azure/donatrack && (date; docker compose run --rm -T logistica java -jar planificacion-nocturna-jar-with-dependencies.jar) >> /home/donatrack_azure/logs/planificacion-nocturna.log 2>&1