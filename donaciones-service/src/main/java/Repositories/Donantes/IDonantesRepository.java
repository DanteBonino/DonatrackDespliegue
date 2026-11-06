package Repositories.Donantes;

import DTOs.DatosDonante;
import Donantes.Donante;
import ExceptionHandler.DatosInvalidosException;
import java.util.List;

public interface IDonantesRepository {
  default Integer cantidadDeDonantes(){
    return this.getInstancias().size();
  }

  List<Donante> getInstancias();

  List<Donante> getInactivos();

  void registrar(Donante instancia);

  Donante findById(String id);

  void eliminarInstancia(String id);

  void actualizarInstancia(String id, DatosDonante datos);

  default void validarActualizacion(Donante donante, DatosDonante datos) {
    boolean tipoCoincide = datos.getTipoPersona() == null
        || donante.getTipoPersona().equals(datos.getTipoPersona());
    System.out.println("Estoy en validar actualizacion: " + tipoCoincide);
    if (!tipoCoincide) {
      throw new DatosInvalidosException("No se puede cambiar el tipo de persona de un donante existente");
    }
  }
}
