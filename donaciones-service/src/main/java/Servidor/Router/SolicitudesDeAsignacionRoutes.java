package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import Controllers.SolicitudesDeAsignacion.SolicitudesDeAsignacionController;
import io.javalin.apibuilder.EndpointGroup;

public class SolicitudesDeAsignacionRoutes implements EndpointGroup {
  private SolicitudesDeAsignacionController controller;

  public SolicitudesDeAsignacionRoutes(SolicitudesDeAsignacionController solicitudesDeAsignacionController) {
    this.controller = solicitudesDeAsignacionController;
  }

  @Override
  public void addEndpoints() {
    path("solicitudes-de-asignacion", () -> {
      post("ejecutar-algoritmo", controller::ejecutarADemanda);
      get("/", controller::getAll);
      path("/{idSolicitud}", () -> {
        post("/aceptar", controller::aceptar);
      });
    });
  }
}
