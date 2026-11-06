package builders;


import domain.Posicion;

public class PosicionBuilder {
  private Double latitud;
  private Double longitud;

  public PosicionBuilder conLatitud(Double latitud) {
    this.latitud = latitud;
    return  this;
  }

  public PosicionBuilder conLongitud(Double longitud) {
    this.longitud = longitud;
    return  this;
  }

  public Posicion build() {
    if (latitud < -90 || latitud > 90) {
      throw new IllegalArgumentException("Latitud inválida");
    }
    if (longitud < -180 || longitud > 180) {
      throw new IllegalArgumentException("Longitud invalida");
    }
    return new Posicion(latitud, longitud);
  }

}
