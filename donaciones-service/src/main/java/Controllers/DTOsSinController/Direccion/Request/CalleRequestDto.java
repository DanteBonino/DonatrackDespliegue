package Controllers.DTOsSinController.Direccion.Request;

import Direccion.Calle;
import Direccion.builders.CalleBuilder;

public record CalleRequestDto(String nombre, int altura) {
  public Calle crearInstancia() {
    return new CalleBuilder()
        .conNombre(nombre)
        .conAltura(altura)
        .build();
  }
}
