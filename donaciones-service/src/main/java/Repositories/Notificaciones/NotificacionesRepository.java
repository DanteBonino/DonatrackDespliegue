package Repositories.Notificaciones;

import Contacto.DatosDeContacto;
import Notificaciones.Notificacion;
import Repositories.Generales.RepositorioBasico;
import Repositories.Generales.filtros.Filtro;
import Repositories.Generales.filtros.Filtros;
import java.util.List;

public class NotificacionesRepository implements INotificacionesRepository, RepositorioBasico<Notificacion> {
  private static final NotificacionesRepository INSTANCE = new NotificacionesRepository();

  public static NotificacionesRepository getInstance() {
    return INSTANCE;
  }

  private NotificacionesRepository() {
  }

  @Override
  public void registrar(Notificacion notificacion) {
    RepositorioBasico.super.registrar(notificacion);
  }

  @Override
  public List<Notificacion> notificacionesEnviables() {
    Filtros esEnviable = new Filtros();
    esEnviable.agregarFiltro(Filtro.igual("fueEntregada", false));
    esEnviable.agregarFiltro(Filtro.menorQue("vecesRebotada", Notificacion.MAXIMO_REINTENTOS));
    return obtenerSegun(esEnviable);
  }

  @Override
  public List<Notificacion> getInstancias() {
    return RepositorioBasico.super.getInstancias();
  }

  @Override
  public Class<Notificacion> clase() {
    return Notificacion.class;
  }

  public List<Notificacion> getUltimasDe(DatosDeContacto contacto, int limite) {
    String jpql = "SELECT n FROM Notificacion n " +
        "WHERE n.contacto = :contacto " +
        "ORDER BY n.id DESC";

    return entityManager()
        .createQuery(jpql, Notificacion.class)
        .setParameter("contacto", contacto)
        .setMaxResults(limite)
        .getResultList();
  }
}
