package Direccion;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Posicion {

  @Column(name = "posicion_latitud")
  private Double latitud;
  @Column(name = "posicion_longitud")
  private Double longitud;

  protected Posicion() {}

  public Posicion(double latitud, double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }

  public Double getLatitud() {
    return latitud;
  }

  public Double getLongitud() {
    return longitud;
  }



  public boolean esIgual(Posicion otraPosicion) {
    return getLatitud().equals(otraPosicion.getLatitud()) && getLongitud().equals(otraPosicion.getLongitud());
  }


}