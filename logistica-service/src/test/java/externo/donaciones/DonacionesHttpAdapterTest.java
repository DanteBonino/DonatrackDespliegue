package externo.donaciones;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de DonacionesHttpAdapter (Contratos de Integración)")
class DonacionesHttpAdapterTest {

  private MockWebServer mockWebServer;
  private DonacionesHttpAdapter adapter;
  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    String baseUrl = mockWebServer.url("/").toString();
    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    adapter = new DonacionesHttpAdapter(baseUrl);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  @DisplayName("Notificar inicio envía JSON con idEntidad")
  void notificarInicio_enviaPayloadCorrecto() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    adapter.notificarInicioDeEntrega("ENT-999", "DON-123", "BEN-456");

    RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
    assertNotNull(request, "El adapter NO realizó la llamada HTTP");

    assertEquals("/donaciones/DON-123/en-traslado", request.getPath());
    assertEquals("POST", request.getMethod());

    String jsonRecibido = request.getBody().readUtf8();

    JsonNode body = mapper.readTree(jsonRecibido);
    assertNotNull(body.get("idEntidad"), "El JSON no contiene la clave 'idEntidad'");
    assertEquals("BEN-456", body.get("idEntidad").asText());

    assertNotNull(body.get("idEntrega"), "El JSON no contiene la clave 'idEntrega'");
    assertEquals("ENT-999", body.get("idEntrega").asText());
  }

  @Test
  @DisplayName("Notificar entrega completada envía JSON con idBeneficiario y comprobante anidado")
  void notificarCompletada_enviaPayloadCorrecto() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    adapter.notificarEntregaCompletada("DON-123", "BEN-456", "foto.jpg", "ABC123");

    RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
    assertNotNull(request, "El adapter NO realizó la llamada HTTP");

    assertEquals("/donaciones/DON-123/entregada", request.getPath());
    assertEquals("POST", request.getMethod());

    String jsonRecibido = request.getBody().readUtf8();

    JsonNode body = mapper.readTree(jsonRecibido);
    assertNotNull(body.get("idBeneficiario"), "El JSON no contiene la clave 'idBeneficiario'");
    assertEquals("BEN-456", body.get("idBeneficiario").asText());

    assertTrue(body.has("comprobanteDeEntrega"), "Falta el objeto anidado comprobanteDeEntrega");
    JsonNode comprobante = body.get("comprobanteDeEntrega");
    assertEquals("ABC123", comprobante.get("patenteCamion").asText());
    assertTrue(comprobante.has("timestamp"), "Falta el timestamp en el comprobante");
  }

  @Test
  @DisplayName("Notificar incidencia envía JSON con idBeneficiario y justificacion")
  void notificarIncidencia_enviaPayloadCorrecto() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    adapter.notificarIncidencia("DON-123", "BEN-456", "Entidad cerrada");

    RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
    assertNotNull(request, "El adapter NO realizó la llamada HTTP");

    assertEquals("/donaciones/DON-123/entrega-fallida", request.getPath());
    assertEquals("POST", request.getMethod());

    String jsonRecibido = request.getBody().readUtf8();

    JsonNode body = mapper.readTree(jsonRecibido);
    assertNotNull(body.get("idBeneficiario"), "El JSON no contiene la clave 'idBeneficiario'");
    assertEquals("BEN-456", body.get("idBeneficiario").asText());
    assertEquals("Entidad cerrada", body.get("justificacion").asText());
  }

  @Test
  @DisplayName("Notificar reprogramación envía POST a /en-deposito sin body")
  void notificarReprogramacion_enviaPostCorrecto() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    adapter.notificarReprogramacion("DON-999");
    RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
    assertNotNull(request, "El adapter NO realizó la llamada HTTP");
    assertEquals("/donaciones/DON-999/en-deposito", request.getPath());
    assertEquals("POST", request.getMethod());
    // El body debería estar vacío o ausente
    assertEquals(0, request.getBodySize());
  }

  @Test
  @DisplayName("Notificar reprogramación lanza excepción si donaciones-service responde error")
  void notificarReprogramacion_lanzaExcepcionSiFalla() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Error"));
    assertThrows(RuntimeException.class, () -> adapter.notificarReprogramacion("DON-ERR"));
  }

  // ==========================================================================
  @Nested
  @DisplayName("notificarDonacionesNoPlanificadas")
  class NotificarDonacionesNoPlanificadasTest {

    @Test
    @DisplayName("Envía POST con la lista de IDs de donaciones no planificadas")
    void enviaPostConListaDeIdsNoPlanificadas() throws Exception {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setBody("{\"mensaje\":\"Donaciones marcadas como no planificadas\"}"));

      List<String> idsNoPlanificadas = List.of("DON-101", "DON-102", "DON-103");
      adapter.notificarDonacionesNoPlanificadas(idsNoPlanificadas);

      RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
      assertNotNull(request, "No se realizó ninguna petición a donaciones-service");
      assertEquals("POST", request.getMethod());
      assertTrue(request.getPath().contains("/donaciones/revertir-no-planificadas"));

      // Leer el body UNA SOLA VEZ
      String body = request.getBody().readUtf8();
      JsonNode jsonBody = new ObjectMapper().readTree(body);

      // El adapter usa Map.of("idDonaciones", lista)
      assertTrue(jsonBody.has("idDonaciones"),
          "El body debe contener la clave 'idDonaciones'");

      JsonNode idsArray = jsonBody.get("idDonaciones");
      assertTrue(idsArray.isArray(), "idDonaciones debe ser un array");
      assertEquals(3, idsArray.size(), "Debe contener 3 IDs");

      // Verificar que cada ID está presente
      List<String> idsEnviados = new ObjectMapper().readValue(
          idsArray.toString(),
          new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}
      );
      assertTrue(idsEnviados.containsAll(idsNoPlanificadas),
          "Los IDs enviados no coinciden con los esperados");
    }

    @Test
    @DisplayName("No envía nada si la lista está vacía")
    void noEnviaNadaSiListaVacia() throws Exception {
      adapter.notificarDonacionesNoPlanificadas(List.of());

      RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
      assertNull(request, "No debería haberse enviado ninguna petición con lista vacía");
    }

    @Test
    @DisplayName("No envía nada si la lista es null")
    void noEnviaNadaSiListaNull() throws Exception {
      adapter.notificarDonacionesNoPlanificadas(null);

      RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
      assertNull(request, "No debería haberse enviado ninguna petición con lista null");
    }

    @Test
    @DisplayName("Lanza excepción si donaciones-service responde con error")
    void lanzaExcepcionSiDonacionesServiceRespondeError() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setBody("{\"error\":\"Internal Server Error\"}"));

      List<String> ids = List.of("DON-200");

      assertThrows(RuntimeException.class,
          () -> adapter.notificarDonacionesNoPlanificadas(ids),
          "Debe lanzar RuntimeException ante un error del servicio externo");
    }
  }

  // ==========================================================================
  @Nested
  @DisplayName("notificarInicioDeEntrega - Contrato con idEntrega")
  class NotificarInicioDeEntregaTest {

    @Test
    @DisplayName("Envía POST con la lista de IDs de donaciones no planificadas")
    void enviaPostConListaDeIdsNoPlanificadas() throws Exception {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setBody("{\"mensaje\":\"Donaciones marcadas como no planificadas\"}"));

      List<String> idsNoPlanificadas = List.of("DON-101", "DON-102", "DON-103");
      adapter.notificarDonacionesNoPlanificadas(idsNoPlanificadas);

      RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
      assertNotNull(request, "No se realizó ninguna petición a donaciones-service");
      assertEquals("POST", request.getMethod());
      assertTrue(request.getPath().contains("/donaciones/revertir-no-planificadas"));

      // Leer el body UNA SOLA VEZ
      String body = request.getBody().readUtf8();
      JsonNode jsonBody = new ObjectMapper().readTree(body);

      // El adapter usa Map.of("idDonaciones", lista)
      assertTrue(jsonBody.has("idDonaciones"),
          "El body debe contener la clave 'idDonaciones'");

      JsonNode idsArray = jsonBody.get("idDonaciones");
      assertTrue(idsArray.isArray(), "idDonaciones debe ser un array");
      assertEquals(3, idsArray.size(), "Debe contener 3 IDs");

      // Verificar que cada ID está presente
      List<String> idsEnviados = new ObjectMapper().readValue(
          idsArray.toString(),
          new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}
      );
      assertTrue(idsEnviados.containsAll(idsNoPlanificadas),
          "Los IDs enviados no coinciden con los esperados");
    }

    @Test
    @DisplayName("Lanza excepción si donaciones-service responde 4xx/5xx al iniciar entrega")
    void lanzaExcepcionSiDonacionesServiceRespondeError() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(409)
          .setBody("{\"error\":\"La donación no está en estado AsignacionRealizada\"}"));

      assertThrows(RuntimeException.class,
          () -> adapter.notificarInicioDeEntrega("ENT-002", "DON-600", "BEN-10"),
          "Debe lanzar RuntimeException cuando donaciones-service responde con error");
    }

    @Test
    @DisplayName("El Content-Type del request es application/json")
    void elContentTypeEsApplicationJson() throws Exception {
      mockWebServer.enqueue(new MockResponse().setResponseCode(200));

      adapter.notificarInicioDeEntrega("ENT-003", "DON-700", "BEN-20");

      RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
      assertNotNull(request);

      String contentType = request.getHeader("Content-Type");
      assertNotNull(contentType, "El header Content-Type no debe ser null");
      assertTrue(contentType.contains("application/json"),
          "El Content-Type debe ser application/json, pero fue: " + contentType);
    }
  }
}