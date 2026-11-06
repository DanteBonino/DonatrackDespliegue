package ExceptionHandler;


public class DestinatarioSinMedioPreferidoException extends RuntimeException {
  public DestinatarioSinMedioPreferidoException() {
    super("No se encuentra el medio de contacto preferido del destinatario");
  }
}
