package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.post;

import controllers.CallbackController;
import io.javalin.apibuilder.EndpointGroup;

public class ExternoRoutes implements EndpointGroup {
  private CallbackController controller;

  public ExternoRoutes(CallbackController controller) {
    this.controller = controller;
  }

  public void addEndpoints() {
    post("/externo/callback-planificacion", controller::recibirPlanificacion);
  }
}
