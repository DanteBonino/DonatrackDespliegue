package Servidor.Router;

import static io.javalin.apibuilder.ApiBuilder.crud;

import Controllers.EntidadesBenefeciarias.EntidadesBeneficiariasController;
import Controllers.Necesidades.NecesidadesController;
import io.javalin.apibuilder.EndpointGroup;

public class BeneficiariosRoutes implements EndpointGroup {
  private EntidadesBeneficiariasController controller;

  public BeneficiariosRoutes(EntidadesBeneficiariasController controller) {
    this.controller = controller;
  }

  @Override
  public void addEndpoints() {
    crud("entidades-beneficiarias/{entidadBeneficiaria-id}", controller);
  }
}
