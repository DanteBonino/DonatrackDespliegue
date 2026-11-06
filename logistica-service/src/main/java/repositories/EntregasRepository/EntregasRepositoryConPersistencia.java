package repositories.EntregasRepository;

import domain.Camion;
import domain.Entrega;
import exceptions.RecursoNoEncontradoException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import repositories.RepositorioBasico;
import repositories.RutasRepository.RutasRepositoryConPersistencia;

public class EntregasRepositoryConPersistencia implements RepositorioBasico<Entrega>, EntregasRepository {
  private static EntregasRepositoryConPersistencia INSTANCE = new EntregasRepositoryConPersistencia();

  public static EntregasRepositoryConPersistencia getInstance() {
    return INSTANCE;
  }

  private EntregasRepositoryConPersistencia() {

  }

  public Entrega obtenerPorId(Long id) {
      return RepositorioBasico.super.obtenerPorId(id);
  }

  public Entrega obtenerPorDonacionId(String donacionId){
    return obtenerUnoSegun("idDonacion", donacionId);
  }

  public Entrega obtenerPorNombre(String nombreEntrega) {
    return obtenerUnoSegun("nombre", nombreEntrega);
  }

  public Class<Entrega> clase() {
    return Entrega.class;
  }
}
