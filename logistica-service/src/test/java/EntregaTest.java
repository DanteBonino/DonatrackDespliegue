import domain.Entrega;
import domain.EstadoEntrega;
import exceptions.EstadoEntregaInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utility.FabricaDeDatos;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de Máquina de Estados de Entrega")
public class EntregaTest {

  private Entrega entrega;

  @BeforeEach
  void setUp() {
    // Usamos el constructor con ID explícito para tests predecibles
    entrega = FabricaDeDatos.nuevaEntrega("DON-123", "ENT-BEN-01");
  }

  @Test
  @DisplayName("Una entrega recién creada está PLANIFICADA y tiene su ID asignado")
  void unaEntregaRecienCreadaEstaPlanificada() {
    assertEquals(EstadoEntrega.PLANIFICADA, entrega.getEstado());
    assertEquals(entrega.getNombre(), entrega.getNombre());
    assertEquals("DON-123", entrega.getIdDonacion());
  }

  @Test
  @DisplayName("No se puede confirmar recepción si está PLANIFICADA")
  void noSePuedeConfirmarRecepcionSiEstaPlanificada() {
    assertThrows(EstadoEntregaInvalidoException.class, () -> {
      entrega.confirmarRecepcion("foto.jpg", "PAT-TEST");
    });
  }

  @Test
  @DisplayName("Al iniciar viaje pasa a EN_CAMINO")
  void alIniciarViajePasaAEnCamino() {
    entrega.iniciarViaje();
    assertEquals(EstadoEntrega.EN_CAMINO, entrega.getEstado());
  }

  @Test
  @DisplayName("Al confirmar recepción pasa a COMPLETADA y guarda el comprobante")
  void alConfirmarRecepcionPasaACompletada() {
    entrega.iniciarViaje();
    entrega.confirmarRecepcion("comprobante.jpg", "PAT-TEST");

    assertEquals(EstadoEntrega.COMPLETADA, entrega.getEstado());
    assertEquals("comprobante.jpg", entrega.getComprobanteFoto());
  }

  @Test
  @DisplayName("Al reportar incidencia pasa a INCIDENCIA y guarda el motivo")
  void alReportarIncidenciaPasaAIncidencia() {
    entrega.iniciarViaje();
    entrega.reportarIncidencia("Entidad cerrada");

    assertEquals(EstadoEntrega.INCIDENCIA, entrega.getEstado());
    assertEquals("Entidad cerrada", entrega.getMotivoNoRecepcion());
  }

  @Test
  @DisplayName("Al reprogramar vuelve a PLANIFICADA y limpia el motivo")
  void alReprogramarVuelveAPlanificada() {
    entrega.iniciarViaje();
    entrega.reportarIncidencia("Entidad cerrada");

    entrega.reprogramar();

    assertEquals(EstadoEntrega.PLANIFICADA, entrega.getEstado());
    assertNull(entrega.getMotivoNoRecepcion());
  }

  @Test
  @DisplayName("No se puede reprogramar si no hay una incidencia previa")
  void noSePuedeReprogramarSiNoHayIncidencia() {
    assertThrows(EstadoEntregaInvalidoException.class, () -> {
      entrega.reprogramar(); // Está PLANIFICADA, debería fallar
    });
  }
}