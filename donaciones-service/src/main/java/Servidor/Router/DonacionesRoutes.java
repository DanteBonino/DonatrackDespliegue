package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import Controllers.Donanciones.DonacionesController;
import Injector.Injector;
import UsuarioFinal.Rol;
import io.javalin.apibuilder.EndpointGroup;

public class DonacionesRoutes implements EndpointGroup {
  private DonacionesController controller;

  public  DonacionesRoutes(DonacionesController controller) {
    this.controller = controller;
  }
  @Override
  public void addEndpoints() {
    get("donaciones/listas-para-entregar", controller::getListasParaEntregar);
    //get("donaciones/asignadas", controller::getAsignadas);
    get("donaciones/pendientes-de-revision", controller::getPendientesDeRevision);
    get("/donaciones", controller::getAll);

    path("donaciones/{id}", () -> {
      get(controller::getOne);
      delete(controller::delete);
      post("/en-traslado", controller::iniciarTraslado);
      post("/entregada", controller::marcarComoEntregada);
      post("/entrega-fallida", controller::marcarComoFallida);
      post("/en-deposito", controller::almacenarEnDeposito);
      post("/lista-para-entregar", controller::alistarParaEntregar);
      post("/vencida", controller::vencer, Rol.ADMINISTRADOR);
      get("/historial-cambios-de-estado", controller::getHistorialDeCambiosDeEstado);
    });
  }
}
