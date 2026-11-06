package controllers;

import ServiceLocator.ServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.*;
import domain.eventos.EntregaPublicador;
import dtos.CamionDashboardDto;
import dtos.CamionResponseDto;
import dtos.MensajeResponse;
import dtos.MonitoreoRutaDto;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;

import java.time.LocalDate;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryConPersistencia;
import server.ExceptionHandler;
import server.FiltroGlobalDeTransacciones;
import utility.FabricaDeDatos;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests de Integración: CamionController (HTTP Real -> Dominio -> Repository)")
public class CamionControllerIntegrationTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {

  // Instancia reutilizable de Jackson para deserializar los JSONs de respuesta
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private FlotaRepository flotaRepository;
  private RutasRepository rutasRepository;
  private Javalin app;

  // Camiones pre-cargados para los distintos escenarios
  private Camion camionDisponible;        // Sin ruta -> 409 en POST /posicion
  private Camion camionConRutaEnCurso;    // Con ruta activa -> 200 en POST /posicion
  private Camion camionConRutaPlanificada;// Ruta PLANIFICADA -> 200 en POST /iniciar-ruta
  private Camion camionConRutaVacia;      // Ruta sin entregas -> 400 en POST /iniciar-ruta
  private Camion camionConRutaYaIniciada; // Ruta EN_CURSO -> 400 en POST /iniciar-ruta
  private Ruta rutaEnCurso;

