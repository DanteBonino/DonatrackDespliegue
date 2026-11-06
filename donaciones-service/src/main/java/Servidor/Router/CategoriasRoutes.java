package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.get;

import Controllers.Categorias.CategoriasController;
import io.javalin.apibuilder.EndpointGroup;

public class CategoriasRoutes implements EndpointGroup {
  private CategoriasController controller;

  public CategoriasRoutes(CategoriasController controller) {
    this.controller = controller;
  }


  @Override
  public void addEndpoints() {
    get("/categorias", controller::getAll);
  }
}
