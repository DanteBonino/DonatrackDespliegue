package Controllers.DTOsSinController.Direccion.Response;

import Direccion.Calle;
import Direccion.builders.CalleBuilder;

public record CalleDto(String nombre, int altura) {

  public static CalleDto desde(Calle calle) {
    return new CalleDto(calle.getNombre(), calle.getAltura());
  }
}
