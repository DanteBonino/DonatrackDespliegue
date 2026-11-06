package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.get;

import Controllers.Notificaciones.NotificacionesController;
import io.javalin.apibuilder.EndpointGroup;

public class NotificacionesRoutes implements EndpointGroup {
  private NotificacionesController controller;

  public NotificacionesRoutes(NotificacionesController controller) {
    this.controller = controller;
  }

  @Override
  public void addEndpoints() {
    get("/notificaciones", controller::getAll);
  }
}