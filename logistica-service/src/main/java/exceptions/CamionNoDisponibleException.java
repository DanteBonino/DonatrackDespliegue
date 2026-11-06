package exceptions;

import java.time.LocalDate;

public class CamionNoDisponibleException extends RuntimeException {
  public CamionNoDisponibleException(String patente) {
    this(patente, LocalDate.now());
  }

  public CamionNoDisponibleException(String patente, LocalDate fecha) {
    super("No puede asignarse la ruta al camion con patente " + patente + " porque no esta disponible o ya tiene una ruta activa en la fecha: " + fecha);
  }
}
