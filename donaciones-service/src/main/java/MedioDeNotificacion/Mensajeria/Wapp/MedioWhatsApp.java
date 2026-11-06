package MedioDeNotificacion.Mensajeria.Wapp;


import MedioDeNotificacion.MedioDeNotificacion;
import MedioDeNotificacion.Mensajeria.Adapter.ClienteMensajeria;
import MedioDeNotificacion.Mensajeria.Adapter.Mensaje;
import Notificaciones.Notificacion;
import MedioDeNotificacion.TipoDeMedio;

public class MedioWhatsApp extends MedioDeNotificacion {
  private ClienteMensajeria clienteMensajeria;

  public MedioWhatsApp(ClienteMensajeria clienteMensajeria) {
    this.clienteMensajeria = clienteMensajeria;
  }

  public void envioDeNotificacion(Notificacion notificacion) {
    this.clienteMensajeria.enviarMensaje(new Mensaje("whatsapp:" + notificacion.getReceptor().getTelefono().getNumeroWpp(), notificacion.getMensaje()));
  }

  public TipoDeMedio getTipoDeMedio() {
    return TipoDeMedio.WPP;
  }
}
