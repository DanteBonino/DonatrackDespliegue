import domain.*;
import exceptions.CamionNoAsignadoIntentaAccionarSobreLaRutaException;
import exceptions.EstadoRutaInvalidoException;
import exceptions.RutaSinCamionAsignadoException;
import exceptions.RutaSinEntregasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests de Ruta")
public class RutaTest {

  private Ruta ruta;
  private Camion camion;

  @BeforeEach
  void setUp() {
    ruta = new Ruta( LocalDate.now());
    camion = new Camion("ABC123", 10.0, 3.0,
        5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
  }

  @Test
  void noSePuedeIniciarUnaRutaSiElCambioQueSePasaComoParametroNoEsElQueLaTieneAsignada() {
    Entrega entregaMock = mock(Entrega.class);
    ruta.agregarEntrega(entregaMock);

    assertThrows(CamionNoAsignadoIntentaAccionarSobreLaRutaException.class, () -> {
      ruta.darInicio(camion);
    });
  }

  @Test
  void noSePuedeIniciarRutaSinEntregas() {
    camion.asignarRuta(ruta);

    assertThrows(RutaSinEntregasException.class, () -> {
      ruta.darInicio(camion);
    });
  }

  @Test
  void alIniciarRutaSeInicianTodasLasEntregas() {
    Entrega entrega1 = mock(Entrega.class);
    Entrega entrega2 = mock(Entrega.class);

    camion.asignarRuta(ruta);
    ruta.agregarEntrega(entrega1);
    ruta.agregarEntrega(entrega2);

    ruta.darInicio(camion);

    assertEquals(EstadoRuta.EN_CURSO, ruta.getEstado());

    // Verificamos que la ruta le pidió a cada entrega que inicie
    verify(entrega1).iniciarViaje();
    verify(entrega2).iniciarViaje();
  }

  @Test
  void noSePuedenAgregarEntregasSiLaRutaYaInicio() {
    Entrega entrega1 = mock(Entrega.class);
    camion.asignarRuta(ruta);
    ruta.agregarEntrega(entrega1);
    ruta.darInicio(camion); // La ruta ya está en curso

    Entrega entregaTardia = mock(Entrega.class);

    assertThrows(EstadoRutaInvalidoException.class, () -> {
      ruta.agregarEntrega(entregaTardia);
    });
  }
}