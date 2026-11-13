package Repositories.Donaciones;

import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Generales.RepositorioConSoftDelete;

import Repositories.Generales.paginacion.Pagina;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

public class DonacionesRepository implements IDonacionesRepository, RepositorioConSoftDelete<DonacionIndependiente> {
  private static final DonacionesRepository INSTACE = new DonacionesRepository();

  private DonacionesRepository() {
  }

  public static DonacionesRepository getInstance() {
    return INSTACE;
  }

  @Override
  public List<DonacionIndependiente> getInstancias() {
    return RepositorioConSoftDelete.super.getInstancias();
  }

  @Override
  public DonacionIndependiente findById(String id) {
    return RepositorioConSoftDelete.super.findById(id);
  }

  @Override
  public void registrar(DonacionIndependiente nuevaEntidad) {
    RepositorioConSoftDelete.super.registrar(nuevaEntidad);
  }

  @Override
  public void eliminarInstancia(String id) {
    RepositorioConSoftDelete.super.eliminarInstancia(id);
  }

  public Class<DonacionIndependiente> clase() {
    return DonacionIndependiente.class;
  }

  public List<DonacionIndependiente> getEnEstado(EstadoDonacion unEstado) {
    return obtenerTodosSegun("estadoActual", unEstado);
  }

  private static final String DONACIONES_ASIGNADAS_A_ENTIDAD =
      "d.id IN (SELECT d2.id FROM DonacionIndependiente d2 " +
          "         JOIN d2.historialEstado h WHERE h.asignado = :entidad)";

  public Pagina<DonacionIndependiente> getDonacionesDe(EntidadBeneficiaria entidad, int numero, int tamanio) {
    EntityManager em = entityManager();

    long total = em.createQuery(
            "SELECT COUNT(d) FROM DonacionIndependiente d WHERE " + DONACIONES_ASIGNADAS_A_ENTIDAD, Long.class)
        .setParameter("entidad", entidad)
        .getSingleResult();
    int totalPaginas = (int) Math.max(1, Math.ceil(total / (double) tamanio));
    int actual = Math.min(Math.max(numero, 1), totalPaginas);

    // 1) Solo los ids de la página pedida
    List<Long> ids = em.createQuery(
            "SELECT d.id FROM DonacionIndependiente d WHERE " + DONACIONES_ASIGNADAS_A_ENTIDAD +
                " ORDER BY d.id DESC", Long.class)
        .setParameter("entidad", entidad)
        .setFirstResult((actual - 1) * tamanio)
        .setMaxResults(tamanio)
        .getResultList();

    if (ids.isEmpty()) {
      return new Pagina<>(List.of(), actual, totalPaginas, total);
    }

    // 2) Esas donaciones, con todos los fetches
    List<DonacionIndependiente> items = em.createQuery(
            "SELECT DISTINCT d FROM DonacionIndependiente d " +
                "JOIN FETCH d.item i " +
                "JOIN FETCH i.bien b " +
                "JOIN FETCH b.subCategoria " +
                "JOIN FETCH d.donante " +
                "LEFT JOIN FETCH d.historialEstado " +
                "WHERE d.id IN :ids " +
                "ORDER BY d.id DESC", DonacionIndependiente.class)
        .setParameter("ids", ids)
        .getResultList();

    return new Pagina<>(items, actual, totalPaginas, total);
  }

  public Map<EstadoDonacion, Long> contarDonacionesPorEstadoDe(EntidadBeneficiaria entidad) {
    String jpql = "SELECT h.estadoDonacion, COUNT(d) " +
        "FROM DonacionIndependiente d " +
        "JOIN d.historialEstado h " +
        "WHERE h.asignado = :entidad " +
        "GROUP BY h.estadoDonacion";

    List<Object[]> resultados = entityManager()
        .createQuery(jpql, Object[].class)
        .setParameter("entidad", entidad)
        .getResultList();

    Map<EstadoDonacion, Long> conteos = new HashMap<>();
    for (Object[] fila : resultados) {
      EstadoDonacion estado = (EstadoDonacion) fila[0];
      Long cantidad = (Long) fila[1];
      conteos.put(estado, cantidad);
    }

    return conteos;
  }

  public Pagina<DonacionIndependiente> getDonacionesDeDonante(Donante donante, int numero, int tamanio) {
    EntityManager em = entityManager();

    long total = em.createQuery(
            "SELECT COUNT(d) FROM DonacionIndependiente d " +
                "WHERE d.donante = :donante AND d.estaEliminado = false", Long.class)
        .setParameter("donante", donante)
        .getSingleResult();
    int totalPaginas = (int) Math.max(1, Math.ceil(total / (double) tamanio));
    int actual = Math.min(Math.max(numero, 1), totalPaginas);

    // 1) Solo los ids de la página pedida (sin joins: el LIMIT lo aplica la base)
    List<Long> ids = em.createQuery(
            "SELECT d.id FROM DonacionIndependiente d " +
                "WHERE d.donante = :donante AND d.estaEliminado = false " +
                "ORDER BY d.id DESC", Long.class)
        .setParameter("donante", donante)
        .setFirstResult((actual - 1) * tamanio)
        .setMaxResults(tamanio)
        .getResultList();

    if (ids.isEmpty()) {
      return new Pagina<>(List.of(), actual, totalPaginas, total);
    }

    // 2) Esas donaciones, con todos los fetches
    List<DonacionIndependiente> items = em.createQuery(
            "SELECT DISTINCT d FROM DonacionIndependiente d " +
                "JOIN FETCH d.item i " +
                "JOIN FETCH i.bien b " +
                "JOIN FETCH b.subCategoria " +
                "LEFT JOIN FETCH d.historialEstado " +
                "LEFT JOIN FETCH d.donante " +
                "WHERE d.id IN :ids " +
                "ORDER BY d.id DESC", DonacionIndependiente.class)
        .setParameter("ids", ids)
        .getResultList();

    return new Pagina<>(items, actual, totalPaginas, total);
  }

