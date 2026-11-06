package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Camion;
import domain.Entrega;
import domain.Posicion;
import domain.Ruta;
import dtos.MensajeResponse;
import dtos.MonitoreoRutaDto;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryConPersistencia;

import java.time.LocalDate;
import server.ExceptionHandler;
import server.FiltroGlobalDeTransacciones;
import utility.FabricaDeDatos;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests de Integración: MonitoreoController (HTTP Real -> Dominio -> Repository)")
public class MonitoreoControllerIntegrationTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {

  private FlotaRepository flotaRepository;
  private RutasRepository rutasRepository;
  private Javalin app;

  private Camion camionEnCurso;
  private Camion camionPlanificado;
  private Camion camionDisponible;
  private Ruta rutaEnCurso;

  @BeforeEach
  void setUp() {
    flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    rutasRepository = RutasRepositoryConPersistencia.getInstance();

    // 1. Persistir datos forzando el COMMIT a la BD
    withTransaction(() -> {

      // ESCENARIO 1: Camión con ruta EN_CURSO
      camionEnCurso = new Camion("CCC333", 15.0, 4.0,
          8000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      rutaEnCurso = new Ruta(LocalDate.now());
      Entrega entrega1 = FabricaDeDatos.nuevaEntrega( "DON-301", "ENT-BEN-01");
      Entrega entrega2 = FabricaDeDatos.nuevaEntrega("DON-302", "ENT-BEN-02");
      rutaEnCurso.agregarEntrega(entrega1);
      rutaEnCurso.agregarEntrega(entrega2);
      camionEnCurso.asignarRuta(rutaEnCurso);
      camionEnCurso.iniciarRuta();
      camionEnCurso.actualizarPosicion(new Posicion(-34.6, -58.4));

      rutasRepository.registrar(rutaEnCurso);
      flotaRepository.registrar(camionEnCurso);

      // ESCENARIO 2: Camión con ruta PLANIFICADA
      camionPlanificado = new Camion("BBB222", 12.0, 3.5,
          6000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      Ruta rutaPlanificada = new Ruta( LocalDate.now().plusDays(1));
      Entrega entrega3 = FabricaDeDatos.nuevaEntrega( "DON-201", "ENT-BEN-03");
      rutaPlanificada.agregarEntrega(entrega3);
      camionPlanificado.asignarRuta(rutaPlanificada);

      rutasRepository.registrar(rutaPlanificada);
      flotaRepository.registrar(camionPlanificado);

      // ESCENARIO 3: Camión DISPONIBLE
      camionDisponible = new Camion("AAA111", 10.0, 3.0,
          5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      flotaRepository.registrar(camionDisponible);

      return null;
    });

    // 2. Crear servidor de prueba registrando Transacciones y ExceptionHandler
    MonitoreoController controller = new MonitoreoController();
    app = Javalin.create(config -> {
      new FiltroGlobalDeTransacciones().aplicarTransaccion(config);
      ExceptionHandler.registrar(config);
      config.routes.get("/rutas/activas", controller::obtenerRutasActivas);
    });

  }

  // =========================================================================
  // GET /rutas/activas
  // =========================================================================
  @Nested
  @DisplayName("GET /rutas/activas")
  class ObtenerRutasActivas {

    @Test
    @DisplayName("200 OK: devuelve solo las rutas en estado EN_CURSO con su estructura DTO")
    void devuelveSoloRutasEnCurso() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/rutas/activas");

        assertThat(response.code()).isEqualTo(200);

        List<MonitoreoRutaDto> respuestas = leerListaComo(response, MonitoreoRutaDto.class);

        assertThat(respuestas).hasSize(1);

        MonitoreoRutaDto dto = respuestas.get(0);
        assertThat(dto.nombreRuta()).isEqualTo(rutaEnCurso.getNombre());
        assertThat(dto.patenteCamion()).isEqualTo("CCC333");
        assertThat(dto.estadoRuta()).isEqualTo("EN_CURSO");
        assertThat(dto.latitud()).isEqualTo(-34.6);
        assertThat(dto.longitud()).isEqualTo(-58.4);
        assertThat(dto.totalEntregas()).isEqualTo(2);
        assertThat(dto.entregasCompletadas()).isEqualTo(0);
      });
    }

    @Test
    @DisplayName("200 OK: devuelve lista vacía si no hay rutas en curso")
    void devuelveListaVaciaSiNoHayRutasEnCurso() {
      // Limpiamos los datos del setUp() para simular base vacía
      withTransaction(MonitoreoControllerIntegrationTest.this::limpiarBaseDeDatos);

      MonitoreoController controllerVacio = new MonitoreoController();
      Javalin appVacia = Javalin.create(config -> {
        new FiltroGlobalDeTransacciones().aplicarTransaccion(config);
        ExceptionHandler.registrar(config);
        config.routes.get("/rutas/activas", controllerVacio::obtenerRutasActivas);
      });

      JavalinTest.test(appVacia, (server, client) -> {
        var response = client.get("/rutas/activas");

        assertThat(response.code()).isEqualTo(200);
        List<MonitoreoRutaDto> respuestas = leerListaComo(response, MonitoreoRutaDto.class);
        assertThat(respuestas.size()).isEqualTo(0);
      });
    }
  }
}