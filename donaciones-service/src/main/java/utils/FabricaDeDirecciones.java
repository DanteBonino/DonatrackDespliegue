package utils;

import Direccion.*;
import Direccion.builders.CalleBuilder;
import Direccion.builders.DireccionBuilder;
import Direccion.builders.PosicionBuilder;
import com.twilio.rest.insights.v1.Call;

public class FabricaDeDirecciones {
  public static Direccion nuevaDireccion(Calle calle, Posicion posicion) {
    return new DireccionBuilder()
        .conCalle(calle)
        .conPosicion(posicion)
        .build();
  }

  public static Direccion direccionCualquiera() {
    return nuevaDireccion(
        calleCualquiera(),
        posicionCualquiera()
    );
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
