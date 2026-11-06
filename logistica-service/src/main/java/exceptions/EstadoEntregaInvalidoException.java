package exceptions;

import domain.EstadoEntrega;

public class EstadoEntregaInvalidoException extends RuntimeException {
  public EstadoEntregaInvalidoException(String accion, EstadoEntrega estadoActual) {
    super("No se puede " + accion + " una entrega que se encuentra en estado " + estadoActual + ".");
  }
}
