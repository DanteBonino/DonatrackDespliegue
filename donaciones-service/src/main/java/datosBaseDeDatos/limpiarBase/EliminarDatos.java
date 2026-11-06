package datosBaseDeDatos.limpiarBase;

import datosBaseDeDatos.ModificadorBaseDeDatos;

public class EliminarDatos extends ModificadorBaseDeDatos {
  public static void main(String[] args) {
    new EliminarDatos().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    new LimpiadorBaseDeDatosPostgres().limpiar();
  }
}