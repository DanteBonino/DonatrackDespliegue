package MedioDeNotificacion.Mensajeria.SMS;


import MedioDeNotificacion.MedioDeNotificacion;
import MedioDeNotificacion.Mensajeria.Adapter.ClienteMensajeria;
import MedioDeNotificacion.Mensajeria.Adapter.Mensaje;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;

public class MedioSMS extends MedioDeNotificacion {
  private ClienteMensajeria clienteMensajeria;


  public MedioSMS(ClienteMensajeria clienteMensajeria) {
    this.clienteMensajeria = clienteMensajeria;
  }

  public void envioDeNotificacion(Notificacion notificacion) {
    this.clienteMensajeria.enviarMensaje(new Mensaje(notificacion.getReceptor().getTelefono().getNumeroSms(), notificacion.getMensaje()));
  }

  public TipoDeMedio getTipoDeMedio() {
    return TipoDeMedio.SMS;
  }

}
