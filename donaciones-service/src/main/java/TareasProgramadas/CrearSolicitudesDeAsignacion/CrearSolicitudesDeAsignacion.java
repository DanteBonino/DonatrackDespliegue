package TareasProgramadas.CrearSolicitudesDeAsignacion;


import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoDeAsignacion;
import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.SolicitudesDeAsignacion.SolicitudesDeAsignacionRepository;
import java.util.ArrayList;
import java.util.List;


public class CrearSolicitudesDeAsignacion {
  private final AlgoritmoDeAsignacion algoritmoDeAsignacion;
  private final DonacionesRepository donacionesRepository;

  public CrearSolicitudesDeAsignacion(AlgoritmoDeAsignacion algoritmoDeAsignacion, DonacionesRepository donacionesRepository) {
    this.algoritmoDeAsignacion = algoritmoDeAsignacion;
    this.donacionesRepository = donacionesRepository;
  }

  public List<SolicitudDeAsignacion> ejecutar() {
    var entidadesBeneficiarias = BeneficiariosRepository.getInstance().getInstancias();
    List<SolicitudDeAsignacion> solicitudesCreadas = new ArrayList<>();

    for (var donacion : donacionesRepository.getDonacionesEnDeposito()) {
      var candidatas = algoritmoDeAsignacion.crearRanking(donacion, entidadesBeneficiarias);
      if (!candidatas.isEmpty()) {
        SolicitudDeAsignacion solicitud = new SolicitudDeAsignacion(donacion, candidatas);
        SolicitudesDeAsignacionRepository.getInstance().registrar(solicitud);
        solicitudesCreadas.add(solicitud);
      }
    }
    return solicitudesCreadas;
  }
}