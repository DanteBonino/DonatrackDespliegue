package exceptions;

public class NoSePuedeModificarElPublicadorUnaVezYaFueSeteadoException extends RuntimeException {
  public NoSePuedeModificarElPublicadorUnaVezYaFueSeteadoException() {
    super("Ya había sido seteado el Publicador anteriormente, no se puede volver a hacerlo");
  }
}
