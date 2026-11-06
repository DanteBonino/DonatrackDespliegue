package UsuarioFinal;

import Controllers.Web.AdminWebController;
import Controllers.Web.BeneficiariaWebController;
import Controllers.Web.DonanteWebController;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;

public enum Rol implements RouteRole {
  DONANTE {
    @Override
    public void showDashBoard(Context context) {
      new DonanteWebController().dashboard(context);
    }
  },
  ADMINISTRADOR {
    @Override
    public void showDashBoard(Context context) {
      new AdminWebController().dashboard(context);
    }
  },
  ENTIDADBENEFICIARIA {
    @Override
    public void showDashBoard(Context context) {
      new BeneficiariaWebController().dashboard(context);
    }
  },
  CUALQUIERA,
  SINSESIONACTIVA,
  CONSESIONACTIVA;


  public void showDashBoard(Context context) {

  }
}
