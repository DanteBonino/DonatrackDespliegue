package exceptions;

public class RutaSinEntregasException extends RuntimeException {
  public RutaSinEntregasException(String idRuta) {
    super("No puede iniciarse la ruta '" + idRuta + "' porque no tiene entregas cargadas.");
  }
}
