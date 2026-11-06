package Repositories.Generales;

import ExceptionHandler.RecursoNoEncontradoException;
import Persistible.Persistible;
import Repositories.Generales.filtros.FiltroDeBusqueda;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import java.util.Optional;
import javax.persistence.TypedQuery;

public interface  RepositorioBasico<T extends Persistible> extends WithSimplePersistenceUnit {

  default void validarUnicidadDeNegocio(T nuevaEntidad) {
    //Por default no hay validación
  }

  default void registrar(T nuevaEntidad) {
    validarUnicidadDeNegocio(nuevaEntidad);
    persist(nuevaEntidad);
  }

  default List<T> getInstancias() {
    return createQuery("SELECT r FROM " + nombre() + " r WHERE 1=1" + condicionDeEliminacion(), clase())
        .getResultList();
  }

  private TypedQuery<T> queryPorAtributo(String nombre, Object valor) {
    return createQuery(
        "SELECT r FROM " + nombre() + " r WHERE r." + nombre + " = :" + nombre + condicionDeEliminacion(),
        clase()
    ).setParameter(nombre, valor);
  }

  default T obtenerUnoSegun(String nombre, Object valor) {
    return queryPorAtributo(nombre, valor)
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No existe " + clase().getSimpleName() + " con un atributo llamado " + nombre + " con el valor de " + valor
        ));
  }

  default Optional<T> obtenerUnoSiExisteSegun(String nombre, Object valor) {
    return queryPorAtributo(nombre, valor)
        .setMaxResults(1)
        .getResultStream()
        .findFirst();
  }

  default Optional<T> obtenerSiExistePorIdDominio(String idDominio) {
    return obtenerUnoSiExisteSegun("idDominio", idDominio);
  }

  default List<T> obtenerTodosSegun(String nombre, Object valor) {
    return queryPorAtributo(nombre, valor).getResultList();
  }

  default T findById(String id) {
    return obtenerUnoSegun("idDominio", id);
  }

  default T obtenerPorIdJPA(Long id) {
    T entidadBuscada = find(clase(), id);

    if(entidadBuscada == null) {
      throw new RecursoNoEncontradoException("La " + nombre() + " con identificador: " + id + " no existe");
    }
    return entidadBuscada;
  }

  default String condicionDeEliminacion() {
    return "";
  }


  default boolean existeSegun(String nombre, Object valor) {
    String nombreParametro = nombre.replace(".", "_");
    Long cantidad = createQuery(
        "SELECT COUNT(r) FROM " + clase().getSimpleName() + " r WHERE r." + nombre + " = :" + nombreParametro + condicionDeEliminacion(),
        Long.class
    )
        .setParameter(nombreParametro, valor)
        .getSingleResult();

    return cantidad > 0;
  }

  Class<T> clase();

  default String nombre() {
    return clase().getSimpleName();
  }

  default List<T> obtenerSegun(FiltroDeBusqueda filtro) {
    TypedQuery<T> query = createQuery(
        "SELECT r FROM " + nombre() + " r WHERE " + filtro.construirCadenaDeComparacion("r") + condicionDeEliminacion(),
        clase());
    filtro.aplicarParametros(query);
    return query.getResultList();
  }

  default void registrarInstancias(List<T> nuevasInstancias) {
    nuevasInstancias.forEach(this::registrar);
  }

  default int cantidadInstancias() {
    return getInstancias().size();
  }
}
