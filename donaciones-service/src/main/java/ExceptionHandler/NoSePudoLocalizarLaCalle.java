package ExceptionHandler;

import Direccion.Calle;

public class NoSePudoLocalizarLaCalle extends RuntimeException {
  public NoSePudoLocalizarLaCalle(Calle calle) {
    super(
        "No se pudo localizar la calle: " + calle.getFormatoLegible() + " ya sea porque no es válida o está caído el servicio para lograrlo"
    );
  }
}
