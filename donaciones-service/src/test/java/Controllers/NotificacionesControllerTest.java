package Controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Controllers.Notificaciones.NotificacionResponseDto;
import Donantes.Donante;
import Injector.Injector;
import Notificaciones.Notificacion;
import Repositories.Donantes.DonantesRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Servidor.ServidorDonaciones;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;

import utils.FabricaDeDatos;
import utils.FabricaDeNotificaciones;

@ExtendWith(MockitoExtension.class)
public class NotificacionesControllerTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {
  private Javalin app;
  @Mock
  private Injector inyectorDeDependencias;
  private NotificacionesRepository notificacionesRepository;

  @BeforeEach
  void setUp() {
    notificacionesRepository = NotificacionesRepository.getInstance();
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
  }

  @Nested
  class SinNotificacionesPersistidas {
    @Test
    void pedirTodasDevuelveUnaListaVaciaY200ComoStatusCode() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(leerRespuestas(respuestaHttp)).isEmpty();
      });
    }
  }
  @Nested
  class ConNotificacionesPersisitdas {
    private Notificacion notificacionPersistida;
    private Donante donanteANotificar;
    @BeforeEach
    void setUp() {
      donanteANotificar = FabricaDeDatos.unDonanteDePrueba();
      notificacionPersistida = FabricaDeNotificaciones.holaMundoViaMail(donanteANotificar.getDatosDeContacto());
      withTransaction(() -> {
        DonantesRepository.getInstance().registrar(donanteANotificar);
        notificacionesRepository.registrar(notificacionPersistida);
      });
    }

    @Test
    void pedirTodasDevuelveUnaStatusCode200() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertThat(respuestaHttp.code()).isEqualTo(200);
      });
    }

    @Test
    void pedirTodasSiHayUnaSolaPersistidaDevuelveUnaListaDe1Elemento() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        assertEquals(1, leerRespuestas(respuestaHttp).size());
      });
    }

    @Test
    void pedirTodasSiHayUnaSolaPersistidaDevuelveUnaListaDe1ElementoCuyoNombreCoincideConElDelElementoPersistido() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(pathBase());
        NotificacionResponseDto notificacionEncontrada = leerRespuestas(respuestaHttp).get(0);
        assertEquals(notificacionPersistida.getIdDominio(), notificacionEncontrada.getId());
      });
    }
  }

  private String pathBase() {
    return "/notificaciones";
  }

  private List<NotificacionResponseDto> leerRespuestas(Response respuesta)  throws IOException {
    return leerListaComo(respuesta, NotificacionResponseDto.class);
  }
}
