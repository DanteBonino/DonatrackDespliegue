package externo.planificador;

import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.CamionDto;
import dtos.DonacionDto;
import dtos.SolicitudPlanificacionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para interactuar con el sistema externo de planificación de rutas.
 * Se encarga de preparar las solicitudes de planificación, dividiendo las donaciones
 * en lotes y enviando los datos en formato JSON.
 */
public class PlanificadorRutasAdapter {
  private static final int MAX_DONACIONES_POR_LOTE = 100;
  private final String urlPlanificador;
  private final ObjectMapper objectMapper;

  public PlanificadorRutasAdapter(String urlPlanificador) {
    this.urlPlanificador = urlPlanificador;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.findAndRegisterModules();
  }

  /**
   * Solicita la planificación al sistema externo.
   * Divide la lista de donaciones en lotes de tamaño máximo definido
   * y envía cada lote junto con la lista de camiones.
   *
   * @param donaciones lista de donaciones a planificar
   * @param camiones  lista de camiones disponibles para asignar
   */
  public void solicitarPlanificacion(List<DonacionDto> donaciones, List<CamionDto> camiones) {
    List<List<DonacionDto>> lotes = dividirEnLotes(donaciones, MAX_DONACIONES_POR_LOTE);

    for (int i = 0; i < lotes.size(); i++) {
      List<DonacionDto> lote = lotes.get(i);
      SolicitudPlanificacionRequest request = new SolicitudPlanificacionRequest(lote, camiones);
      String jsonPayload = serializarAJson(request);
      simularEnvioHttp(jsonPayload);
    }
  }

  /**
   * Convierte un objeto a su representación JSON utilizando Jackson.
   *
   * @param obj el objeto a serializar
   * @return el JSON como String
   * @throws RuntimeException si ocurre un error en la serialización
   */
  private String serializarAJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException("Error al serializar objeto a JSON", e);
    }
  }

  /**
   * Divide una lista de donaciones en sublistas del tamaño máximo especificado.
   *
   * @param lista         la lista completa de donaciones
   * @param tamanoMaxLote el tamaño máximo de cada lote
   * @return una lista de sublistas con las donaciones divididas
   */
  public List<List<DonacionDto>> dividirEnLotes(List<DonacionDto> lista, int tamanoMaxLote) {
    List<List<DonacionDto>> lotes = new ArrayList<>();
    for (int i = 0; i < lista.size(); i += tamanoMaxLote) {
      lotes.add(lista.subList(i, Math.min(i + tamanoMaxLote, lista.size())));
    }
    return lotes;
  }

  /**
   * Simula el envío de la solicitud HTTP al sistema externo.
   * En una implementación real, aquí se realizaría la llamada a la API.
   *
   * @param json el payload JSON que representa la solicitud de planificación
   */
  public void simularEnvioHttp(String json) {
    //System.out.println("==== ENVIANDO SOLICITUD AL PLANIFICADOR EXTERNO ====");
    //System.out.println(json);
    //System.out.println("====================================================");
  }
}