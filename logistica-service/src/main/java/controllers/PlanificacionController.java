package controllers;

import dtos.MensajeResponse;
import io.javalin.http.Context;

public class PlanificacionController {

  private final Runnable accionPlanificacion;

  public PlanificacionController(Runnable accionPlanificacion) {
    this.accionPlanificacion = accionPlanificacion;
  }

  public void ejecutarPlanificacion(Context ctx) {
    accionPlanificacion.run();
    ctx.status(200).json(new MensajeResponse("Planificacion nocturna comenzada con exito."));
  }
}
