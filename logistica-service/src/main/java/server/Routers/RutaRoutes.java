package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.get;
import controllers.MonitoreoController;
import io.javalin.apibuilder.EndpointGroup;

public class RutaRoutes implements EndpointGroup {
  private MonitoreoController controller;

  public RutaRoutes(MonitoreoController controller) {
    this.controller = controller;
  }
  @Override
  public void addEndpoints() {
    get("/rutas/activas", controller::obtenerRutasActivas);
  }
}
