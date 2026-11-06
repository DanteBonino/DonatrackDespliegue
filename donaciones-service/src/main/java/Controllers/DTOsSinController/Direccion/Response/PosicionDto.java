package Controllers.DTOsSinController.Direccion.Response;

import Direccion.Posicion;
import Direccion.builders.PosicionBuilder;

public record PosicionDto(Double longitud, Double latitud) {

  public static PosicionDto desde(Posicion posicion) {
    return new PosicionDto(posicion.getLongitud(), posicion.getLatitud());
  }
}
