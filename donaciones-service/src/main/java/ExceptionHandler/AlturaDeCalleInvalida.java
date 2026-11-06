package ExceptionHandler;

public class AlturaDeCalleInvalida extends RuntimeException {
  public AlturaDeCalleInvalida() {
    super("La altura de una calle no puede ser negativa");
  }
}
