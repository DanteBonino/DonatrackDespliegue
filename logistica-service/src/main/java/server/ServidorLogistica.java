package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.Configuracion;
import controllers.*;
import controllers.utils.AsignadorDePlanificacion;
import datosmock.MockDataLoader;
import domain.Entrega;
import externo.planificador.SolicitanteDelPlanificadorExternoDeRutas;
import inicializacion.ChequeadorInicializacionBaseDeDatos;
import injector.LogisticaInjector;
import io.javalin.apibuilder.EndpointGroup;
import java.util.List;
import repositories.FlotaRepository.FlotaRepository;
import repositories.RutasRepository.RutasRepository;
import io.javalin.Javalin;
import server.Routers.CamionRoutes;
import server.Routers.EntregaRoutes;
import server.Routers.ExternoRoutes;
import server.Routers.HealthCheckRoutes;
import server.Routers.PlanificacionRoutes;
import server.Routers.Router;
import server.Routers.RutaRoutes;
import utility.BaseDeDatos;

public class ServidorLogistica {
  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    ChequeadorInicializacionBaseDeDatos.chequearBaseDeDatos();
    Configuracion configuracion = new Configuracion();

    LogisticaInjector injector = new LogisticaInjector();
    //Jackson no tiene soporte nativo para fechas, se le agrega este modulo
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    var app = Javalin.create(config -> {
      ExceptionHandler.registrar(config);
      new FiltroGlobalDeTransacciones().aplicarTransaccion(config);
      config.routes.apiBuilder(
          new Router(routers(injector))
      );
    }).start(8080);
  }

  private static List<EndpointGroup> routers(LogisticaInjector inyector) {
    SolicitanteDelPlanificadorExternoDeRutas solicitante = new SolicitanteDelPlanificadorExternoDeRutas(
        inyector.getDonacionesServiceAdapter(),
        inyector.getPlanificadorRutasAdapter()
    );
    return List.of(
        new HealthCheckRoutes(new HealthController("logistica-service")),
        new CamionRoutes(new CamionController()),
        new RutaRoutes(new MonitoreoController()),
        new ExternoRoutes(
            new CallbackController(
              inyector.getCallbackReceiver(),
              new AsignadorDePlanificacion(inyector.getEntregaPublicador()),
              inyector.getDonacionesServiceAdapter()
            )
        ),
        new PlanificacionRoutes(
            new PlanificacionController(
              solicitante::solicitarPlanificacionNocturna
            )
        ),
        new EntregaRoutes(
            new EntregaController(
              inyector.getAlmacenamientoDeComprobantes(),
              inyector.getGeneradorDeUrlDeTracking()
            )
        )
    );
  }
}