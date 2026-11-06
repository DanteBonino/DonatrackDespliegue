package utility;

import builders.CalleBuilder;
import builders.DireccionBuilder;
import builders.PosicionBuilder;
import domain.Calle;
import domain.Entrega;
import domain.Posicion;
import domain.eventos.EntregaPublicador;
import domain.Direccion;

public class FabricaDeDatos {
  public static Entrega nuevaEntrega(String idDonacion, String idEntidadBeneficiaria) {
    return nuevaEntregaConPublicador(idDonacion, idEntidadBeneficiaria, new EntregaPublicador());
  }

  public static Entrega nuevaEntregaConPublicador(String idDonacion, String idEntidadBeneficiaria, EntregaPublicador publicador) {
    return new Entrega(idDonacion, idEntidadBeneficiaria, unaDireccionCualquiera(),publicador);
  }

  public static Direccion unaDireccionCualquiera() {
    return new DireccionBuilder()
        .conCalle(calleCualquiera())
        .conPosicion(posicionCualquiera())
        .build();
  }

  public static Calle calleCualquiera() {
    return new CalleBuilder()
        .conAltura(1234)
        .conNombre("Avenida Rivadavia")
        .build();
  }

  public static Posicion posicionCualquiera() {
    return new PosicionBuilder()
        .conLongitud(-58.383996)
        .conLatitud(-34.608644)
        .build();
  }
}
