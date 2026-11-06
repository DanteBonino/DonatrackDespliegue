package ExceptionHandler;

public class CorreoNoEnviadoException extends RuntimeException {
  public CorreoNoEnviadoException(Throwable error) {
    super("El correo no fue enviado", error);
  }
  public CorreoNoEnviadoException(int statusCode) {
    super("El correo no fue enviado. SendGrid respondio " + statusCode);
  }
}
