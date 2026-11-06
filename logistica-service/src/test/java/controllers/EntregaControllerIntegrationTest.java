package controllers;

import ServiceLocator.ServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.utils.AlmacenamientoDeComprobantes;
import controllers.utils.GeneradorDeUrlDeTracking;
import domain.*;
import domain.eventos.EntregaPublicador;
import dtos.MensajeResponse;
import injector.LogisticaInjector;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.EntregasRepository.EntregasRepository;
import repositories.EntregasRepository.EntregasRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepository;

import java.time.LocalDate;
import repositories.RutasRepository.RutasRepositoryConPersistencia;
import server.ExceptionHandler;
import server.FiltroGlobalDeTransacciones;
import utility.FabricaDeDatos;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Integración: EntregaController (HTTP Real -> Dominio -> Repository)")
public class EntregaControllerIntegrationTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private RutasRepository rutasRepository;
  private FlotaRepository flotaRepository;
  private EntregasRepository entregasRepository;
  @Mock
  private LogisticaInjector injector;
  private LogisticaInjector fakeInjector;
  private Javalin app;

  private Entrega entregaEnCamino;
  private Entrega entregaPlanificada;
  private Entrega entregaEnIncidencia;
  private Entrega entregaCompletada;
  private Entrega entregaSinRuta;

  @TempDir
  Path tempDir;


  @BeforeEach
  void setUp() {
    ServiceLocator.configurar(new EntregaPublicador());
    when(injector.getGeneradorDeUrlDeTracking()).thenReturn(new GeneradorDeUrlDeTracking("Av. Medrano 951, CABA, Argentina", new Posicion(-34.59855526311623, -58.42013582713193)));
    rutasRepository = RutasRepositoryConPersistencia.getInstance();
    flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    entregasRepository = EntregasRepositoryConPersistencia.getInstance();

    withTransaction(() -> {
      // === RUTA 1: Camión en movimiento ===
      Camion camion1 = new Camion("AAA111", 10.0, 3.0,
          5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      Ruta rutaEnCurso = new Ruta( LocalDate.now());
      camion1.asignarRuta(rutaEnCurso);

      entregaEnCamino = FabricaDeDatos.nuevaEntrega("DON-001", "BEN-001");
      rutaEnCurso.agregarEntrega(entregaEnCamino);

      entregaEnIncidencia = FabricaDeDatos.nuevaEntrega( "DON-003", "BEN-003");
      rutaEnCurso.agregarEntrega(entregaEnIncidencia);

      entregaCompletada = FabricaDeDatos.nuevaEntrega( "DON-004", "BEN-004");
      rutaEnCurso.agregarEntrega(entregaCompletada);

      camion1.iniciarRuta();
      entregaEnIncidencia.reportarIncidencia("Motivo original");
      entregaCompletada.confirmarRecepcion("foto_ok.jpg", "AAA111");

      rutasRepository.registrar(rutaEnCurso);
      flotaRepository.registrar(camion1);

      // === RUTA 2: Camión quieto ===
      Camion camion2 = new Camion("BBB222", 12.0, 3.5,
          6000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      Ruta rutaPlanificada = new Ruta( LocalDate.now().plusDays(1));
      camion2.asignarRuta(rutaPlanificada);

      entregaPlanificada = FabricaDeDatos.nuevaEntrega( "DON-002", "BEN-002");
      rutaPlanificada.agregarEntrega(entregaPlanificada);

      rutasRepository.registrar(rutaPlanificada);
      flotaRepository.registrar(camion2);

      entregaSinRuta = FabricaDeDatos.nuevaEntrega("DON-101", "BEN-102");
      entityManager().persist(entregaSinRuta);//No le agrego el método registrar al repository, sólo porque SIEMPRE que se creen las Entregas van a estar de la mano de Rutas
    });

    EntregaController controller = new EntregaController(
        new AlmacenamientoDeComprobantes(tempDir),
        injector.getGeneradorDeUrlDeTracking()
    );

    app = Javalin.create(config -> {
      new FiltroGlobalDeTransacciones().aplicarTransaccion(config);
      ExceptionHandler.registrar(config);
      config.routes.post("/entregas/{nombreEntrega}/reportar-incidencia", controller::reportarIncidencia);
      config.routes.post("/entregas/{nombreEntrega}/reprogramar", controller::reprogramar);
      config.routes.get("/entregas/{nombreEntrega}/tracking", controller::redirigirATracking);
      config.routes.post("/entregas/{nombreEntrega}/confirmar", controller::confirmarRecepcion);
    });
  }

  // =========================================================================
  // POST /entregas/{idEntrega}/reportar-incidencia
  // =========================================================================
  @Nested
  @DisplayName("POST /entregas/{idEntrega}/reportar-incidencia")
  class ReportarIncidencia {

    @Test
    @DisplayName("200 OK: reporta la incidencia cuando la entrega está EN_CAMINO")
    void conEntregaEnCaminoDevuelve200() {
      String jsonBody = """
                { "motivo": "Entidad beneficiaria cerrada" }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/entregas/" + entregaEnCamino.getNombre() + "/reportar-incidencia", jsonBody);
        assertThat(response.code()).isEqualTo(200);

        MensajeResponse msg = leerMensaje(response);
        assertThat(msg.mensaje()).contains("Incidencia reportada");

        entityManager().clear();
        Entrega actualizada = entregasRepository.obtenerPorId(entregaEnCamino.getId());
        assertThat(actualizada.getEstado()).isEqualTo(EstadoEntrega.INCIDENCIA);
        assertThat(actualizada.getMotivoNoRecepcion()).isEqualTo("Entidad beneficiaria cerrada");
      });
    }

    @Test
    @DisplayName("404 Not Found: si el ID de la entrega no existe")
    void entregaInexistenteDevuelve404() {
      String jsonBody = """
                { "motivo": "Cerrado" }
                """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/entregas/999999/reportar-incidencia", jsonBody);
        assertThat(response.code()).isEqualTo(404);
      });
    }

    @Test
    @DisplayName("409 Conflict: si la entrega ya está COMPLETADA")
    void entregaCompletadaDevuelve409() {
      String jsonBody = """
                    { "motivo": "Cerrado" }
                    """;

      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/entregas/" + entregaCompletada.getNombre() + "/reportar-incidencia", jsonBody);
        assertThat(response.code()).isEqualTo(409);
      });
    }
  }

  // =========================================================================
  // POST /entregas/{idEntrega}/reprogramar
  // =========================================================================
  @Nested
  @DisplayName("POST /entregas/{idEntrega}/reprogramar")
  class Reprogramar {

    @Test
    @DisplayName("200 OK: reprograma la entrega y notifica a donaciones-service")
    void conEntregaEnIncidenciaNotificaADonaciones() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/entregas/" + entregaEnIncidencia.getNombre() + "/reprogramar", "");
        assertThat(response.code()).isEqualTo(200);
        MensajeResponse msg = leerMensaje(response);
        assertThat(msg.mensaje()).contains("reprogramada");
        entityManager().clear();
        Entrega actualizada = entregasRepository.obtenerPorId(entregaEnIncidencia.getId());
        assertThat(actualizada.getEstado()).isEqualTo(EstadoEntrega.PLANIFICADA);
        assertThat(actualizada.getMotivoNoRecepcion()).isNull();
      });
    }

    @Test
    @DisplayName("404 Not Found: si el ID de la entrega no existe")
    void entregaInexistenteDevuelve404() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/entregas/999999/reprogramar", "");
        assertThat(response.code()).isEqualTo(404);
      });
    }

    @Test
    @DisplayName("409 Conflict: si la entrega no está en INCIDENCIA (ej. EN_CAMINO)")
    void entregaEnCaminoDevuelve409() {
      JavalinTest.test(app, (server, client) -> {
        var response = client.post("/entregas/" + entregaEnCamino.getNombre() + "/reprogramar", "");
        assertThat(response.code()).isEqualTo(409);
      });
    }
  }

  @Nested
  @DisplayName("GET /entregas/{nombreEntrega}/tracking")
  class RedirigirATracking {
    @Test
    @DisplayName("404 Not Found: si el nombre de la entrega no se relaciona con ninguna Entrega")
    void entregaInexistenteDevuelve404() {

      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/entregas/999999/tracking");
        assertThat(response.code()).isEqualTo(404);
        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).isEqualTo("No existe Entrega con un atributo llamado nombre con el valor de 999999");
      });
    }

    @Test
    @DisplayName("404 Not Found: si la entrega que matcheo con el id de la entrega no matchea con ninguna Ruta")
    void rutaInexistenteDevuelve404() {

      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/entregas/" + entregaSinRuta.getNombre() + "/tracking");
        assertThat(response.code()).isEqualTo(404);
        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).isEqualTo("No existe Ruta asociada a la entrega: " + entregaSinRuta.getId());
      });
    }

    @Test
    @DisplayName("404 Not Found: si la ruta que matcheo con la entrega que matcheo con el id de la donacion no matchea con ningun Camion")
    void camionInexistenteDevuelve404() {
      Ruta rutaSinCamion = new Ruta( LocalDate.now());
      withTransaction(() -> {
        rutaSinCamion.agregarEntrega(entregaSinRuta);
        rutasRepository.registrar(rutaSinCamion);
      });
      JavalinTest.test(app, (server, client) -> {
        var response = client.get("/entregas/"+ entregaSinRuta.getNombre()+ "/tracking");
        assertThat(response.code()).isEqualTo(404);
        MensajeResponse error = leerMensaje(response);
        assertThat(error.mensaje()).isEqualTo("No existe camion asociado a la Ruta: " + rutaSinCamion.getId());
      });
    }

    @Test
    @DisplayName("302 Found: El link de tracking de una Entrega cuyo Camion no se movió del Depósito reedirige al link de Google Maps del deposito")
    void redirigeAlTracking() {
      JavalinTest.test(app, (server, client) -> {
        // 1. Configuramos un cliente que no siga redirecciones
        okhttp3.OkHttpClient clientSinRedirects = new okhttp3.OkHttpClient.Builder()
            .followRedirects(false)
            .build();

        // 2. Armamos la petición usando el puerto dinámico de JavalinTest
        okhttp3.Request request = new okhttp3.Request.Builder()
            .url(client.getOrigin() + "/entregas/" +entregaEnCamino.getNombre() + "/tracking")
            .build();

        // 3. Ejecutamos y validamos
        try (okhttp3.Response response = clientSinRedirects.newCall(request).execute()) {
          assertThat(response.code()).isEqualTo(302); // 302 es el status code de ctx.redirect()

          String locationUrl = response.header("Location");
          assertThat(locationUrl).isEqualTo("https://www.google.com/maps/search/?api=1&query=Av.+Medrano+951%2C+CABA%2C+Argentina");
        }
      });
    }
  }

  @Nested
  @DisplayName("POST /entregas/{idEntrega}/confirmar")
  class ConfirmarEntrega {

    // Método auxiliar para armar requests multipart/form-data reales
    private RequestBody crearRequestBodyConFoto(String nombreForm, String nombreArchivo) {
      return new MultipartBody.Builder()
          .setType(MultipartBody.FORM)
          .addFormDataPart(
              nombreForm,
              nombreArchivo,
              RequestBody.create("contenido-falso-de-imagen".getBytes(), MediaType.parse("image/jpeg"))
          )
          .build();
    }

    @Test
    @DisplayName("200 OK: confirma recepción y guarda el archivo en disco")
    void confirmaRecepcionExitosamente() {
      JavalinTest.test(app, (server, client) -> {
        var requestBody = crearRequestBodyConFoto("foto", "foto_recepcion_ok.jpg");

        okhttp3.Request request = new okhttp3.Request.Builder()
            .url(client.getOrigin() + "/entregas/" + entregaEnCamino.getNombre() + "/confirmar")
            .post(requestBody)
            .build();

        try (okhttp3.Response response = new okhttp3.OkHttpClient().newCall(request).execute()) {
          assertThat(response.code()).isEqualTo(200);

          String json = response.body().string();
          MensajeResponse res = objectMapper.readValue(json, MensajeResponse.class);
          assertThat(res.mensaje()).contains("Recepción confirmada con éxito");

          // Opcional: Verificar que el archivo realmente se guardó en la carpeta temporal
          assertThat(Files.list(tempDir).count()).isEqualTo(1);
        }
      });
    }

    @Test
    @DisplayName("400 Bad Request: si no se envía el campo 'foto' en la request")
    void bodySinArchivoDevuelve400() {
      JavalinTest.test(app, (server, client) -> {
        // Petición multipart pero vacía (sin adjuntos)
        RequestBody requestBodyVacio = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("otro_campo", "valor")
            .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
            .url(client.getOrigin() + "/entregas/" + entregaEnCamino.getNombre() + "/confirmar")
            .post(requestBodyVacio)
            .build();

        try (okhttp3.Response response = new okhttp3.OkHttpClient().newCall(request).execute()) {
          assertThat(response.code()).isEqualTo(400);

          String json = response.body().string();
          MensajeResponse error = objectMapper.readValue(json, MensajeResponse.class);
          assertThat(error.mensaje()).isEqualTo("Debe adjuntar una foto");
        }
      });
    }

    @Test
    @DisplayName("404 Not Found: si el ID de la entrega no existe")
    void entregaInexistenteDevuelve404() {
      JavalinTest.test(app, (server, client) -> {
        var requestBody = crearRequestBodyConFoto("foto", "foto.jpg");

        okhttp3.Request request = new okhttp3.Request.Builder()
            .url(client.getOrigin() + "/entregas/999999/confirmar")
            .post(requestBody)
            .build();

        try (okhttp3.Response response = new okhttp3.OkHttpClient().newCall(request).execute()) {
          assertThat(response.code()).isEqualTo(404);
          MensajeResponse error = objectMapper.readValue(response.body().byteStream(), MensajeResponse.class);
          assertThat(error.mensaje()).isEqualTo("No existe Entrega con un atributo llamado nombre con el valor de 999999");
        }
      });
    }

    @Test
    @DisplayName("409 Conflict: Si la entrega no está en camino")
    void entregaQueNoEstaEnCaminoDevuelve409() {
      JavalinTest.test(app, (server, client) -> {
        var requestBody = crearRequestBodyConFoto("foto", "foto.jpg");

        okhttp3.Request request = new okhttp3.Request.Builder()
            .url(client.getOrigin() + "/entregas/" + entregaEnIncidencia.getNombre() + "/confirmar")
            .post(requestBody)
            .build();

        try (okhttp3.Response response = new okhttp3.OkHttpClient().newCall(request).execute()) {
          assertThat(response.code()).isEqualTo(409);
          MensajeResponse error = objectMapper.readValue(response.body().byteStream(), MensajeResponse.class);
          assertThat(error.mensaje()).isEqualTo("No se puede confirmar una entrega que se encuentra en estado " + entregaEnIncidencia.getEstado() + ".");
        }
      });
    }
  }

  private MensajeResponse leerMensaje(Response respuesta) throws IOException {
    return leerComo(respuesta, MensajeResponse.class);
  }
}