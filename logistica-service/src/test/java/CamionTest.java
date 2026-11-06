//import java.Camion;
//import java.ExceptionHandler.CamionNoDisponible;
//import java.ExceptionHandler.CamionSinRutaActiva;
//import java.Posicion;
//import java.Ruta;
import domain.Camion;
import domain.Entrega;
import domain.EstadoRuta;
import domain.Posicion;
import domain.Ruta;
import exceptions.CamionNoDisponibleException;
import exceptions.CamionSinRutaActivaException;
import exceptions.RutaInvalidaParaAsignarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
import utility.FabricaDeDatos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Tests de Camion")
public class CamionTest {
  private Camion camion;
  private Ruta rutaQueSeDebeRecorrerHoy;
  private Ruta otraRutaQueSeDebeRecorrerHoy;
  private Ruta rutaQueSeDebeRecorrerHoyConUnaEntrega;
  private Ruta rutaVieja;
  private Ruta rutaNoPlanificada;
  private Ruta rutaCon44Entregas;
  private Ruta rutaCon44EntregasCompletadas;
  private Entrega entrega;

  @BeforeEach
  void setUp() {
    camion = new Camion(
        "ABC123",
        10.0,
        3.0,
        5000.0,
        new Posicion(-34.59855526311623, -58.42013582713193));
    rutaQueSeDebeRecorrerHoy = new Ruta( LocalDate.now());
    otraRutaQueSeDebeRecorrerHoy = new Ruta( LocalDate.now());
    rutaQueSeDebeRecorrerHoyConUnaEntrega = new Ruta(LocalDate.now());
    rutaVieja = mock(Ruta.class);
    rutaNoPlanificada = mock(Ruta.class);
    rutaCon44Entregas = mock(Ruta.class);
    rutaCon44EntregasCompletadas = mock(Ruta.class);
    entrega = FabricaDeDatos.nuevaEntrega( "DON-1", "BEN-1");
    rutaQueSeDebeRecorrerHoyConUnaEntrega.agregarEntrega(entrega);
  }

  @Test
  void unCamionSinRutaAsignadaEstaDisponible() {
    assertTrue(camion.estaDisponible());
  }

  @Test
  void noSePuedeAsignarUnaRutaQueCuyaFechaDeRecorridaYaOcurrio() {
    when(rutaVieja.getFechaDeReparto()).thenReturn(LocalDate.of(2000, 8, 8));
    assertThrows(RutaInvalidaParaAsignarException.class, () -> {
      camion.asignarRuta(rutaVieja);
    });
  }

  @Test
  void noSePuedeAsignarUnaRutaSiNoEstaPlanificada() {
    when(rutaNoPlanificada.estaPlanificada()).thenReturn(false);
    assertThrows(RutaInvalidaParaAsignarException.class, () -> {
      camion.asignarRuta(rutaNoPlanificada);
    });
  }

  @Test
  void cuandoSeAsignaUnaRutaYLaRutaDebeRecorrerseHoyElCamionPasaATenerUnaRutaAsignada() {
    camion.asignarRuta(rutaQueSeDebeRecorrerHoy);
    assertTrue(camion.tieneRutaAsignada());
  }

  @Test
  void unCamionConUnaRutaAsignadaNoEstáDisponible() {
    camion.asignarRuta(rutaQueSeDebeRecorrerHoy);
    assertFalse(camion.estaDisponible());
  }

  @Test
  void unCamionConUnaRutaEnCursoNoEstaDisponible() {
    camion.asignarRuta(rutaQueSeDebeRecorrerHoyConUnaEntrega);
    camion.iniciarRuta();
    assertFalse(camion.estaDisponible());
  }

  @Test
  void noSePuedeActualizarPosicionSiElCamionEstaDisponible() {
    Posicion nuevaPos = new Posicion(-34.6, -58.4);

    // El camión no tiene ruta, por lo tanto no debería aceptar tracking
    assertThrows(CamionSinRutaActivaException.class, () -> {
      camion.actualizarPosicion(nuevaPos);
    });
  }

