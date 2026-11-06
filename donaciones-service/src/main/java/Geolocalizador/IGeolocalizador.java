package Geolocalizador;

import Direccion.*;

public interface IGeolocalizador {
  default Direccion geoLocalizar(Calle calle) {
    return new Direccion(calle, this.getPosicionDe(calle));
  };

  Posicion getPosicionDe(Calle calle);
}
