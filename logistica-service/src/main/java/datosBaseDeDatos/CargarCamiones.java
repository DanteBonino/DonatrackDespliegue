package datosBaseDeDatos;

import domain.Camion;
import domain.Posicion;
import injector.LogisticaInjector;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import utility.BaseDeDatos;

public class CargarCamiones implements WithSimplePersistenceUnit {
  private Posicion posicion;
  public static void main(String[] args) {
    BaseDeDatos.configurarDesdeEntorno();
    LogisticaInjector injector = new LogisticaInjector();
    new CargarCamiones(injector.getPosicionDeposito()).ejecutar();
  }

  public CargarCamiones(Posicion posicion) {
    this.posicion = posicion;
  }

  public void ejecutar() {
    List<String> patentes = List.of(
        "AAA111", "ABC123", "ABF456", "ACG789", "ADK012",
        "AEM345", "AFN678", "AGP901", "AHR234", "AJS567",
        "AKT890", "ALU123", "AMV456", "ANW789", "AOX012",
        "APY345", "AQZ678", "ARB901", "ASC234", "ATD567",
        "AUE890", "AVF123", "AWG456", "AXH789", "AYI012",
        "AZJ345", "BAK678", "BBL901", "BCM234", "BDN567",
        "BEO890", "BFP123", "BGQ456", "BHR789", "BIS012",
        "BJT345", "BKU678", "BLV901", "BMW234", "BNX567",
        "BOY890", "BPZ123", "BQA456", "BRB789", "BSC012",
        "BTD345", "BUE678", "BVF901", "BWG234", "BXH567"
    );
    withTransaction(() -> {
      patentes.forEach(patente -> {
        FlotaRepositoryConPersistencia.getInstance().registrar(camionEnDepositoConPatente(patente));
      });
    });
  }

  private Camion camionEnDepositoConPatente(String patente) {
    return new Camion(patente, 12.0, 3.5, 6000.0, posicion);
  }
}
