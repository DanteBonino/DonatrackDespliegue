package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import Controllers.Categorias.CategoriasController;
import Controllers.Lote.LotesController;
import io.javalin.apibuilder.EndpointGroup;

public class LotesRoutes implements EndpointGroup {
  private LotesController controller;

  public LotesRoutes(LotesController controller) {
    this.controller = controller;
  }


  @Override
  public void addEndpoints() {
    get("/lotes", controller::getAll);
    post("/lotes", controller::create);
  }
}
