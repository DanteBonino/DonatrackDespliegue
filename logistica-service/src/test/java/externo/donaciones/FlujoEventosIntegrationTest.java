package externo.donaciones;

import domain.Entrega;
import domain.eventos.EntregaPublicador;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static utility.FabricaDeDatos.unaDireccionCualquiera;

@DisplayName("Tests de Integración: Flujo de Eventos -> HTTP Adapter")
class FlujoEventosIntegrationTest {

  private MockWebServer mockWebServer;
  private DonacionesHttpAdapter adapter;
  private EntregaPublicador publicador;
  private SincronizadorDeEstadosObserver observer;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    String baseUrl = mockWebServer.url("/").toString();
    // Remover la trailing slash
    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

    adapter = new DonacionesHttpAdapter(baseUrl);
    observer = new SincronizadorDeEstadosObserver(adapter);
    publicador = new EntregaPublicador();
    publicador.suscribir(observer);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
    publicador.desuscribirTodos();
  }

  @Test
  @DisplayName("Al iniciar una entrega, se envía POST a /en-traslado")
  void alIniciarEntregaSeNotificaEnTraslado() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    Entrega entrega = new Entrega( "DON-123", "BEN-1", unaDireccionCualquiera(), publicador);
    entrega.iniciarViaje();
    RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
    assertNotNull(request, "El adapter NO realizó la llamada HTTP");
    assertEquals("POST", request.getMethod());
    assertTrue(request.getPath().contains("/donaciones/DON-123/en-traslado"));
  }

  @Test
  @DisplayName("Al confirmar recepción, se envía POST a /entregada")
  void alConfirmarRecepcionSeNotificaEntregada() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    Entrega entrega = new Entrega( "DON-456", "BEN-2", unaDireccionCualquiera(),publicador);
    entrega.iniciarViaje();
    entrega.confirmarRecepcion("foto_comprobante.jpg", "PAT_INTEGRATION");
    mockWebServer.takeRequest(5, TimeUnit.SECONDS); // iniciarViaje
    RecordedRequest req2 = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
    assertNotNull(req2, "No se notificó la confirmación de recepción");
    assertEquals("POST", req2.getMethod());
    assertTrue(req2.getPath().contains("/donaciones/DON-456/entregada"));
  }

  @Test
  @DisplayName("Al reportar incidencia, se envía POST a /entrega-fallida con justificacion")
  void alReportarIncidenciaSeNotificaEntregaFallida() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    Entrega entrega = new Entrega( "DON-789", "BEN-3", unaDireccionCualquiera(),publicador);
    entrega.iniciarViaje();
    entrega.reportarIncidencia("Entidad cerrada");
    mockWebServer.takeRequest(5, TimeUnit.SECONDS); // iniciarViaje
    RecordedRequest reqIncidencia = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
    assertNotNull(reqIncidencia, "No se notificó la incidencia");
    assertEquals("POST", reqIncidencia.getMethod());
    assertTrue(reqIncidencia.getPath().contains("/donaciones/DON-789/entrega-fallida"));
    String body = reqIncidencia.getBody().readUtf8();
    assertTrue(body.contains("Entidad cerrada"));
  }

  @Test
  @DisplayName("Al reprogramar una entrega con incidencia, se envía POST a /en-deposito")
  void alReprogramarEntregaSeNotificaEnDeposito() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200)); // iniciarViaje
    mockWebServer.enqueue(new MockResponse().setResponseCode(200)); // reportarIncidencia
    mockWebServer.enqueue(new MockResponse().setResponseCode(200)); // reprogramar -> /en-deposito

    Entrega entrega = new Entrega("DON-999", "BEN-4", unaDireccionCualquiera(),publicador);

    entrega.iniciarViaje();
    entrega.reportarIncidencia("Entidad cerrada");
    entrega.reprogramar();

    mockWebServer.takeRequest(5, TimeUnit.SECONDS); // iniciarViaje
    mockWebServer.takeRequest(5, TimeUnit.SECONDS); // incidencia
    RecordedRequest reqReprogramar = mockWebServer.takeRequest(5, TimeUnit.SECONDS);

    assertNotNull(reqReprogramar, "No se notificó la reprogramación");
    assertEquals("POST", reqReprogramar.getMethod());
    assertTrue(reqReprogramar.getPath().contains("/donaciones/DON-999/en-deposito"));
  }
}