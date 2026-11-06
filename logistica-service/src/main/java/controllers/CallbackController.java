package controllers;

import controllers.utils.AsignadorDePlanificacion;
import domain.Camion;
import domain.Entrega;
import domain.Ruta;
import dtos.EntregaDto;
import dtos.MensajeResponse;
import dtos.PlanificacionResponse;
import dtos.RutaPlanificadaDto;
import exceptions.CamionNoDisponibleException;
import externo.callback.CallbackPlanificadorReceiver;
import externo.donaciones.DonacionesServiceAdapter;
import io.javalin.http.Context;
import repositories.FlotaRepository.FlotaRepository;
import repositories.RutasRepository.RutasRepository;
import java.time.LocalDate;
import java.util.List;

public class CallbackController {

  private final CallbackPlanificadorReceiver receiver;
  private final AsignadorDePlanificacion asignadorDePlanificacion;
  private final DonacionesServiceAdapter donacionesServiceAdapter;

  public CallbackController(CallbackPlanificadorReceiver receiver,
                            AsignadorDePlanificacion asignadorDePlanificacion,
                            DonacionesServiceAdapter donacionesServiceAdapter) {
    this.receiver = receiver;
    this.asignadorDePlanificacion = asignadorDePlanificacion;
    this.donacionesServiceAdapter = donacionesServiceAdapter;
  }

  /**
   * Endpoint: POST /externo/callback-splanificacion
   *
   * Procesa un callback JSON con rutas planificadas y crea nuevas
   * instancias de {@link Ruta} en el sistema.
   *
   * <p>Acciones principales:
   * - Parsear el JSON recibido.
   * - Validar y asignar camiones.
   * - Crear entregas y agregarlas a la ruta.
   * - Guardar las rutas en el repositorio.
   *
   * <p>Respuestas HTTP:
   * - 200: Planificación recibida con éxito.
   * - 404: Camión no encontrado.
   * - 409: Camión no disponible.
   * - 400: Error al procesar el JSON.
   *
   * @param ctx contexto de Javalin con la petición y la respuesta
   */
  public void recibirPlanificacion(Context ctx) {
    String json = ctx.body();
    PlanificacionResponse response = receiver.procesarCallback(json);
    List <Ruta> rutasAsignadas = asignadorDePlanificacion.asignarRutas(response, LocalDate.now());
    List<String> noAsignadas = response.getDonacionesSinAsignar();
    if (noAsignadas != null && !noAsignadas.isEmpty()) {
        donacionesServiceAdapter.notificarDonacionesNoPlanificadas(noAsignadas);
    }
    ctx.status(200).json(new MensajeResponse("Planificación recibida con éxito. Rutas creadas: " + rutasAsignadas.size()));
  }
}