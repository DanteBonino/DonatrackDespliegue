package ExceptionHandler;

public class MensajeNoEnviadoException extends RuntimeException {
  public MensajeNoEnviadoException(Throwable error) {
    super("El mensaje no fue enviado", error);
  }
}
