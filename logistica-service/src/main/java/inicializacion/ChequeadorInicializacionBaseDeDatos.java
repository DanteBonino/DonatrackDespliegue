package inicializacion;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class ChequeadorInicializacionBaseDeDatos implements WithSimplePersistenceUnit {
  public static void chequearBaseDeDatos() {
    System.out.println("Verificando conexión a la base de dato");
    try {
      new ChequeadorInicializacionBaseDeDatos().withTransaction(() -> {
        System.out.println("Adentro del withTransaction");
        new ChequeadorInicializacionBaseDeDatos().entityManager()
            .createNativeQuery("SELECT 1")
            .getSingleResult();
      });
      System.out.println("CONEXIÓN EXITOSA");
    } catch (Exception e) {
      System.err.println("No hay conexión a la base de datos.");
      System.exit(1);
    }
  }
}
