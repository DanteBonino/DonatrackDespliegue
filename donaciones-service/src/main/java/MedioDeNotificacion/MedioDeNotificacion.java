package MedioDeNotificacion;

import ExceptionHandler.NotificacionNoEnviableException;
import Notificaciones.Notificacion;
import Repositories.Notificaciones.INotificacionesRepository;

abstract public class MedioDeNotificacion { ;

  public void enviarNotificacion(Notificacion unaNotificacion) {
    this.validarEnvio(unaNotificacion);
    try {
      this.envioDeNotificacion(unaNotificacion);
      unaNotificacion.pasarAEntregada();
    } catch (RuntimeException e) {
      unaNotificacion.serRebotada();
    }
  }

  private void validarEnvio(Notificacion unaNotificacion) {
    this.validarQueSeaEnviable(unaNotificacion);
    this.validarQueSeaEnviablePorEsteMedio(unaNotificacion);
  }

  private void validarQueSeaEnviable(Notificacion unaNotificacion) {
    if(!unaNotificacion.esEnviable()) throw new NotificacionNoEnviableException();
  }

  private void validarQueSeaEnviablePorEsteMedio(Notificacion unaNotificacion) {
    if(!unaNotificacion.esEnviablePorElMedio(this.getTipoDeMedio())) throw new NotificacionNoEnviableException();
  }

  abstract protected void envioDeNotificacion(Notificacion unaNotificacion);

  abstract public TipoDeMedio getTipoDeMedio();


}
