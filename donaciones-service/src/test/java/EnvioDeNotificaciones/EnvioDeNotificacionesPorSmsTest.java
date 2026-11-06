package EnvioDeNotificaciones;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import MedioDeNotificacion.Mensajeria.Adapter.ClienteMensajeria;
import MedioDeNotificacion.Mensajeria.Adapter.Mensaje;
import MedioDeNotificacion.Mensajeria.SMS.MedioSMS;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import utils.ConLimpiezaDeBaseDeDatos;

public class EnvioDeNotificacionesPorSmsTest implements ConLimpiezaDeBaseDeDatos {
  private ClienteMensajeria clienteMensajeria;
  private NotificacionesRepository notificacionesRepository;
  private MedioSMS medioSMS;
  private DatosDeContacto destinatario;
  private Notificacion notificacion;
  private Mensaje mensajeEnviado;

  @BeforeEach
  void setUp() {
    clienteMensajeria        = mock(ClienteMensajeria.class);
    notificacionesRepository = NotificacionesRepository.getInstance();
    medioSMS                 = new MedioSMS(clienteMensajeria);
    destinatario             = new DatosDeContactoBuilder()
        .conTelefono("1122334455")
        .conMail("dante@gmail.com")
        .build();
    notificacion             = new NotificacionPreferencial("Hola", destinatario);

    medioSMS.envioDeNotificacion(notificacion);
    withTransaction(() -> {
      registrarNotificacion(notificacion);
    });

    ArgumentCaptor<Mensaje> captor = ArgumentCaptor.forClass(Mensaje.class);
    verify(clienteMensajeria).enviarMensaje(captor.capture());
    mensajeEnviado = captor.getValue();
    List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
    assertThat(notificaciones).contains(notificacion);
  }

  @Test
  void seEnviaAlNumeroCorrecto() {
    assertEquals("+541122334455", mensajeEnviado.receptor());
  }

  @Test
  void seUsaElContenidoDeLaNotificacion() {
    assertEquals("Hola", mensajeEnviado.contenido());
  }

  private void registrarNotificacion(Notificacion unaNotificacion) {
    entityManager().persist(unaNotificacion.getReceptor());
    notificacionesRepository.registrar(unaNotificacion);
  }
}
