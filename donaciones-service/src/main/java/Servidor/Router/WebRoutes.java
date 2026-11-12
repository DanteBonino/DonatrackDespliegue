package Servidor.Router;

import static UsuarioFinal.Rol.ADMINISTRADOR;
import static UsuarioFinal.Rol.CONSESIONACTIVA;
import static UsuarioFinal.Rol.CUALQUIERA;
import static UsuarioFinal.Rol.DONANTE;
import static UsuarioFinal.Rol.SINSESIONACTIVA;
import static io.javalin.apibuilder.ApiBuilder.beforeMatched;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import Controllers.Web.AuthController;
import Controllers.Web.HomeController;
import Controllers.Web.AdminWebController;
import Controllers.Web.BeneficiariaWebController;
import Controllers.Web.DonanteWebController;
import Servidor.Autorizador.Autorizador;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import UsuarioFinal.Rol;
import io.javalin.apibuilder.EndpointGroup;

public class WebRoutes implements EndpointGroup {

  private final HomeController homeController;
  private final AuthController authController;
  private final DonanteWebController donanteWebController;
  private final BeneficiariaWebController beneficiaria;
  private final AdminWebController admin;

  public WebRoutes(HomeController homeController,
                   AuthController authController,
                   DonanteWebController donanteWebController,
                   BeneficiariaWebController beneficiariaWebController,
                   AdminWebController adminWebController) {
    this.homeController = homeController;
    this.authController = authController;
    this.donanteWebController = donanteWebController;
    this.beneficiaria = beneficiariaWebController;
    this.admin = adminWebController;
  }

  @Override
  public void addEndpoints() {
    beforeMatched(Autorizador::autorizar);
    // ==== Area PUBLICA ====
    get("/", homeController::index, SINSESIONACTIVA);
    get("/legal", homeController::legal, CUALQUIERA);
    get("/donaciones/entregadas", homeController::donacionesEntregadas, CUALQUIERA);

    // ==== Area AUTH ====
    get("/login", authController::showLogin, SINSESIONACTIVA);
    post("/login", authController::doLogin, SINSESIONACTIVA);
    get("/registro", authController::showRegistro, SINSESIONACTIVA);
    post("/logout", authController::doLogout, CONSESIONACTIVA);

    get("/registro/donante-humano", authController::showRegistroHumano, SINSESIONACTIVA);
    post("/registro/donante-humano", authController::doRegistroHumano, SINSESIONACTIVA);
    get("/registro/donante-juridico", authController::showRegistroJuridico, SINSESIONACTIVA);
    post("/registro/donante-juridico", authController::doRegistroJuridico, SINSESIONACTIVA);
    get("/registro/entidad-beneficiaria", authController::showRegristroBeneficiaria, SINSESIONACTIVA);
    post("/registro/entidad-beneficiaria", authController::doRegistroBeneficiario, SINSESIONACTIVA);

    get("/dashboard", ctx -> {
      Rol rol = ManejadorDeSesion.obtenerRol(ctx).get();
      rol.showDashBoard(ctx);
    } ,CONSESIONACTIVA);

    get("/mis-donaciones", donanteWebController::misDonaciones, DONANTE);
    get("/entidades-beneficiarias", donanteWebController:: entidades, DONANTE);
    get("/donaciones/en-deposito", admin::deposito, ADMINISTRADOR);
    post("/donaciones/{id}/vencer", admin::vencerDonacion, ADMINISTRADOR);
    get("/necesidades", beneficiaria::necesidades, Rol.ENTIDADBENEFICIARIA);
    get("/donaciones/asignadas", beneficiaria::donacionesAsignadas, Rol.ENTIDADBENEFICIARIA);
  }

}