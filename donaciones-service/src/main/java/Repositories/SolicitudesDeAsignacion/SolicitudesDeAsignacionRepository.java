package Repositories.SolicitudesDeAsignacion;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Repositories.Generales.RepositorioBasico;
import java.util.List;

public class SolicitudesDeAsignacionRepository implements ISolicitudesDeAsignacionRepository, RepositorioBasico<SolicitudDeAsignacion> {
  private static SolicitudesDeAsignacionRepository INSTANCE = new SolicitudesDeAsignacionRepository();

  public static SolicitudesDeAsignacionRepository getInstance() {
    return INSTANCE;
  }

  private SolicitudesDeAsignacionRepository() {

  }

  @Override
  public void registrar(SolicitudDeAsignacion nuevaSolicitud) {
    RepositorioBasico.super.registrar(nuevaSolicitud);
  }

  @Override
  public SolicitudDeAsignacion findById(String id) {
    return RepositorioBasico.super.findById(id);
  }

  @Override
  public List<SolicitudDeAsignacion> getInstancias() {
    return RepositorioBasico.super.getInstancias();
  }

  public Class<SolicitudDeAsignacion> clase() {
    return SolicitudDeAsignacion.class;
  }
}
