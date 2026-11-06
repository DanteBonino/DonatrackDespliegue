package datosBaseDeDatos;

import config.Configuracion;
import datosmock.MockDataLoader;
import domain.Entrega;
import injector.LogisticaInjector;
import utility.BaseDeDatos;

public class CargarDatosDePrueba {
  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    LogisticaInjector injector = new LogisticaInjector();
    MockDataLoader.cargar(injector.getPosicionDeposito());
  }
}