package exceptions;

import domain.EstadoRuta;

public class EstadoRutaInvalidoException extends RuntimeException {
  public EstadoRutaInvalidoException(String operacion, EstadoRuta estadoActual) {
    super("La operacion '" + operacion + "' requiere que la ruta este en estado PLANIFICADA pero esta en estado " + estadoActual + ".");
  }
}
