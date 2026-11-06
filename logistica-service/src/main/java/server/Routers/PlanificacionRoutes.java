package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.post;

import controllers.PlanificacionController;
import io.javalin.apibuilder.EndpointGroup;

public class PlanificacionRoutes implements EndpointGroup {
  private PlanificacionController controller;

  public PlanificacionRoutes(PlanificacionController controller) {
    this.controller = controller;
  }
  @Override
  public void addEndpoints() {
    post("/planificacion/ejecutar", controller::ejecutarPlanificacion);
  }
}
