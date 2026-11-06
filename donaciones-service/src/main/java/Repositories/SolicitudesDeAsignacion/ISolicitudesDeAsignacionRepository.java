package Repositories.SolicitudesDeAsignacion;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import java.util.List;

public interface ISolicitudesDeAsignacionRepository {
  void registrar(SolicitudDeAsignacion nuevaSolicitud);
  SolicitudDeAsignacion findById(String id);
  List<SolicitudDeAsignacion> getInstancias();
}
