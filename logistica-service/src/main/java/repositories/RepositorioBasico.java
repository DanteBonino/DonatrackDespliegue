package repositories;

import domain.persistencia.EntidadBase;
import exceptions.RecursoNoEncontradoException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public interface  RepositorioBasico<T extends EntidadBase> extends WithSimplePersistenceUnit {

  default void registrar(T nuevaEntidad) {
      persist(nuevaEntidad);
  }

  default List<T> obtenerTodas() {
    return createQuery("from " + clase().getSimpleName(), clase())
        .getResultList();
  }

  default List<T> obtenerTodosSegun(String nombre, Object valor) {
    return createQuery("SELECT r FROM " + nombre() + " r WHERE r." + nombre + " = :" + nombre, clase())
        .setParameter(nombre, valor)
        .getResultList();
  }

  default T obtenerPorId(Long id) {
    T entidadBuscada = find(clase(), id);

    if(entidadBuscada == null) {
      throw new RecursoNoEncontradoException("La " + nombre() + " con identificador: " + id + " no existe");
    }
    return entidadBuscada;
  }

  default T obtenerUnoSegun(String nombre, Object valor) {
    return createQuery("SELECT r FROM " + clase().getSimpleName() + " r WHERE r." + nombre + " = :" + nombre, clase())
        .setParameter(nombre, valor)
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No existe " + clase().getSimpleName() + " con un atributo llamado " + nombre + " con el valor de " + valor
        ));
  }

  Class<T> clase();

  default String nombre() {
    return clase().getSimpleName();
  }
}
