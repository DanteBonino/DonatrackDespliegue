package tareasprogramadas;

import inicializacion.ChequeadorInicializacionBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public abstract class TareaProgramada implements WithSimplePersistenceUnit {
  public void ejecutar() {
    ChequeadorInicializacionBaseDeDatos.chequearBaseDeDatos();
    withTransaction(this::ejecutarTarea);
  }

  abstract protected void ejecutarTarea();
}
