package TareasProgramadas.ReintentoDeEnvioDeNotificaciones;

import BusNotificaciones.BusNotificaciones;
import Repositories.Notificaciones.NotificacionesRepository;
import TareasProgramadas.TareaProgramada;

public class ReintentoDeEnvioDeNotificacionesPendientes extends TareaProgramada {
  private BusNotificaciones busNotificaciones;

  public ReintentoDeEnvioDeNotificacionesPendientes(
      BusNotificaciones busNotificaciones
  ) {
    this.busNotificaciones = busNotificaciones;
  }

  @Override
  protected void ejecutarTarea() {
    NotificacionesRepository.getInstance()
        .notificacionesEnviables()
        .forEach(this.busNotificaciones::procesar);
  }

}
