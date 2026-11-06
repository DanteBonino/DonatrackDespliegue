package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import Controllers.Necesidades.NecesidadesController;
import io.javalin.apibuilder.EndpointGroup;

public class NecesidadesRoutes implements EndpointGroup {
  private NecesidadesController controller;

  public NecesidadesRoutes(NecesidadesController controller) {
    this.controller = controller;
  }

  @Override
  public void addEndpoints() {
    path("/entidades-beneficiarias/{idBeneficiario}/necesidades", () -> {
      get(controller::getAll);
      post(controller::create);
      path("/{idNecesidad}", () -> {
        get(controller::getOne);
        delete(controller::delete);
      });
    });
  }
}
