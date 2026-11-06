package ExceptionHandler;

public class NotificacionNoEnviableException extends RuntimeException {
  public NotificacionNoEnviableException() {
    super("La notificacion ya fue enviada previamente, no se puede volver a enviar");
  }
}
