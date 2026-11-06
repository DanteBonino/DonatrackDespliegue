package Repositories.Beneficiarios;

import Controllers.EntidadesBenefeciarias.DatosEntidadesBeneficiarias;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ExceptionHandler.EntidadYaExisteException;
import java.util.List;

public interface IBeneficiariosRepository {
  List<EntidadBeneficiaria> getInstancias();

  EntidadBeneficiaria findById(String id);

  void registrar(EntidadBeneficiaria nuevaEntidad);

  void actualizarInstancia(String id, DatosEntidadesBeneficiarias nuevosDatos);

  void eliminarInstancia(String id);

}
