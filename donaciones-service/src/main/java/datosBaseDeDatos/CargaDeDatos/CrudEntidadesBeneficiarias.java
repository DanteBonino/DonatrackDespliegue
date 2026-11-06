package datosBaseDeDatos.CargaDeDatos;

import Repositories.Beneficiarios.BeneficiariosRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utils.FabricaDeEntidadesBeneficiarias;

public class CrudEntidadesBeneficiarias extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CrudEntidadesBeneficiarias().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    cargarEscenario();
  }

  public void cargarEscenario() {
    BeneficiariosRepository beneficiariosRepository = BeneficiariosRepository.getInstance();
    withTransaction(() -> {
      beneficiariosRepository.registrarInstancias(FabricaDeEntidadesBeneficiarias.crearEntidadesSinNecesidades(10));
    });
  }
}