package repositories.RutasRepository;

import domain.Camion;
import domain.EstadoRuta;
import domain.Ruta;
import exceptions.RecursoNoEncontradoException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDate;
import java.util.List;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RepositorioBasico;

public class RutasRepositoryConPersistencia implements RepositorioBasico<Ruta>, RutasRepository {
  private static RutasRepositoryConPersistencia INSTANCE = new RutasRepositoryConPersistencia();

  public static RutasRepositoryConPersistencia getInstance() {
    return INSTANCE;
  }

  private RutasRepositoryConPersistencia() {

  }

  public void registrar(Ruta ruta) {
    RepositorioBasico.super.registrar(ruta);
  }

  public List<Ruta> obtenerTodas() {
    return RepositorioBasico.super.obtenerTodas();
  }

  public List<Ruta> obtenerRutasEnCurso() {
    return obtenerTodosSegun("estado", EstadoRuta.EN_CURSO);
  }

  public List<Ruta> obtenerRutasDelDia(LocalDate unaFecha) {
    return obtenerTodosSegun("fechaDeReparto", unaFecha);
  }

  public Ruta buscarRutaPorEntregaId(Long idEntrega) {
    return createQuery(
        "SELECT r FROM Ruta r JOIN r.entregas e WHERE e.id = :idEntrega", Ruta.class
    ).setParameter("idEntrega", idEntrega)
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe Ruta asociada a la entrega: " + idEntrega));
  }

  @Override
  public Class<Ruta> clase() {
    return Ruta.class;
  }

}
