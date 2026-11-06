package exceptions;

public class TransaccionException extends RuntimeException {
  public TransaccionException(Throwable cause) {
    super("Ocurrió un error no controlado durante la ejecución de la transacción.", cause);
  }
}
