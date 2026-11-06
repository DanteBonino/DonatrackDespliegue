package Direccion;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Direccion {
  @Embedded
  private Calle calle;
  @Embedded
  private Posicion posicion;

  protected Direccion() {

  }
  public Direccion(Calle calle, Posicion posicion) {
    this.calle = calle;
    this.posicion = posicion;
  }

  public Calle getCalle() {
    return calle;
  }

  public Posicion getPosicion() {
    return posicion;
  }

  public Boolean esLaMismaQue(Direccion direccion) {
    return getCalle().esLaMismaQue(direccion.getCalle()) && getPosicion().esIgual(direccion.getPosicion());
  }
}
