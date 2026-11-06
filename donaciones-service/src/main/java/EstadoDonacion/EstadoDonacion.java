package EstadoDonacion;


import ExceptionHandler.TransicionInvalidaDeEstado;
import java.util.List;

public enum EstadoDonacion {

  PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of(ASIGNACION_REALIZADA, EN_DEPOSITO);
    }
  },
  ASIGNACION_REALIZADA() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of(LISTA_PARA_ENTREGAR);
    }
  },
  EN_DEPOSITO() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of(VENCIDA, PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION);
    }
  },
  ENTREGADA() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of();
    }
  },
  ENTREGA_FALLIDA() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of(EN_DEPOSITO, VENCIDA);
    }
  },
  LISTA_PARA_ENTREGAR() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of(EN_TRASLADO);
    }
  },
  VENCIDA() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of();
    }
  },
  EN_TRASLADO() {
    public List<EstadoDonacion> getDestinosValidosParaTransicionar() {
      return List.of(ENTREGA_FALLIDA, ENTREGADA);
    }
  };

  public void validarTransicion(EstadoDonacion destino){
    if(!this.esDestinoValido(destino)){
      throw new TransicionInvalidaDeEstado(this, destino);
    }
  };

  private Boolean esDestinoValido(EstadoDonacion destino){
    return this.getDestinosValidosParaTransicionar().stream().anyMatch(destino::equals);
  }

  public Boolean esDeTipo(EstadoDonacion estadoDonacion) {
    return this.equals(estadoDonacion);
  }

  public abstract List<EstadoDonacion> getDestinosValidosParaTransicionar();
}
