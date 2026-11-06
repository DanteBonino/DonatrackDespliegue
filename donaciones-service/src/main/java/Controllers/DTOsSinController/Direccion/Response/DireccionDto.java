package Controllers.DTOsSinController.Direccion.Response;

import Direccion.*;
import Direccion.builders.DireccionBuilder;
import ServiceLocator.ServiceLocator;

public record DireccionDto(CalleDto calle, PosicionDto posicion) {


  public static DireccionDto desde(Direccion direccion) {
    return new DireccionDto(
        CalleDto.desde(direccion.getCalle()),
        PosicionDto.desde(direccion.getPosicion())
    );
  }
}
