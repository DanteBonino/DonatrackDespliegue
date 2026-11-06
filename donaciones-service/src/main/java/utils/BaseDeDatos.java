package utils;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

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