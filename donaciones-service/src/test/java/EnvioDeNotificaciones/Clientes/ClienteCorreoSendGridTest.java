package EnvioDeNotificaciones.Clientes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ExceptionHandler.CorreoNoEnviadoException;
import MedioDeNotificacion.Correo.Adapter.Implementaciones.ClienteCorreoSendGrid;
import MedioDeNotificacion.Correo.Adapter.Post;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

class ClienteCorreoSendGridTest {

  private static final String EMISOR = "donatrack@mail.com";

  private SendGrid sendGridMock;
  private ClienteCorreoSendGrid clienteCorreo;
  private Post post;

  @BeforeEach
  void setUp() {
    sendGridMock  = mock(SendGrid.class);
    clienteCorreo = new ClienteCorreoSendGrid(sendGridMock, EMISOR);
    post          = new Post("ana@gmail.com", "Notificacion Donatrack", "Hola");
  }

  @Nested
  class CuandoElEnvioEsExitoso {

    @BeforeEach
    void setUpEnvioExitoso() throws IOException {
      Response response = new Response();
      response.setStatusCode(202);
      when(sendGridMock.api(any(Request.class))).thenReturn(response);
    }

    @Test
    void noLanzaNingunaExcepcion() {
      assertDoesNotThrow(() -> clienteCorreo.enviarEmail(post));
    }

    @Test
    void seLlamaAlApiDeSendGridUnaSolaVez() throws IOException {
      clienteCorreo.enviarEmail(post);
      verify(sendGridMock, times(1)).api(any(Request.class));
    }

  }

  @Nested
  class CuandoLaApiResponde4xxO5xx {

    @BeforeEach
    void setUpEnvioFallido() throws IOException {
      Response response = new Response();
      response.setStatusCode(400);
      when(sendGridMock.api(any(Request.class))).thenReturn(response);
    }

    @Test
    void seLanzaCorreoNoEnviadoException() {
      assertThrows(CorreoNoEnviadoException.class, () -> clienteCorreo.enviarEmail(post));
    }
  }

  @Nested
  class CuandoFallaLaConexion {

    @BeforeEach
    void setUpFalloDeRed() throws IOException {
      when(sendGridMock.api(any(Request.class))).thenThrow(new IOException("Sin conexión"));
    }

    @Test
    void seLanzaCorreoNoEnviadoException() {
      assertThrows(CorreoNoEnviadoException.class, () -> clienteCorreo.enviarEmail(post));
    }
  }
}