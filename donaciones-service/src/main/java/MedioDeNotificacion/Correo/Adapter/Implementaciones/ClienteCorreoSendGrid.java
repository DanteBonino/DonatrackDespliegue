package MedioDeNotificacion.Correo.Adapter.Implementaciones;


import ExceptionHandler.CorreoNoEnviadoException;
import MedioDeNotificacion.Correo.Adapter.ClienteCorreo;
import MedioDeNotificacion.Correo.Adapter.Post;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;

public class ClienteCorreoSendGrid implements ClienteCorreo {
  private final SendGrid sendGrid;
  private final String emisor;

  public ClienteCorreoSendGrid(String apiKey, String emisor) {
    this(new SendGrid(apiKey), emisor);
  }

  public ClienteCorreoSendGrid(SendGrid sendGrid, String emisor) {
    this.sendGrid = sendGrid;
    this.emisor = emisor;
  }

  @Override
  public void enviarEmail(Post post) {
    try {
      Mail mail = construirMail(post);
      Response response = enviarRequest(mail);
      validarRespuesta(response);
    } catch (IOException e) {
      throw new CorreoNoEnviadoException(e);
    }
  }

  private Mail construirMail(Post post) {
    Email from = new Email(this.emisor);
    Email to = new Email(post.receptor());
    Content content = new Content("text/plain", post.contenido());
    return new Mail(from, post.asunto(), to, content);
  }

  private Response enviarRequest(Mail mail) throws IOException {
    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());
    return sendGrid.api(request);
  }

  private void validarRespuesta(Response response) {
    if (response.getStatusCode() >= 400) {
      throw new CorreoNoEnviadoException(response.getStatusCode());
    }
  }
}
