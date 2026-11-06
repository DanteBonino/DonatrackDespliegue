package Repositories.Generales;

import Actualizable.Actualizable;
import Eliminable.Eliminable;
import Persistible.Persistible;

public interface RepositorioDeActualizable<T extends Persistible & Actualizable<DatosDeActualizacion>, DatosDeActualizacion> extends RepositorioBasico<T> {
  default void actualizarInstancia(String id, DatosDeActualizacion datos) {
    T almacenado = findById(id);
    validarActualizacion(almacenado, datos);
    almacenado.refrescarParcial(datos);
  }

  void validarActualizacion(T instancia, DatosDeActualizacion datos);
}