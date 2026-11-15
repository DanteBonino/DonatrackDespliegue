package Repositories.Notificaciones;

import Contacto.DatosDeContacto;
import Notificaciones.Notificacion;
import Repositories.Generales.RepositorioBasico;
import Repositories.Generales.filtros.Filtro;
import Repositories.Generales.filtros.Filtros;
import Repositories.Generales.paginacion.Pagina;
import java.util.List;
import javax.persistence.EntityManager;

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

  @SuppressWarnings("JpaQlInspection")   // mismo aviso de IntelliJ que en las donaciones
  public Pagina<Notificacion> getEntregadasDe(DatosDeContacto contacto, int numero, int tamanio) {
    EntityManager em = entityManager();
    String desde = "FROM Notificacion n WHERE n.contacto = :contacto AND n.fueEntregada = true";

    long total = em.createQuery("SELECT COUNT(n) " + desde, Long.class)
        .setParameter("contacto", contacto)
        .getSingleResult();
    int totalPaginas = (int) Math.max(1, Math.ceil(total / (double) tamanio));
    int actual = Math.min(Math.max(numero, 1), totalPaginas);

    List<Notificacion> items = em.createQuery("SELECT n " + desde + " ORDER BY n.id DESC", Notificacion.class)
        .setParameter("contacto", contacto)
        .setFirstResult((actual - 1) * tamanio)
        .setMaxResults(tamanio)
        .getResultList();

    return new Pagina<>(items, actual, totalPaginas, total, tamanio);
  }

  public List<Notificacion> getNoLeidasRecientesDe(DatosDeContacto contacto, int limite) {
    return entityManager().createQuery("""
          SELECT n FROM Notificacion n
          WHERE n.contacto = :contacto AND n.fueEntregada = true AND n.fechaDeLectura IS NULL
          ORDER BY n.id DESC
          """, Notificacion.class)
        .setParameter("contacto", contacto)
        .setMaxResults(limite)
        .getResultList();
  }

  public long contarNoLeidas(DatosDeContacto contacto) {
    String jpql = "SELECT COUNT(n) FROM Notificacion n WHERE n.contacto = :contacto AND n.fechaDeLectura IS NULL";

    return entityManager().createQuery(jpql, Long.class)
        .setParameter("contacto", contacto)
        .getSingleResult();
  }
}
