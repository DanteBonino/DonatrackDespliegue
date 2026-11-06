package externo.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.PlanificacionResponse; // Asegúrate de que el import sea correcto (no "java.DTOs")
import exceptions.ProcesarCallBackDePlanificacionException;

public class CallbackPlanificadorReceiver {
  private final ObjectMapper objectMapper;

  public CallbackPlanificadorReceiver() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Procesa el callback recibido en formato JSON y lo convierte en un objeto {@link PlanificacionResponse}.
   *
   * @param jsonRecibido el contenido JSON enviado por el planificador
   * @return una instancia de {@link PlanificacionResponse} con los datos deserializados
   */
  public PlanificacionResponse procesarCallback(String jsonRecibido) {
    try {
      return objectMapper.readValue(jsonRecibido, PlanificacionResponse.class);
    } catch (Exception e) {
      throw new ProcesarCallBackDePlanificacionException();
    }
  }
}