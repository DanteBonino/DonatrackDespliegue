package BusNotificaciones;

import MedioDeNotificacion.MedioDeNotificacion;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;

public class BusNotificaciones {
  private MedioNotificacionFactory factory;

  public BusNotificaciones(MedioNotificacionFactory factory) {
    this.factory = factory;
  }

  public void procesar(Notificacion notificacion) {
    TipoDeMedio tipoMedio = notificacion.getMedioDeNotificacion();
    MedioDeNotificacion medioReal = this.factory.construirMedio(tipoMedio);
    medioReal.enviarNotificacion(notificacion);
  }
}
