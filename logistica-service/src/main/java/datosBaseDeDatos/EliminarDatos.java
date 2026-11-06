package datosBaseDeDatos;

import config.Configuracion;
import datosmock.MockDataLoader;
import injector.LogisticaInjector;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utility.BaseDeDatos;

public class EliminarDatos {
  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    new LimpiadorBaseDeDatosPostgres().limpiar();
  }
}