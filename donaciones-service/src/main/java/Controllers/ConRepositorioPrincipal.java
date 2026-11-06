package Controllers;

import Persistible.Persistible;
import Repositories.Generales.RepositorioBasico;

public interface ConRepositorioPrincipal <T extends Persistible> {
  RepositorioBasico<T> repositorioPrincipal();
}
