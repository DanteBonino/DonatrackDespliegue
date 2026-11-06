package tareasprogramadas;

import externo.planificador.SolicitanteDelPlanificadorExternoDeRutas;
import injector.LogisticaInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.BaseDeDatos;

public class PlanificacionFueraDePicoMain {

  private static final Logger logger = LoggerFactory.getLogger(PlanificacionFueraDePicoMain.class);

  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    logger.info("Iniciando tarea programada: Planificacion Nocturna de Rutas...");

    // Inyectamos las dependencias reales
    LogisticaInjector injector = new LogisticaInjector();

    SolicitanteDelPlanificadorExternoDeRutas gestor = new SolicitanteDelPlanificadorExternoDeRutas(
        injector.getDonacionesServiceAdapter(),
        injector.getPlanificadorRutasAdapter()
    );

    // Ejecutamos la logica de negocio
    gestor.solicitarPlanificacionNocturna();

    logger.info("Tarea de planificacion finalizada.");
  }
}