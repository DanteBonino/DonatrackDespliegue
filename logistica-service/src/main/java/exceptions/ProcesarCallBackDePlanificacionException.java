package exceptions;

public class ProcesarCallBackDePlanificacionException extends RuntimeException {
  public ProcesarCallBackDePlanificacionException() {
    super(
        "Error al deserializar el callback JSON"
    );
  }
}
