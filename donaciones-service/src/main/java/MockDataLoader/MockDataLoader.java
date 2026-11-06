package MockDataLoader;

import Injector.Injector;
import Repositories.Donantes.DonantesRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utils.FabricaDeDatos;

public class MockDataLoader implements WithSimplePersistenceUnit {
  public static void cargar(Injector injector) {
    DonantesRepository donantesRepository = DonantesRepository.getInstance();


    new MockDataLoader().withTransaction(() -> {
      donantesRepository.registrar(FabricaDeDatos.unDonanteDePrueba());
    });
  }
}
