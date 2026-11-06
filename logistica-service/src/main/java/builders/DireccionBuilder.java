package builders;


import domain.Calle;
import domain.Direccion;
import domain.Posicion;
import utility.Validaciones;

public class DireccionBuilder {
  private Calle calle;
  private Posicion posicion;

  public DireccionBuilder conCalle(Calle calle) {
    this.calle = calle;
    return this;
  }

  public DireccionBuilder conPosicion(Posicion posicion) {
    this.posicion = posicion;
    return this;
  }

  public Direccion build() {
    Validaciones.requerido(calle, "calle");
    Validaciones.requerido(posicion, "calle");
    return new Direccion(calle, posicion);
  }

}
