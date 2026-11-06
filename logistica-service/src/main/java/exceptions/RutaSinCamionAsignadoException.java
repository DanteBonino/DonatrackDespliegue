package exceptions;

public class RutaSinCamionAsignadoException extends RuntimeException {
  public RutaSinCamionAsignadoException(String idRuta) {
    super("No puede iniciarse la ruta '" + idRuta + "' porque no tiene un camion asignado.");
  }
}
