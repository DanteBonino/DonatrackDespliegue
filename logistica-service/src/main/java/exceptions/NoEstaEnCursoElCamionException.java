package exceptions;

public class NoEstaEnCursoElCamionException extends RuntimeException {
  public NoEstaEnCursoElCamionException(String patente) {
    super("El Camion, cuya patente es " + patente + ", no se encuentra recorriendo una Ruta actualmente");
  }
}
