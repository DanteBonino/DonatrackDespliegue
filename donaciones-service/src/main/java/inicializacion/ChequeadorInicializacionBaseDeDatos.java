package inicializacion;

import ExceptionHandler.NoSePudoConectarALaBaseException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.sql.SQLException;
import org.hibernate.Session;

public class ChequeadorInicializacionBaseDeDatos implements WithSimplePersistenceUnit {
  public static void chequearBaseDeDatos() {
    System.out.println("Verificando conexión a la base de datos");
    ChequeadorInicializacionBaseDeDatos chequeador = new ChequeadorInicializacionBaseDeDatos();
    chequeador.withTransaction(() -> {
      System.out.println("Adentro del withTransaction");
      Session session = chequeador.entityManager().unwrap(Session.class);
      session.doWork(connection -> {
        if (!connection.isValid(2)) {
          throw new NoSePudoConectarALaBaseException("Falló la conexión a la base");
        }
      });
      System.out.println("CONEXIÓN EXITOSA");
    });
  }
}
