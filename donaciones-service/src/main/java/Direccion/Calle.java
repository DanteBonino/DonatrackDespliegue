package Direccion;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Calle {
  @Column(name = "calle_nombre")
  private String nombre;
  @Column(name = "calle_altura")
  private int altura;

  protected Calle() {

  }
  public Calle (String nombre, int altura) {
    this.altura = altura;
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public int getAltura() {
    return altura;
  }

  public String getFormatoLegible() {
    return nombre + " " + altura;
  }

  public Boolean esLaMismaQue(Calle otraCalle) {
    return nombre.equals(otraCalle.getNombre()) && getAltura() == otraCalle.getAltura();
  }
}
