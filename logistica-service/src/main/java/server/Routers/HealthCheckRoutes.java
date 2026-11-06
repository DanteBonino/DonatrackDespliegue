package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.get;

import controllers.HealthController;
import io.javalin.apibuilder.EndpointGroup;


public class HealthCheckRoutes implements EndpointGroup {
  private HealthController controller;

  public HealthCheckRoutes(HealthController controller) {
    this.controller = controller;
  }

  @Override
  public void addEndpoints() {
    get("/health", controller::check);
  }
}
