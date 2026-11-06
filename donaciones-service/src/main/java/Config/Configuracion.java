package Config;
import utils.Entorno;

public class Configuracion {

  public String getApiKeySendGrid() {
    return Entorno.requerida("SENDGRID_API_KEY");
  }

  public String getEmisorSendGrid() {
    return Entorno.requerida("SENDGRID_EMISOR");
  }

  public String getAccountSidTwilio() { return Entorno.requerida("TWILIO_ACCOUNT_SID");}

  public String getAuthTokenTwilio() { return Entorno.requerida("TWILIO_AUTH_TOKEN");}

  public String getEmisorWhatsAppTwilio() { return Entorno.requerida("TWILIO_EMISOR_WHATSAPP");}

  public String getLogisticaServiceUrl() {
    return Entorno.requerida("URL_PUBLICA_LOGISTICA");
  }

  public String getEmisorSmsTwilio() {
    return Entorno.requerida("TWILIO_EMISOR_SMS");
  }
}