package BusNotificaciones;

import MedioDeNotificacion.TipoDeMedio;

import MedioDeNotificacion.Correo.MedioCorreo;
import MedioDeNotificacion.MedioDeNotificacion;
import MedioDeNotificacion.Mensajeria.SMS.MedioSMS;
import MedioDeNotificacion.Mensajeria.Wapp.MedioWhatsApp;

public class MedioNotificacionFactory {
  private final MedioCorreo   medioMail;
  private final MedioWhatsApp medioWhatsApp;
  private final MedioSMS      medioSMS;

  public MedioNotificacionFactory(MedioCorreo medioMail,
                                  MedioWhatsApp medioWhatsApp,
                                  MedioSMS medioSMS) {
    this.medioMail = medioMail;
    this.medioWhatsApp = medioWhatsApp;
    this.medioSMS = medioSMS;
  }

  public MedioDeNotificacion construirMedio(TipoDeMedio tipo) {
    return switch (tipo) {
      case MAIL -> this.medioMail;
      case WPP -> this.medioWhatsApp;
      case SMS -> this.medioSMS;
    };
  }
}