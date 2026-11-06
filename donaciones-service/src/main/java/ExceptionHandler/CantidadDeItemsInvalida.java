package ExceptionHandler;

public class CantidadDeItemsInvalida extends RuntimeException {
  public CantidadDeItemsInvalida() {
    super("No se puede crear un Lote sin donaciones");
  }
}
