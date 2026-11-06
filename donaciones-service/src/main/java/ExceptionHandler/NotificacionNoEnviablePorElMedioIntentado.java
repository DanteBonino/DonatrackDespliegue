package ExceptionHandler;

public class NotificacionNoEnviablePorElMedioIntentado extends RuntimeException {
  public NotificacionNoEnviablePorElMedioIntentado() {
    super("La notificacion no es enviable por este medio");
  }
}