  @Test
  void noSePuedeAsignarUnaRutaSiTieneUnaAsignadaEnLaFechaQueSeDebeRecorrerLaNuevaRuta() {

    camion.asignarRuta(rutaQueSeDebeRecorrerHoy); // Primera asignación OK

    // Segunda asignación debe fallar
    assertThrows(CamionNoDisponibleException.class, () -> {
      camion.asignarRuta(otraRutaQueSeDebeRecorrerHoy);
    });
  }

  @Nested
  @DisplayName("Finalizacion")
  class FinalizacionDeRutaTest {

    private Camion camionSinRutaAsignada;

    @BeforeEach
    void setUp() {
      camionSinRutaAsignada = new Camion("4040", 10, 10, 10, new Posicion(80, 10));
      camion.asignarRuta(rutaQueSeDebeRecorrerHoyConUnaEntrega);
      camion.iniciarRuta();
      entrega.confirmarRecepcion("UnComprobante", camion.getPatente());
      camion.finalizarRuta();
    }

    @Test
    void noSePuedeFinalizarUnaRutaSiNoSeCuentaConUnaRutaAsignada() {
      assertThrows(CamionSinRutaActivaException.class, () -> {
        camionSinRutaAsignada.finalizarRuta();
      });
    }

    @Test
    void unCamionEstaDisponibleSiLaRutaAsignadaParaElDiaDeLaFechaEstaFinalizada() {
      assertTrue(camion.estaDisponible());
    }

  }


  @Test
  void noSePuedeIniciarUnaRutaSiNoSeCuentaConUnaRutaAsignada() {
    assertThrows(CamionSinRutaActivaException.class, () -> {
      camion.iniciarRuta();
    });
  }

  @Test
  void elTotalDeEntregasDeLaRutaAsignadaEs0SiNoHayRutaAsignada() {
    assertEquals(0, camion.totalDeEntregasDeRutaAsignada());
  }

  @Test
  void elTotalDeEntregasEsIgualAlTotalDeEntregasDeLaRutaAsignada() {
    when(rutaCon44Entregas.getTotalDeEntregas()).thenReturn(44);
    when(rutaCon44Entregas.estaPlanificada()).thenReturn(true);
    when(rutaCon44Entregas.seVencio()).thenReturn(false);
    when(rutaCon44Entregas.estaAsignadaParaHoy()).thenReturn(true);
    camion.asignarRuta(rutaCon44Entregas);
    assertEquals(44, camion.totalDeEntregasDeRutaAsignada());
  }

  @Test
  void elTotalDeEntregasCompletadasDeLaRutaAsignadaEs0SiNoHayRutaAsignada() {
    assertEquals(0, camion.totalDeEntregasDeRutaAsignada());
  }

  @Test
  void elTotalDeEntregasCompletadasEsIgualAlTotalDeEntregasCompletadasDeLaRutaAsignada() {
    when(rutaCon44EntregasCompletadas.getTotalDeEntregasCompletadas()).thenReturn(44);
    when(rutaCon44EntregasCompletadas.estaPlanificada()).thenReturn(true);
    when(rutaCon44EntregasCompletadas.seVencio()).thenReturn(false);
    when(rutaCon44EntregasCompletadas.estaAsignadaParaHoy()).thenReturn(true);
    camion.asignarRuta(rutaCon44EntregasCompletadas);
    assertEquals(44, camion.totalDeEntregasCompletadasDeLaRutaAsignada());
  }

  @Test
  void elEstadoDeLaRutaEsDisponibleSiNoHayRutaAsignada() {
    assertEquals(EstadoRuta.DISPONIBLE, camion.getEstadoRutaAsignada());
  }

  @Test
  void elEstadoDeLaRutaEsElDeLaRutaAsignadaSiHayRutaAsignada() {
    camion.asignarRuta(rutaQueSeDebeRecorrerHoy);
    assertEquals(rutaQueSeDebeRecorrerHoy.getEstado(), camion.getEstadoRutaAsignada());
  }

  @Test
  void elNombreDeLaRutaAsignadaEsNullSiNoHayRutaAsignada() {
    assertNull(camion.getNombreRutaAsignada());
  }

  @Test
  void elNombreDeLaRutaEsElDeLaRutaAsignadaSiHayRutaAsignada() {
    camion.asignarRuta(rutaQueSeDebeRecorrerHoy);
    assertEquals(rutaQueSeDebeRecorrerHoy.getNombre(), camion.getNombreRutaAsignada());
  }

}