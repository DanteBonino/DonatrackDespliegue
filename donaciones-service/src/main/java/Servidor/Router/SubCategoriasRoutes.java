package Servidor.Router;
import static io.javalin.apibuilder.ApiBuilder.get;

import Controllers.SubCategorias.SubCategoriasController;
import io.javalin.apibuilder.EndpointGroup;

public class SubCategoriasRoutes implements EndpointGroup {
  private SubCategoriasController controller;

  public SubCategoriasRoutes(SubCategoriasController controller) {
    this.controller = controller;
  }

  @Override
  public void addEndpoints() {
    get("/subcategorias", controller::getAll);
  }
}
