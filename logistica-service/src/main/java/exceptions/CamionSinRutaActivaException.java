package exceptions;

public class CamionSinRutaActivaException extends RuntimeException {
  public CamionSinRutaActivaException(String patente) {
    super("El camion cuya patente es: " + patente + " no tiene una ruta asignada en la fecha de hoy, por ende no puede actualizarse su posición ni iniciar su ruta.");
  }
}