  public Map<EstadoDonacion, Long> contarDonacionesPorEstadoDeDonante(Donante donante) {
    String jpql = "SELECT d.estadoActual, COUNT(d) " +
        "FROM DonacionIndependiente d " +
        "WHERE d.donante = :donante AND d.estaEliminado = false " +
        "GROUP BY d.estadoActual";

    List<Object[]> resultados = entityManager()
        .createQuery(jpql, Object[].class)
        .setParameter("donante", donante)
        .getResultList();

    Map<EstadoDonacion, Long> conteos = new HashMap<>();
    for (Object[] fila : resultados) {
      EstadoDonacion estado = (EstadoDonacion) fila[0];
      Long cantidad = (Long) fila[1];
      conteos.put(estado, cantidad);
    }

    return conteos;
  }

  @SuppressWarnings("unchecked")
  public List<Object[]> getMarcadoresDeEntregadas(int limite) {
    return entityManager().createNativeQuery("""
        SELECT
            eb.razon_social,
            eb.posicion_latitud,
            eb.posicion_longitud,
            i.cantidad_de_unidades,
            sc.nombre
        FROM DonacionesIndependientes d
        JOIN Historial_Cambios_de_Estado h
            ON h.id_donacion_independiente = d.id
        JOIN EntidadesBeneficiarias eb
            ON h.id_entidad_beneficiaria_asignada = eb.id
        JOIN ItemsDonacion i
            ON d.id_item = i.id
        JOIN Bienes b
            ON i.id_bien = b.id
        JOIN SubCategorias sc
            ON b.id_subcategoria = sc.id
        WHERE d.estado_actual = 'ENTREGADA'
          AND d.esta_eliminado = false
          AND h.id_entidad_beneficiaria_asignada IS NOT NULL
          AND h.orden_de_transicion = (
              SELECT MAX(h2.orden_de_transicion)
              FROM Historial_Cambios_de_Estado h2
              WHERE h2.id_donacion_independiente = d.id
          )
        ORDER BY h.fecha_de_transicion DESC
        """)
        .setMaxResults(limite)
        .getResultList();
  }
  public Map<EstadoDonacion, Long> contarDonacionesPorEstadoGlobal() {
    String jpql = "SELECT d.estadoActual, COUNT(d) " +
        "FROM DonacionIndependiente d " +
        "WHERE d.estaEliminado = false " +
        "GROUP BY d.estadoActual";

    List<Object[]> resultados = entityManager()
        .createQuery(jpql, Object[].class)
        .getResultList();

    Map<EstadoDonacion, Long> conteos = new HashMap<>();
    for (Object[] fila : resultados) {
      conteos.put((EstadoDonacion) fila[0], (Long) fila[1]);
    }
    return conteos;
  }

  // admin/dashboard
  public long contarDonacionesTotales() {
    String jpql = "SELECT COUNT(d) FROM DonacionIndependiente d WHERE d.estaEliminado = false";
    return entityManager()
        .createQuery(jpql, Long.class)
        .getSingleResult();
  }

  private static final String EN_DEPOSITO_NO_ELIMINADAS =
      "d.estadoActual = :estado AND d.estaEliminado = false";

  public Pagina<DonacionIndependiente> getDonacionesEnDepositoConDetalles(int numero, int tamanio) {
    EntityManager em = entityManager();

    long total = em.createQuery(
            "SELECT COUNT(d) FROM DonacionIndependiente d WHERE " + EN_DEPOSITO_NO_ELIMINADAS, Long.class)
        .setParameter("estado", EstadoDonacion.EN_DEPOSITO)
        .getSingleResult();
    int totalPaginas = (int) Math.max(1, Math.ceil(total / (double) tamanio));
    int actual = Math.min(Math.max(numero, 1), totalPaginas);

    List<DonacionIndependiente> items = em.createQuery("""
          SELECT d FROM DonacionIndependiente d
          JOIN FETCH d.item i
          JOIN FETCH i.bien b
          JOIN FETCH b.subCategoria sc
          LEFT JOIN FETCH d.donante don
          WHERE %s
          ORDER BY d.id DESC
          """.formatted(EN_DEPOSITO_NO_ELIMINADAS), DonacionIndependiente.class)
        .setParameter("estado", EstadoDonacion.EN_DEPOSITO)
        .setFirstResult((actual - 1) * tamanio)
        .setMaxResults(tamanio)
        .getResultList();

    return new Pagina<>(items, actual, totalPaginas, total);
  }
}
