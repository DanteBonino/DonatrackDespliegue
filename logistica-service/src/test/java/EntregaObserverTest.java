import domain.Entrega;
import domain.EstadoEntrega;
import domain.eventos.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utility.FabricaDeDatos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests de Observer de Entregas")
public class EntregaObserverTest {

  private EntregaPublicador publicador;
  private EntregaObserver observerMock;
  private Entrega entrega;

  @BeforeEach
  void setUp() {
    publicador = new EntregaPublicador();
    observerMock = mock(EntregaObserver.class);
    publicador.suscribir(observerMock);

    // Usamos el constructor con ID fijo para poder assertear el evento fácilmente
    entrega = FabricaDeDatos.nuevaEntregaConPublicador("DON-123", "ENT-BEN-01", publicador);
  }

  @Test
  @DisplayName("Al iniciar viaje se publica EntregaIniciadaEvent con los IDs correctos")
  void alIniciarViajeSePublicaElEventoCorrecto() {
    entrega.iniciarViaje();

    verify(observerMock).anteEntregaIniciada(argThat(evento ->
        evento.idEntrega().equals(entrega.getNombre()) &&
            evento.idDonacion().equals("DON-123")
    ));
  }

  @Test
  @DisplayName("Al confirmar recepción se publica EntregaCompletadaEvent con el comprobante")
  void alConfirmarRecepcionSePublicaElEventoCorrecto() {
    entrega.iniciarViaje(); // Primero debe estar EN_CAMINO
    entrega.confirmarRecepcion("foto.jpg", "PAT-TEST");

    verify(observerMock).anteEntregaCompletada(argThat(evento ->
        evento.idEntrega().equals(entrega.getNombre()) &&
            evento.idDonacion().equals("DON-123") &&
            evento.comprobanteFoto().equals("foto.jpg") &&
            evento.patenteCamion().equals("PAT-TEST")
    ));
  }

  @Test
  @DisplayName("Al reportar incidencia se publica IncidenciaLogisticaEvent con el motivo")
  void alReportarIncidenciaSePublicaElEventoCorrecto() {
    entrega.iniciarViaje(); // Primero debe estar EN_CAMINO
    entrega.reportarIncidencia("Entidad cerrada");

    verify(observerMock).anteIncidenciaLogistica(argThat(evento ->
        evento.idEntrega().equals(entrega.getNombre()) &&
            evento.idDonacion().equals("DON-123") &&
            evento.motivoNoRecepcion().equals("Entidad cerrada")
    ));
  }

  @Test
  @DisplayName("Si la entrega no tiene publicador, no falla al cambiar de estado")
  void siNoTienePublicadorNoFalla() {
    Entrega entregaSinPublicador = FabricaDeDatos.nuevaEntrega( "DON-456", "ENT-BEN-02");
    // No le inyectamos ningún publicador

    assertDoesNotThrow(entregaSinPublicador::iniciarViaje);
    Assertions.assertEquals(EstadoEntrega.EN_CAMINO, entregaSinPublicador.getEstado());
  }

  @Test
  @DisplayName("Se pueden suscribir múltiples observers y todos son notificados")
  void multiplesObserversSonNotificados() {
    EntregaObserver observer2 = mock(EntregaObserver.class);
    EntregaObserver observer3 = mock(EntregaObserver.class);

    publicador.suscribir(observer2);
    publicador.suscribir(observer3);

    entrega.iniciarViaje();

    verify(observerMock).anteEntregaIniciada(any());
    verify(observer2).anteEntregaIniciada(any());
    verify(observer3).anteEntregaIniciada(any());
  }

  @Test
  @DisplayName("Al reprogramar se publica EntregaReprogramadaEvent con los IDs correctos")
  void alReprogramarSePublicaElEventoCorrecto() {
    entrega.iniciarViaje();
    entrega.reportarIncidencia("Entidad cerrada");
    entrega.reprogramar();
    verify(observerMock).anteEntregaReprogramada(argThat(evento ->
        evento.idEntrega().equals(entrega.getNombre()) &&
            evento.idDonacion().equals("DON-123")
    ));
  }

  @Test
  @DisplayName("Al reprogramar sin publicador no falla")
  void alReprogramarSinPublicadorNoFalla() {
    Entrega entregaSinPublicador = FabricaDeDatos.nuevaEntrega("DON-456", "ENT-BEN-02");
    entregaSinPublicador.iniciarViaje();
    entregaSinPublicador.reportarIncidencia("Motivo");
    assertDoesNotThrow(entregaSinPublicador::reprogramar);
    assertEquals(EstadoEntrega.PLANIFICADA, entregaSinPublicador.getEstado());
  }
}