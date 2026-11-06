package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.crud;

import Controllers.Donantes.DonantesController;
import io.javalin.apibuilder.EndpointGroup;

public class DonantesRoutes implements EndpointGroup {
  private DonantesController controller;

  public DonantesRoutes(DonantesController donantesController) {
    this.controller = donantesController;
  }

  @Override
  public void addEndpoints() {
    crud("donantes/{donante-id}", controller);
  }
}
