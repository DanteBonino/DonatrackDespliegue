package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.get;

import Controllers.Health.HealthController;
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
