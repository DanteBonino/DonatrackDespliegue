package TareasProgramadas.CrearSolicitudesDeAsignacion;


import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoMixtoPorInterseccion;
import Injector.Injector;
import Repositories.Donaciones.DonacionesRepository;
import utils.BaseDeDatos;

public class CrearSolicitudesDeAsignacionMain {
  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    Injector injector = new Injector();
    new TareaCrearSolicitudesDeAsignacion(
        new CrearSolicitudesDeAsignacion(
            injector.getAlgoritmoDeAsignacion(),
            DonacionesRepository.getInstance()
        )
    )
   .ejecutar();
  }
}