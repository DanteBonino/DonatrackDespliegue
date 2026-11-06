package datosBaseDeDatos;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utils.BaseDeDatos;

public class ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public void ejecutar() {
    BaseDeDatos.configurarDesdeEntorno();
  }
}
