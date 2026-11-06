package builders;


import domain.Calle;
import exceptions.AlturaDeCalleInvalida;
import exceptions.NombreDeCalleInvalido;
import utility.Validaciones;

public class CalleBuilder {
  private String nombre;
  private int altura;

  public CalleBuilder conNombre(String nombre) {
    this.nombre = nombre;
    return this;
  }

  public CalleBuilder conAltura(int altura) {
    this.altura = altura;
    return this;
  }

  public Calle build() {
    Validaciones.requerido(nombre, "calle");
    Validaciones.requerido(altura, "calle");
    if(altura < 0) throw new AlturaDeCalleInvalida();
    if(nombre.isEmpty()) throw new NombreDeCalleInvalido();
    return new Calle(nombre, altura);
  }

}
