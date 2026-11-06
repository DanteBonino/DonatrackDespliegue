package Repositories.Generales;

import Actualizable.Actualizable;
import ExceptionHandler.RecursoNoEncontradoException;
import Persistible.Persistible;
import java.util.ArrayList;
import java.util.List;

public abstract class Repository
    <TipoAlmacenado extends Persistible & Actualizable<DatosDeActualizacion>, DatosDeActualizacion> {

  private final List<TipoAlmacenado> almacenados = new ArrayList<>();

  public Repository(List<TipoAlmacenado> almacenadosIniciales) {
    almacenados.addAll(almacenadosIniciales);
  }

  //Create
  public void registrar(TipoAlmacenado instancia) {
    validarUnicidadDeNegocio(instancia);
    almacenados.add(instancia);
  }

  protected void validarUnicidadDeNegocio(TipoAlmacenado instancia) {
    //No hace nada por defecto
  }

  //GetAll
  public List<TipoAlmacenado> getInstancias() {
    return almacenados.stream().filter(this::estaVisible).toList();
  }

  protected boolean estaVisible(TipoAlmacenado instancia) {
    return true;
  }

  //GetOne
  public TipoAlmacenado findById(String id) {
    return almacenados.stream()
        .filter(a -> a.esLaMismaEntidad(id))
        .findFirst()
        .orElseThrow(() -> new RecursoNoEncontradoException(nombreDeEntidad() + " " + id + " no fue encontrado"));
  }

  //Delete
  public void eliminarInstancia(String id) {
    TipoAlmacenado almacenado = findById(id);
    eliminar(almacenado);
  }

  protected void eliminar(TipoAlmacenado instancia) {
    almacenados.remove(instancia); // hard delete por defecto
  }

  //Update[Patch]
  public void actualizarInstancia(String id, DatosDeActualizacion datos) {
    TipoAlmacenado almacenado = findById(id);
    validarActualizacion(almacenado, datos);
    almacenado.refrescarParcial(datos);
  }

  protected abstract void validarActualizacion(TipoAlmacenado almacenado, DatosDeActualizacion datos);
  protected abstract String nombreDeEntidad();
}