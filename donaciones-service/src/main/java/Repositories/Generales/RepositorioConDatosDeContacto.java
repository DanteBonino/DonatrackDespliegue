package Repositories.Generales;

import Contacto.DatosDeContacto;
import Notificable.ConContacto;
import Persistible.Persistible;
import java.util.Optional;

public interface RepositorioConDatosDeContacto<T extends Persistible & ConContacto> extends RepositorioBasico<T> {
  default Optional<DatosDeContacto> obtenerDatosDeContactoDe(String idDeDominio) {
    return obtenerSiExistePorIdDominio(idDeDominio).map(ConContacto::getDatosDeContacto);
  }
}
