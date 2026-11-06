package ExceptionHandler;

public class SubCategoriaInvalidaException extends RuntimeException {
  public SubCategoriaInvalidaException() {
    super("No se puede crear una condicion de aceptabilidad según el estado para una SubCategoria que perecedera");
  }
}
