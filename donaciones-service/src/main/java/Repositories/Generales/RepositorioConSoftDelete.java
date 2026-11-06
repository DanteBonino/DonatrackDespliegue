package Repositories.Generales;

import Eliminable.Eliminable;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ExceptionHandler.RecursoNoEncontradoException;
import Persistible.Persistible;
import java.util.List;

public interface RepositorioConSoftDelete<T extends Persistible & Eliminable> extends RepositorioBasico<T> {
  @Override
  default String condicionDeEliminacion() {
    return " AND r.estaEliminado = false";
  }

  default void eliminarInstancia(String id) {
    T almacenado = findById(id);
    almacenado.marcarComoEliminado();
  }
}
