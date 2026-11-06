package Servidor;
import Controllers.Categorias.CategoriasController;
import Controllers.Donanciones.DonacionesController;
import Controllers.Donantes.DonantesController;
import Controllers.EntidadesBenefeciarias.EntidadesBeneficiariasController;
import Controllers.Health.HealthController;
import Controllers.Lote.LotesController;
import Controllers.Necesidades.NecesidadesController;
import Controllers.Notificaciones.NotificacionesController;
import Controllers.SolicitudesDeAsignacion.SolicitudesDeAsignacionController;
import Controllers.Web.*;
import Controllers.SubCategorias.SubCategoriasController;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteJuridico.TipoJuridica;
import Injector.Injector;
import Servidor.ManejadorDeExcepciones.ManejadorDeExcepciones;
import Servidor.ManejadorDeSesiones.DatosSesion;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import Servidor.Router.CategoriasRoutes;
import Servidor.Router.DonacionesRoutes;
import Servidor.Router.LotesRoutes;
import Servidor.Router.NotificacionesRoutes;
import Servidor.Router.Router;
import Servidor.Router.BeneficiariosRoutes;
import Servidor.Router.DonantesRoutes;
import Servidor.Router.HealthCheckRoutes;
import Servidor.Router.NecesidadesRoutes;
import Servidor.Router.SolicitudesDeAsignacionRoutes;
import Servidor.Router.SubCategoriasRoutes;
import inicializacion.ChequeadorInicializacionBaseDeDatos;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Servidor.Router.WebRoutes;
import Servidor.templates.JavalinRenderer;
import Servidor.templates.JavalinHandlebars;
import utils.BaseDeDatos;

public class ServidorDonaciones {
  private static final Logger logger = LoggerFactory.getLogger(ServidorDonaciones.class);

  public static Javalin crearApp(Injector inyectorDeDependencias) {
    return Javalin.create(config -> {
      config.fileRenderer(new JavalinRenderer().
          register("hbs", new JavalinHandlebars()));
      config.validation.register(LocalDate.class, LocalDate::parse);
      config.validation.register(Genero.class, Genero::valueOf);
      config.validation.register(TipoJuridica.class, TipoJuridica::valueOf);

      ManejadorDeExcepciones.registrarExcepciones(config, logger);
      new FiltroGlobalDeTransacciones().aplicarTransaccion(config);

      config.routes.apiBuilder(
          new Router(routers(inyectorDeDependencias))
      );
    });
  }

  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    ChequeadorInicializacionBaseDeDatos.chequearBaseDeDatos();
    Injector inyectorDeDependencias = new Injector();
    inyectorDeDependencias.configurarServiceLocator();
    inyectorDeDependencias.configurarGeolocalizador();
    Javalin app = crearApp(inyectorDeDependencias);

    app.start(7070);
    Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
  }

  private static List<EndpointGroup> routers(Injector inyectorDeDependencias) {
    return List.of(
        new WebRoutes(
            new HomeController(),
            new AuthController(),
            new DonanteWebController(),
            new BeneficiariaWebController(),
            new AdminWebController()
        ),
        new DonacionesRoutes(
            new DonacionesController()
        ),
        new SolicitudesDeAsignacionRoutes(
            new SolicitudesDeAsignacionController(
                inyectorDeDependencias.getCrearSolicitudesDeAsignacion()
            )
        ),
        new NecesidadesRoutes(
            new NecesidadesController()
        ),
        new BeneficiariosRoutes(
            new EntidadesBeneficiariasController()
        ),
        new HealthCheckRoutes(new HealthController("donaciones-service")),

        new NotificacionesRoutes(
            new NotificacionesController()
        ),
        new SubCategoriasRoutes(
            new SubCategoriasController()
        ),
        new CategoriasRoutes(
            new CategoriasController()
        ),
        new LotesRoutes(
            new LotesController(inyectorDeDependencias.getPublicadorCambiosDeEstado())
        ),
        new DonantesRoutes(
            new DonantesController()
        )
    );
  }
}