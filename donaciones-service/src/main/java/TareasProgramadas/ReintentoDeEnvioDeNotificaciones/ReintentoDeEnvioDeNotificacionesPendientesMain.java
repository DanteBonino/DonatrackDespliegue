package TareasProgramadas.ReintentoDeEnvioDeNotificaciones;

import Injector.Injector;
import utils.BaseDeDatos;

public class ReintentoDeEnvioDeNotificacionesPendientesMain {

  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    Injector inyectorDeDependencias = new Injector();
    new ReintentoDeEnvioDeNotificacionesPendientes(
        inyectorDeDependencias.getBusNotificaciones()
    ).ejecutar();
  }

}
