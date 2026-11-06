package repositories.FlotaRepository;

import domain.Camion;
import domain.EstadoRuta;
import domain.Ruta;
import exceptions.RecursoNoEncontradoException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import repositories.RepositorioBasico;

public class FlotaRepositoryConPersistencia implements FlotaRepository, RepositorioBasico<Camion> {
  private static FlotaRepositoryConPersistencia INSTANCE = new FlotaRepositoryConPersistencia();

  public static FlotaRepositoryConPersistencia getInstance() {
    return INSTANCE;
  }

  private FlotaRepositoryConPersistencia() {

  }

  public void registrar(Camion camion) {
    RepositorioBasico.super.registrar(camion);
  }

  public List<Camion> obtenerTodos() {
    return RepositorioBasico.super.obtenerTodas();
  }

  public List<Camion> obtenerEnCurso() {
    return createQuery(
        "SELECT c FROM Camion c JOIN c.rutasAsignadas r WHERE r.estado = :estado",
        Camion.class
    )
        .setParameter("estado", EstadoRuta.EN_CURSO)
        .getResultList();
  }

  public Camion buscarPorRuta(Ruta unaRuta) {
    return createQuery(
        "SELECT c FROM Camion c WHERE :ruta MEMBER OF c.rutasAsignadas",
        Camion.class
    ).setParameter("ruta", unaRuta)
        .getResultStream()
        .findAny()
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe camion asociado a la Ruta: " + unaRuta.getId()));
  }

  public Camion buscarPorPatente(String patente) {
    return obtenerUnoSegun("patente", patente);
  }

  public List<Camion> obtenerDisponibles() {
    return createQuery(
        "SELECT c FROM Camion c WHERE NOT EXISTS (" +
            "  SELECT r FROM c.rutasAsignadas r " +
            "  WHERE r.estado = domain.EstadoRuta.EN_CURSO " +
            "     OR (r.estado = domain.EstadoRuta.PLANIFICADA AND r.fechaDeReparto = CURRENT_DATE)" +
            ")", Camion.class
    ).getResultList();
  }

  @Override
  public Camion obtenerPorId(Long id) {
    return RepositorioBasico.super.obtenerPorId(id);
  }

  @Override
  public Class<Camion> clase() {
    return Camion.class;
  }


}
