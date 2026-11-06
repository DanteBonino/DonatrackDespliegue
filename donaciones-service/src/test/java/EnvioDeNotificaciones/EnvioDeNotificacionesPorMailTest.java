package EnvioDeNotificaciones;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import MedioDeNotificacion.Correo.Adapter.ClienteCorreo;
import MedioDeNotificacion.Correo.Adapter.Post;
import MedioDeNotificacion.Correo.MedioCorreo;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Envio de Notificaciones por Mail")
public class EnvioDeNotificacionesPorMailTest implements ConLimpiezaDeBaseDeDatos {
    private ClienteCorreo clienteCorreo;
    private NotificacionesRepository notificacionesRepository;
    private MedioCorreo medioCorreo;
    private DatosDeContacto destinatario;
    private Notificacion notificacion;
    private Notificacion notificacionSegundoTest;
 
    @BeforeEach
    void setUp() {
      clienteCorreo            = mock(ClienteCorreo.class);
      notificacionesRepository = NotificacionesRepository.getInstance();
      medioCorreo              = new MedioCorreo(clienteCorreo);
      destinatario             = new DatosDeContactoBuilder()
          .conMail("ana@gmail.com")
          .conTelefono("1122334455")
          .build();
      notificacion             = new NotificacionPreferencial("Hola", destinatario);
      notificacionSegundoTest  = new NotificacionPreferencial("Hola", new DatosDeContactoBuilder()
          .conMails(List.of("a@mail.com", "b@mail.com"))
          .conTelefono("1122334455")
          .build());
    }
    private void registrarNotificacion(Notificacion unaNotificacion) {
      entityManager().persist(unaNotificacion.getReceptor());
      notificacionesRepository.registrar(unaNotificacion);
    }

    @Nested
    class CuandoSeEnviaUnaNotificacionPorMailAUnÚnicoNotificable {

      private Post postEnviado;

      @BeforeEach
      void setUpEnvio() {
        medioCorreo.envioDeNotificacion(notificacion);
        withTransaction(() -> {
          registrarNotificacion(notificacion);
        });

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);//Esto era porque no servía crear un Post con los mismo valores que el que se iba a generar
        verify(clienteCorreo).enviarEmail(captor.capture());
        postEnviado = captor.getValue();
        List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
        assertThat(notificaciones.stream().anyMatch(notificacionPersisitda -> notificacionPersisitda.esLaMismaEntidad(notificacion.getIdDominio())));
      }

      @Test
      void seEnviaAlDestinatarioCorrecto() {
        assertEquals("ana@gmail.com", postEnviado.receptor());
      }

      @Test
      void seUsaElAsuntoCorrecto() {
        assertEquals("Notificacion Donatrack", postEnviado.asunto());
      }

      @Test
      void seUsaElContenidoDeLaNotificacion() {
        assertEquals("Hola", postEnviado.contenido());
      }
    }

    @Test
    void enviaUnEmailPorCadaMailDelDestinatario() {
      medioCorreo.envioDeNotificacion(notificacionSegundoTest);
      withTransaction(() -> {
        registrarNotificacion(notificacionSegundoTest);
      });
      verify(clienteCorreo, times(2)).enviarEmail(any(Post.class));
      List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
      assertThat(notificaciones.stream().anyMatch(notificacionPersisitda -> notificacionPersisitda.esLaMismaEntidad(notificacionSegundoTest.getIdDominio())));
    }


}
