package ExceptionHandler;

public class ConfiguracionNoCargadaException extends RuntimeException {
  public ConfiguracionNoCargadaException(Throwable error) {
    super("La configuracion no fue cargada con exito ", error);
  }

  public ConfiguracionNoCargadaException(String mensaje) {
    super(mensaje);
  }
}
