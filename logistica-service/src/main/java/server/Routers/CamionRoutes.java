package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import controllers.CamionController;
import io.javalin.apibuilder.EndpointGroup;

public class CamionRoutes implements EndpointGroup {
  private CamionController controller;

  public CamionRoutes(CamionController controller) {
     this.controller = controller;
  }

  @Override
  public void addEndpoints() {
    path("camiones", () -> {
      get("/", controller::obtenerTodosLosCamiones);
      path("/{patente}", () -> {
        get("/", controller::obtenerEstadoCamion);
        post("posicion", controller::actualizarPosicion);
        post("iniciar-ruta", controller::iniciarRuta);
        get("/posicion", controller::obtenerPosicionCamion);
      });
    });
  }
}
