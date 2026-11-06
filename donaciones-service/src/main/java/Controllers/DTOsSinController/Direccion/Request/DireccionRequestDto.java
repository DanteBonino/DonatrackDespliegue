package Controllers.DTOsSinController.Direccion.Request;

import Direccion.*;
import Direccion.builders.CalleBuilder;
import Direccion.builders.DireccionBuilder;
import ServiceLocator.ServiceLocator;

public record DireccionRequestDto (String nombre, int altura) {
  public Direccion crearInstancia() {
    Calle unaCalle = new CalleBuilder()
        .conNombre(nombre)
        .conAltura(altura)
        .build();
    return new DireccionBuilder()
        .conCalle(unaCalle)
        .conPosicion(ServiceLocator.obtenerGeolocalizador().getPosicionDe(unaCalle))
        .build();
  }
}