  @BeforeEach
  void setUp() {
    ServiceLocator.configurar(new EntregaPublicador());
    flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    rutasRepository = RutasRepositoryConPersistencia.getInstance();

    withTransaction(() -> {
      // === ESCENARIO 1: Camión DISPONIBLE (sin ruta) ===
      camionDisponible = new Camion("AAA111", 10.0, 3.0,
          5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      flotaRepository.registrar(camionDisponible);

      // === ESCENARIO 2: Camión con ruta EN_CURSO (acepta actualizaciones de posición) ===
      camionConRutaEnCurso = new Camion("BBB222", 12.0, 3.5,
          6000.0, new Posicion(-34.59855526311623, -58.42013582713193));

      rutaEnCurso = new Ruta( LocalDate.now());
      rutaEnCurso.agregarEntrega(FabricaDeDatos.nuevaEntrega("DON-1", "BEN-1"));
      rutasRepository.registrar(rutaEnCurso);
      camionConRutaEnCurso.asignarRuta(rutaEnCurso);
      camionConRutaEnCurso.iniciarRuta(); // Pasa a EN_CURSO y libera al camión para recibir posiciones
      flotaRepository.registrar(camionConRutaEnCurso);


      // === ESCENARIO 3: Camión con ruta PLANIFICADA (puede iniciar ruta) ===
      camionConRutaPlanificada = new Camion("CCC333", 15.0, 4.0,
          8000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      Ruta rutaPlanificada = new Ruta( LocalDate.now());
      rutaPlanificada.agregarEntrega(FabricaDeDatos.nuevaEntrega("DON-2", "BEN-2"));
      rutasRepository.registrar(rutaPlanificada);
      camionConRutaPlanificada.asignarRuta(rutaPlanificada);
      flotaRepository.registrar(camionConRutaPlanificada);

      // === ESCENARIO 4: Camión con ruta PLANIFICADA pero SIN entregas (400 al iniciar) ===
      camionConRutaVacia = new Camion("DDD444", 10.0, 3.0,
          5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      Ruta rutaVacia = new Ruta( LocalDate.now());
      rutasRepository.registrar(rutaVacia);
      camionConRutaVacia.asignarRuta(rutaVacia);
      flotaRepository.registrar(camionConRutaVacia);

      // === ESCENARIO 5: Camión con ruta YA EN_CURSO (400 al intentar iniciar de nuevo) ===
      camionConRutaYaIniciada = new Camion("EEE555", 10.0, 3.0,
          5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      Ruta rutaYaIniciada = new Ruta( LocalDate.now());
      rutaYaIniciada.agregarEntrega(FabricaDeDatos.nuevaEntrega( "DON-3", "BEN-3"));
      rutasRepository.registrar(rutaYaIniciada);
      camionConRutaYaIniciada.asignarRuta(rutaYaIniciada);
      camionConRutaYaIniciada.iniciarRuta();
      flotaRepository.registrar(camionConRutaYaIniciada);
    });

    // Levantamos el Javalin real con los 3 endpoints del CamionController
    CamionController controller = new CamionController();
    app = Javalin.create(config -> {
      new FiltroGlobalDeTransacciones().aplicarTransaccion(config);
      ExceptionHandler.registrar(config);
      config.routes.get("/camiones", controller::obtenerTodosLosCamiones);
      config.routes.get("/camiones/{patente}", controller::obtenerEstadoCamion);
      config.routes.post("/camiones/{patente}/posicion", controller::actualizarPosicion);
      config.routes.post("/camiones/{patente}/iniciar-ruta", controller::iniciarRuta);
      config.routes.get("/camiones/{patente}/posicion", controller::obtenerPosicionCamion);
    });
  }

  // =========================================================================
  // GET /camiones/{patente}
  // =========================================================================
  @Nested
  @DisplayName("GET /camiones/{patente}")
  class ObtenerEstadoCamion {

    @Test
    @DisplayName("200 OK: devuelve el DTO con estado DISPONIBLE si el camión no tiene ruta")
    void camionDisponibleDevuelve200ConEstadoDisponible() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/camiones/AAA111");

        assertThat(response.code()).isEqualTo(200);
        CamionResponseDto dto = leerCamion(response);

        assertThat(dto.patente()).isEqualTo("AAA111");
        assertThat(dto.estadoRuta()).isEqualTo("DISPONIBLE");
        assertThat(dto.nombreRutaAsignada()).isNull();
      });
    }

    @Test
    @DisplayName("200 OK: devuelve el DTO con estado EN_CURSO e idRuta si el camión tiene ruta activa")
    void camionConRutaActivaDevuelve200ConEstadoEnCurso() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/camiones/BBB222");

        assertThat(response.code()).isEqualTo(200);

        CamionResponseDto dto = leerCamion(response);

        assertThat(dto.patente()).isEqualTo("BBB222");
        assertThat(dto.estadoRuta()).isEqualTo("EN_CURSO");
        assertThat(dto.nombreRutaAsignada()).isEqualTo(rutaEnCurso.getNombre());
      });
    }

    @Test
    @DisplayName("404 Not Found: si la patente no existe en la flota")
    void patenteInexistenteDevuelve404() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/camiones/ZZZ999");

        assertThat(response.code()).isEqualTo(404);

        MensajeResponse error = leerMensaje(response);

        assertEquals("No existe Camion con un atributo llamado patente con el valor de ZZZ999", error.mensaje());
      });
    }
  }

  // =========================================================================
  // POST /camiones/{patente}/posicion
  // =========================================================================
  @Nested
  @DisplayName("POST /camiones/{patente}/posicion")
  class ActualizarPosicion {

    @Test
    @DisplayName("200 OK: actualiza la posición cuando el camión tiene ruta activa")
    void conRutaActivaYCoordenadasValidasDevuelve200() {
      String jsonPosicion = """
                { "latitud": -34.6, "longitud": -58.4 }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/BBB222/posicion", jsonPosicion);

        assertThat(response.code()).isEqualTo(200);

        // Deserialización con Jackson
        MensajeResponse msg = leerMensaje(response);

        assertThat(msg.mensaje()).contains("actualizada");

        Camion actualizado = flotaRepository.buscarPorPatente("BBB222");
        entityManager().refresh(actualizado);
        // Verificamos que el dominio realmente se actualizó
        assertThat(actualizado.getPosicionActual().getLatitud()).isEqualTo(-34.6);
        assertThat(actualizado.getPosicionActual().getLongitud()).isEqualTo(-58.4);
      });
    }

    @Test
    @DisplayName("404 Not Found: si la patente no existe")
    void patenteInexistenteDevuelve404() {
      String jsonPosicion = """
                { "latitud": -34.6, "longitud": -58.4, "velocidad": 45.0 }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/ZZZ999/posicion", jsonPosicion);

        assertThat(response.code()).isEqualTo(404);
        MensajeResponse error = leerMensaje(response);
        assertEquals("No existe Camion con un atributo llamado patente con el valor de ZZZ999", error.mensaje());
      });
    }

    @Test
    @DisplayName("409 Conflict: si el camión no tiene ruta activa (CamionSinRutaActivaException)")
    void camionSinRutaActivaDevuelve409() {
      String jsonPosicion = """
                { "latitud": -34.6, "longitud": -58.4, "velocidad": 45.0 }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/AAA111/posicion", jsonPosicion);

        assertThat(response.code()).isEqualTo(409);

        MensajeResponse error = leerMensaje(response);

        assertEquals("El camion cuya patente es: " + camionDisponible.getPatente() + " no tiene una ruta asignada en la fecha de hoy, por ende no puede actualizarse su posición ni iniciar su ruta.", error.mensaje());
      });
    }

    @Test
    @DisplayName("400 Bad Request: si la latitud está fuera de rango (IllegalArgumentException)")
    void latitudInvalidaDevuelve400() {
      String jsonInvalido = """
                { "latitud": 999.0, "longitud": -58.4, "velocidad": 45.0 }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/BBB222/posicion", jsonInvalido);

        assertThat(response.code()).isEqualTo(400);
        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).containsIgnoringCase("latitud");
      });
    }

    @Test
    @DisplayName("400 Bad Request: si la longitud está fuera de rango")
    void longitudInvalidaDevuelve400() {
      String jsonInvalido = """
                { "latitud": -34.6, "longitud": -999.0, "velocidad": 45.0 }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/BBB222/posicion", jsonInvalido);

        assertThat(response.code()).isEqualTo(400);
      });
    }
  }

  // =========================================================================
  // POST /camiones/{patente}/iniciar-ruta
  // =========================================================================
  @Nested
  @DisplayName("POST /camiones/{patente}/iniciar-ruta")
  class IniciarRuta {

    @Test
    @DisplayName("200 OK: inicia la ruta cuando el camión tiene ruta PLANIFICADA con entregas")
    void conRutaPlanificadaYEntregasDevuelve200() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/CCC333/iniciar-ruta", "");

        assertThat(response.code()).isEqualTo(200);
        MensajeResponse msg = leerMensaje(response);
        assertThat(msg.mensaje()).contains("iniciada");

        entityManager().clear();
        Camion actualizado = flotaRepository.buscarPorPatente("CCC333");
        assertThat(actualizado.getEstadoRutaAsignada()).isEqualTo(EstadoRuta.EN_CURSO);
      });
    }

    @Test
    @DisplayName("404 Not Found: si la patente no existe")
    void patenteInexistenteDevuelve404() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/ZZZ999/iniciar-ruta", "");

        assertThat(response.code()).isEqualTo(404);
      });
    }

    @Test
    @DisplayName("409 Conflict: si el camión no tiene ruta asignada (CamionSinRutaActivaException)")
    void camionSinRutaAsignadaDevuelve409() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/AAA111/iniciar-ruta", "");

        assertThat(response.code()).isEqualTo(409);

        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).containsIgnoringCase("ruta");
      });
    }

    @Test
    @DisplayName("400 Bad Request: si la ruta no tiene entregas (RutaSinEntregasException)")
    void rutaSinEntregasDevuelve400() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/DDD444/iniciar-ruta", "");

        assertThat(response.code()).isEqualTo(400);
        MensajeResponse error = leerMensaje(response);

        assertThat(error.mensaje()).containsIgnoringCase("entregas");
      });
    }

    @Test
    @DisplayName("400 Bad Request: si la ruta ya está EN_CURSO (EstadoRutaInvalidoException)")
    void rutaYaIniciadaDevuelve400() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/camiones/EEE555/iniciar-ruta", "");

        assertThat(response.code()).isEqualTo(400);
        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).containsIgnoringCase("PLANIFICADA");
      });
    }
  }

  // =========================================================================
  // GET /camiones (para el dashboard)
  // =========================================================================
  @Nested
  @DisplayName("GET /camiones")
  class ObtenerTodosLosCamiones {
    @Test
    @DisplayName("200 OK: devuelve la lista de todos los camiones con posición y estado de ruta")
    void devuelveListaDeCamiones() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/camiones");
        assertThat(response.code()).isEqualTo(200);

        List<CamionDashboardDto> dtos = leerListaComo(response, CamionDashboardDto.class);

        // En el setUp de ESTE test cargamos 5 camiones (AAA111, BBB222, CCC333, DDD444, EEE555)
        assertThat(dtos).hasSize(5);

        // Verificamos el camión que SABEMOS que está EN_CURSO según el setUp (BBB222)
        CamionDashboardDto camionEnCurso = dtos.stream()
            .filter(c -> c.patente().equals("BBB222"))
            .findFirst().orElseThrow();

        assertThat(camionEnCurso.estadoRuta()).isEqualTo("EN_CURSO");
        assertThat(camionEnCurso.nombreRutaAsignada()).isEqualTo(rutaEnCurso.getNombre());
        assertThat(camionEnCurso.latitud()).isEqualTo(-34.59855526311623); // Tiene posición mock
        assertThat(camionEnCurso.totalEntregas()).isEqualTo(1); // El setUp de este test carga 1 sola entrega para BBB222
        assertThat(camionEnCurso.entregasCompletadas()).isEqualTo(0);

        // Verificamos también uno que está PLANIFICADA (CCC333)
        CamionDashboardDto camionPlanificado = dtos.stream()
            .filter(c -> c.patente().equals("CCC333"))
            .findFirst().orElseThrow();

        assertThat(camionPlanificado.estadoRuta()).isEqualTo("PLANIFICADA");

        // Y uno que está DISPONIBLE (AAA111)
        CamionDashboardDto camionDisponible = dtos.stream()
            .filter(c -> c.patente().equals("AAA111"))
            .findFirst().orElseThrow();

        assertThat(camionDisponible.estadoRuta()).isEqualTo("DISPONIBLE");
        assertThat(camionDisponible.nombreRutaAsignada()).isNull();
      });
    }
  }
  // =========================================================================
  // GET /rutas/{patente}/posicion
  // =========================================================================
  @Nested
  @DisplayName("GET /camiones/{patente}/posicion")
  class ObtenerPosicionCamion {

    @Test
    @DisplayName("200 OK: devuelve el DTO con la posición actual si el camión tiene ruta activa")
    void devuelvePosicionSiRutaActiva() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/camiones/BBB222/posicion");

        assertThat(response.code()).isEqualTo(200);

        MonitoreoRutaDto dto = leerComo(response, MonitoreoRutaDto.class);
        assertThat(dto.nombreRuta()).isEqualTo(rutaEnCurso.getNombre());
        assertThat(dto.patenteCamion()).isEqualTo("BBB222");
        assertThat(dto.latitud()).isEqualTo(-34.59855526311623);
        assertThat(dto.longitud()).isEqualTo(-58.42013582713193);
        assertThat(dto.estadoRuta()).isEqualTo("EN_CURSO");
      });
    }

    @Test
    @DisplayName("404 Not Found: si la patente no existe en el sistema")
    void devuelve404SiPatenteNoExiste() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/camiones/ZZZ999/posicion");

        assertThat(response.code()).isEqualTo(404);
        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).contains("No existe Camion con un atributo llamado patente con el valor de ZZZ999");
      });
    }

    @Test
    @DisplayName("404 Not Found: si la patente existe pero su ruta está PLANIFICADA (no activa)")
    void devuelve404SiRutaNoEstaActiva() {
      JavalinTest.test(app, (server, client) -> {
        // BBB222 existe y tiene ruta, pero está PLANIFICADA, no EN_CURSO
        var response = client.get("/camiones/CCC333/posicion");

        assertThat(response.code()).isEqualTo(404);

        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).contains("El Camion, cuya patente es " + camionConRutaPlanificada.getPatente() + ", no se encuentra recorriendo una Ruta actualmente");
      });
    }
  }

  private CamionResponseDto leerCamion(Response respuesta) throws IOException {
    return leerComo(respuesta, CamionResponseDto.class);
  }

  private MensajeResponse leerMensaje(Response respuesta) throws IOException {
    return leerComo(respuesta, MensajeResponse.class);
  }

  private List<CamionResponseDto> leerCamiones(Response respuesta) throws IOException {
    return leerListaComo(respuesta, CamionResponseDto.class);
  }
}