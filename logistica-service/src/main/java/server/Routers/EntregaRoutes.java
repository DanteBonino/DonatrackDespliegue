package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import controllers.EntregaController;
import io.javalin.apibuilder.EndpointGroup;

public class EntregaRoutes implements EndpointGroup {
  private EntregaController controller;

  public EntregaRoutes(EntregaController controller) {
    this.controller = controller;
  }

  public void addEndpoints() {
    post("/entregas/{nombreEntrega}/confirmar", controller::confirmarRecepcion);
    post("/entregas/{nombreEntrega}/reportar-incidencia", controller::reportarIncidencia);
    post("/entregas/{nombreEntrega}/reprogramar", controller::reprogramar);
    get("/entregas/{nombreEntrega}/tracking", controller::redirigirATracking);
  }
}
