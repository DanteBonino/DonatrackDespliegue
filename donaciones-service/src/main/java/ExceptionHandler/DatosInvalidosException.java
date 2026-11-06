package ExceptionHandler;

public class DatosInvalidosException extends RuntimeException {
  public DatosInvalidosException(String mensaje) {
    super(mensaje);
  }
}
