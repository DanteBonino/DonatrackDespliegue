import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import config.Configuracion;
import injector.LogisticaInjector;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class InjectorYConfigTest {
  @Test
  void sePuedeInstanciarElInjector() {
    assertThrows(IllegalStateException.class, LogisticaInjector::new);
  }

  @Test
  void sePuedeInstanciarConfig() {
    assertDoesNotThrow(Configuracion::new);
  }
  @Disabled("Pendiente de refactorización por cambio en la API")
  @Test
  void noSePuedeGetearLaLatitudDelDepositoPorQueNoHayArchivoDeConfiguracion() {
    Configuracion configuracion = new Configuracion();
    assertThrows(IllegalStateException.class, configuracion::getDepositoLatitud);
  }
  @Disabled("Pendiente de refactorización por cambio en la API")
  @Test
  void noSePuedeGetearLaLongitudDelDepositoSinArchivoDeConfiguracion() {
    Configuracion configuracion = new Configuracion();
    assertThrows(IllegalStateException.class, configuracion::getDepositoLatitud);
  }
  @Disabled("Pendiente de refactorización por cambio en la API")
  @Test
  void noSePuedesePuedeGetearLaDireccionDelDepositoSinArchivoDeConfiguracion() {
    Configuracion configuracion = new Configuracion();
    assertThrows(IllegalStateException.class,configuracion::getDireccionDeposito);
  }
  @Disabled("Pendiente de refactorización por cambio en la API")
  @Test
  void noSePuedeGetearLaUrlDeDonacionesServiceSinArchivoDeConfiguracion() {
    Configuracion configuracion = new Configuracion();
    assertThrows(IllegalStateException.class,configuracion::getUrlDonacionesService);
  }
  @Disabled("Pendiente de refactorización por cambio en la API")
  @Test
  void noSePuedeGetearLaUrlDelPlanificadorExternoSinArchivoDeConfiguracion() {
    Configuracion configuracion = new Configuracion();
    assertThrows(IllegalStateException.class, configuracion::getUrlPlanificadorExterno);
  }
}
