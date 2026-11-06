package TareasProgramadas.CrearSolicitudesDeAsignacion;

import TareasProgramadas.TareaProgramada;

public class TareaCrearSolicitudesDeAsignacion extends TareaProgramada {
  private final CrearSolicitudesDeAsignacion crearSolicitudesDeAsignacion;

  public TareaCrearSolicitudesDeAsignacion(CrearSolicitudesDeAsignacion crearSolicitudesDeAsignacion) {
    this.crearSolicitudesDeAsignacion = crearSolicitudesDeAsignacion;
  }

  @Override
  protected void ejecutarTarea() {
    crearSolicitudesDeAsignacion.ejecutar(); // el cron no necesita el resultado, se descarta acá
  }
}
