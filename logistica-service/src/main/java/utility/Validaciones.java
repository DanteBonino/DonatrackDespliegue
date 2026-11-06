package utility;

import exceptions.DatosInvalidosException;

public class Validaciones {
  private Validaciones() {}

  public static <T> T requerido(T valor, String nombreDelCampo) {
    if (valor == null) {
      throw new DatosInvalidosException(nombreDelCampo + " es obligatorio");
    }
    return valor;
  }

  public static <T> T actualizar(T valorNuevo, T valorActual, String nombreDelCampo, boolean parcial) {
    if (valorNuevo == null) {
      if (parcial) {
        return valorActual;
      }
      throw new DatosInvalidosException(nombreDelCampo + " es obligatorio");
    }
    return valorNuevo;
  }
}
