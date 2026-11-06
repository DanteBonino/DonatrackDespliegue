package externo.planificador; // o en un paquete domain/

import dtos.CamionDto;
import dtos.DonacionDto;
import domain.Camion;
import externo.donaciones.DonacionesServiceAdapter;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepositoryEnMemoria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitanteDelPlanificadorExternoDeRutas {

  private final DonacionesServiceAdapter donacionesAdapter;
  private final PlanificadorRutasAdapter planificadorAdapter;
  private static final Logger logger = LoggerFactory.getLogger(SolicitanteDelPlanificadorExternoDeRutas.class);

  public SolicitanteDelPlanificadorExternoDeRutas(DonacionesServiceAdapter donacionesAdapter,
                                                  PlanificadorRutasAdapter planificadorAdapter) {
    this.donacionesAdapter = donacionesAdapter;
    this.planificadorAdapter = planificadorAdapter;
  }

  public void solicitarPlanificacionNocturna() {
    logger.info("Iniciando proceso de solicitud de planificación nocturna...");

    List<DonacionDto> donaciones = donacionesAdapter.obtenerDonacionesListasParaEntregar();

    if (donaciones.isEmpty()) {
      logger.info("No hay donaciones pendientes de asignar.");
      return;
    }

    List<Camion> camionesDisponibles = FlotaRepositoryConPersistencia.getInstance().obtenerDisponibles();

    List<CamionDto> camionesDto = camionesDisponibles.stream()
        .map(c -> new CamionDto(c.getPatente(), c.getVolumen(), c.getAltura(), c.getCapacidad()))
        .toList();

    planificadorAdapter.solicitarPlanificacion(donaciones, camionesDto);

    logger.info("Solicitud enviada al planificador externo para {} donaciones con {} camiones",
        donaciones.size(), camionesDto.size());
  }
}