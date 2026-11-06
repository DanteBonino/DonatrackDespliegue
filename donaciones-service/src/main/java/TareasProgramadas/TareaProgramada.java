package TareasProgramadas;

import inicializacion.ChequeadorInicializacionBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public abstract class TareaProgramada implements WithSimplePersistenceUnit {
  public final void arrancar() {
    ChequeadorInicializacionBaseDeDatos.chequearBaseDeDatos();
    ejecutar();
  }

  public void ejecutar() {
    withTransaction(this::ejecutarTarea);
  }

  protected abstract void ejecutarTarea();
}