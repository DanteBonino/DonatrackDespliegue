package ExceptionHandler;


import EstadoDonacion.EstadoDonacion;

public class TransicionInvalidaDeEstado extends RuntimeException {
  public TransicionInvalidaDeEstado(EstadoDonacion estadoActual, EstadoDonacion estadoDestino) {
    super("No se puede transicionar desde " + estadoActual + " hacia " + estadoDestino);
  }
}
