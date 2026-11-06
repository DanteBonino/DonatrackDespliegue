package EnvioDeNotificaciones;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Repositories.Notificaciones.NotificacionesRepository;

import MedioDeNotificacion.MedioDeNotificacion;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionPreferencial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.assertThat;


public class MedioDeNotificacionTest {

  static class MedioDeNotificacionTestDouble extends MedioDeNotificacion {
    private final boolean debeFallar;
    private TipoDeMedio tipoDeMedio;

    MedioDeNotificacionTestDouble(boolean debeFallar, TipoDeMedio tipoDeMedio) {
      this.debeFallar = debeFallar;
    }

    @Override
    protected void envioDeNotificacion(Notificacion unaNotificacion) {
      if (debeFallar) {
        throw new RuntimeException("Fallo simulado de envío");
      }
    }

    public TipoDeMedio getTipoDeMedio() {
      return tipoDeMedio;
    }
  }

  private DatosDeContacto receptor;
  private Notificacion notificacion;
  private NotificacionesRepository notificacionesRepository;
  private TipoDeMedio tipoDeMedio;

  @BeforeEach
  void setUp() {
    receptor = new DatosDeContactoBuilder()
        .conMail("dante@gmai.com")
        .conTelefono("1122334455")
        .build();
    notificacion = new NotificacionPreferencial("Cualquier mensaje", receptor);
    notificacionesRepository = NotificacionesRepository.getInstance();
    tipoDeMedio = mock(TipoDeMedio.class);
  }

  @Nested
  class CuandoElEnvioEsExitoso {

    private MedioDeNotificacion medio;

    @BeforeEach
    void setUpEnvioExitoso() {
      medio = new MedioDeNotificacionTestDouble(false, tipoDeMedio);
      medio.enviarNotificacion(notificacion);
      notificacionesRepository.registrar(notificacion);
    }

    @Test
    void laNotificacionYaNoEsEnviable() {
      assertFalse(notificacion.esEnviable(), "Una notificación entregada no debería ser enviable");
    }

//    @Test
//    void seAgregaLaNotificacionAlRepo() {
//      List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
//      assertThat(notificaciones).contains(notificacion);
//    }
  }

  @Nested
  class CuandoElEnvioFalla {

    private MedioDeNotificacion medio;

    @BeforeEach
    void setUpEnvioFallido() {
      medio = new MedioDeNotificacionTestDouble( true, tipoDeMedio);
    }

    @Test
    void conUnRebotelaNotificacionSigueSiendoEnviable() {
      medio.enviarNotificacion(notificacion);
      assertTrue(notificacion.esEnviable(), "Con un solo rebote debería seguir siendo enviable");
    }

    @Test
    void conDosRebotesLaNotificacionSigueSiendoEnviable() {
      medio.enviarNotificacion(notificacion);
      medio.enviarNotificacion(notificacion);
      assertTrue(notificacion.esEnviable(), "Con 2 rebotes todavía debería ser enviable");
    }

    @Test
    void conTresRebotesLaNotificacionDejaDeSerEnviable() {
      medio.enviarNotificacion(notificacion);
      medio.enviarNotificacion(notificacion);
      medio.enviarNotificacion(notificacion);
      assertFalse(notificacion.esEnviable(), "Tras 3 rebotes ya no debería ser enviable");
    }

//    @Test
//    void seAgregaLaNotificacionAlRepoIgual() {
//      medio.enviarNotificacion(notificacion);
//      notificacionesRepository.regristar(notificacion);
//      List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
//      assertThat(notificaciones).contains(notificacion);
//    }
  }
}