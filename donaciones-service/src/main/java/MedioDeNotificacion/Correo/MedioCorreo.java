package MedioDeNotificacion.Correo;


import MedioDeNotificacion.Correo.Adapter.ClienteCorreo;
import MedioDeNotificacion.Correo.Adapter.Post;
import MedioDeNotificacion.MedioDeNotificacion;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;

public class MedioCorreo extends MedioDeNotificacion {
  private static final String ASUNTO = "Notificacion Donatrack";
  private ClienteCorreo clienteCorreo;


  public MedioCorreo(ClienteCorreo clienteCorreo) {
    this.clienteCorreo = clienteCorreo;
  }

  public void envioDeNotificacion(Notificacion notificacion) {
    notificacion.getReceptor().getMailsParaNotificar().
        forEach(unMail ->
            this.clienteCorreo.enviarEmail(new Post(unMail, ASUNTO, notificacion.getMensaje())));
  }

  public TipoDeMedio getTipoDeMedio() {
    return TipoDeMedio.MAIL;
  }
}
