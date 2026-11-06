package datosBaseDeDatos;

import Donaciones.DonacionIndependiente;
import Injector.Injector;
import MockDataLoader.MockDataLoader;

public class CargarDatosDePrueba {
  public static void main(String[] args) {
    Injector injector = new Injector();
    MockDataLoader.cargar(injector);
  }
}