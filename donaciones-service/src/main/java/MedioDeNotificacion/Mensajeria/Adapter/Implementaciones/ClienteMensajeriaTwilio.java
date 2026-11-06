package MedioDeNotificacion.Mensajeria.Adapter.Implementaciones;

import ExceptionHandler.MensajeNoEnviadoException;
import MedioDeNotificacion.Mensajeria.Adapter.ClienteMensajeria;
import MedioDeNotificacion.Mensajeria.Adapter.Mensaje;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


public class ClienteMensajeriaTwilio implements ClienteMensajeria {
  private String emisor;

  public ClienteMensajeriaTwilio(String authToken, String accountSid, String emisor) {
    this.emisor = emisor;
    Twilio.init(accountSid, authToken);
  }

  @Override
  public void enviarMensaje(Mensaje mensaje) {
    try {
      Message.creator(
          new PhoneNumber(mensaje.receptor()),
          new PhoneNumber(this.emisor),
          mensaje.contenido()
      ).create();
    } catch (ApiException e) {
      throw new MensajeNoEnviadoException(e);
    }
  }


}
