package exceptions;

public class NombreDeCalleInvalido extends RuntimeException {
  public NombreDeCalleInvalido() {
    super("El nombre de la calle no puede ser vacío");
  }
}
