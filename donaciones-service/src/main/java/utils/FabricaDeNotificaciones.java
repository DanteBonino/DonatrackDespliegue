package utils;

import Contacto.DatosDeContacto;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionFija;
import Notificaciones.NotificacionPreferencial;

public class FabricaDeNotificaciones {
  public static NotificacionFija nuevaNotificacionFija(String mensaje, DatosDeContacto datosDeContacto, TipoDeMedio tipoDeMedio) {
    return new NotificacionFija(mensaje, datosDeContacto, tipoDeMedio);
  }

  public static NotificacionFija notificacionViaMail(String mensaje, DatosDeContacto datosDeContacto) {
    return nuevaNotificacionFija(mensaje, datosDeContacto, TipoDeMedio.MAIL);
  }

  public static NotificacionFija notificacionViaWpp(String mensaje, DatosDeContacto datosDeContacto) {
    return nuevaNotificacionFija(mensaje, datosDeContacto, TipoDeMedio.WPP);
  }

  public static NotificacionFija notificacionViaSms(String mensaje, DatosDeContacto datosDeContacto) {
    return nuevaNotificacionFija(mensaje, datosDeContacto, TipoDeMedio.SMS);
  }

  public static Notificacion holaMundoViaMail(DatosDeContacto datosDeContacto) {
    return notificacionViaMail("hola mundo", datosDeContacto);
  }

  public static NotificacionPreferencial nuevaNotificacionPreferencial(String mensaje, DatosDeContacto datosDeContacto) {
    return new NotificacionPreferencial(mensaje, datosDeContacto);
  }

}
