package Controllers.Notificaciones;

import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;
import java.util.List;

public class NotificacionResponseDto {
  private TipoDeMedio tipoDeMedio;
  private String mensaje;
  private List<String> mails;
  private String telefono;
  private String id;

  public static NotificacionResponseDto desde(Notificacion notificacion) {
    NotificacionResponseDto respuesta = new NotificacionResponseDto();
    respuesta.setId(notificacion.getIdDominio());
    respuesta.setMails(notificacion.getReceptor().getMailsParaNotificar());
    respuesta.setTelefono(notificacion.getReceptor().getTelefono().getNumero());
    respuesta.setMensaje(notificacion.getMensaje());
    respuesta.setTipoDeMedio(notificacion.getMedioDeNotificacion());
    return respuesta;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public void setMails(List<String> mails) {
    this.mails = mails;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public void setTipoDeMedio(TipoDeMedio tipoDeMedio) {
    this.tipoDeMedio = tipoDeMedio;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TipoDeMedio getTipoDeMedio() {
    return tipoDeMedio;
  }

  public String getMensaje() {
    return mensaje;
  }

  public List<String> getMails() {
    return mails;
  }

  public String getTelefono() {
    return telefono;
  }

  public String getId() {
    return id;
  }

}