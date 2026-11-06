package datosBaseDeDatos.CargaDeDatos;

import static utils.FabricaDeDatos.unDonanteDePrueba;

import Repositories.Donantes.DonantesRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utils.FabricaDeDatos;

public class CargarLoteConDonaciones extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CargarLoteConDonaciones().ejecutar();
  }

  @Override
  public void ejecutar() {
    super.ejecutar();
    cargar();
  }

  public void cargar() {
    withTransaction(() -> {
      DonantesRepository.getInstance().registrar(unDonanteDePrueba());
    });
  }
}
