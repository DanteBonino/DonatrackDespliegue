package externo.donaciones;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.DonacionDto;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DonacionesHttpAdapter implements DonacionesServiceAdapter {
  private final String baseUrl;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public DonacionesHttpAdapter(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
    this.objectMapper.findAndRegisterModules();
  }

  public DonacionesHttpAdapter(String baseUrl, HttpClient httpClient) {
    this.baseUrl = baseUrl;
    this.httpClient = httpClient;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.findAndRegisterModules();
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public void notificarInicioDeEntrega(String idEntrega, String idDonacion, String idEntidadBeneficiaria) {
    // POST /donaciones/{id}/en-traslado (sin body)
    Map<String, String> body = Map.of(
        "idEntidad", idEntidadBeneficiaria,
        "idEntrega", idEntrega);
    postEstado(idDonacion, "/en-traslado", body);
  }

  @Override
  public void notificarEntregaCompletada(String idDonacion, String idEntidadBeneficiaria, String comprobanteFoto, String patenteCamion) {
    // POST /donaciones/{id}/entregada (sin body, el comprobante se guarda en logística)
    Map<String, Object> comprobante = Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "patenteCamion", patenteCamion
    );
    Map<String, Object> body = Map.of(
        "idBeneficiario", idEntidadBeneficiaria,
        "comprobanteDeEntrega", comprobante
    );
    postEstado(idDonacion, "/entregada", body);
  }

  @Override
  public void notificarIncidencia(String idDonacion, String idEntidadBeneficiaria, String motivoNoRecepcion) {
    // POST /donaciones/{id}/entrega-fallida con body {"justificacion": "..."}
    Map<String, String> body = Map.of(
        "idBeneficiario", idEntidadBeneficiaria,
        "justificacion", motivoNoRecepcion != null ? motivoNoRecepcion : "");
    postEstado(idDonacion, "/entrega-fallida", body);
  }

  @Override
  public void notificarReprogramacion(String idDonacion) {
    // POST /donaciones/{id}/en-deposito (sin body)
    postEstado(idDonacion, "/en-deposito", null);
  }

  /**
   * Método generico para hacer POST a un endpoint de cambio de estado.
   *
   * @param idDonacion ID de la donacion
   * @param pathEstado Path especifico del estado (ej: "/en-traslado", "/entregada")
   * @param body Body opcional (null si no requiere body)
   */
  private void postEstado(String idDonacion, String pathEstado, Object body) {
    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/donaciones/" + idDonacion + pathEstado))
          .header("Content-Type", "application/json");

      if (body != null) {
        String json = objectMapper.writeValueAsString(body);
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json));
      } else {
        requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
      }

      HttpRequest request = requestBuilder.build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 400) {
        throw new RuntimeException("Error al notificar a donaciones-service: " + response.body());
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Fallo la integracion con donaciones-service", e);
    }
  }

  @Override
  public List<DonacionDto> obtenerDonacionesListasParaEntregar() {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/donaciones/listas-para-entregar"))
          .header("Accept", "application/json")
          .GET()
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return objectMapper.readValue(response.body(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, DonacionDto.class));
      } else {
        throw new RuntimeException("Error al obtener donaciones: " + response.statusCode());
      }
    } catch (Exception e) {
      throw new RuntimeException("Fallo la consulta a donaciones-service", e);
    }
  }

  @Override
  public void notificarDonacionesNoPlanificadas(List<String> listaIdDonaciones) {
    if (listaIdDonaciones == null ||  listaIdDonaciones.isEmpty()) return;

    try {
      String json = objectMapper.writeValueAsString(Map.of("idDonaciones", listaIdDonaciones));

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/donaciones/revertir-no-planificadas"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new RuntimeException("Error al notificar a donaciones-service: " + response.body());
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Fallo la integracion con donaciones-service (revertir no planificadas)", e);
    }
  }
}