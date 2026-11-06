package externo.donaciones;

import domain.eventos.EntregaCompletadaEvent;
import domain.eventos.EntregaIniciadaEvent;
import domain.eventos.EntregaReprogramadaEvent;
import domain.eventos.IncidenciaLogisticaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de SincronizadorDeEstadosObserver (Mapeo de Eventos)")
class SincronizadorDeEstadosObserverTest {

  @Mock
  private DonacionesServiceAdapter adapterMock;

  private SincronizadorDeEstadosObserver observer;

  @BeforeEach
  void setUp() {
    observer = new SincronizadorDeEstadosObserver(adapterMock);
  }

  @Test
  @DisplayName("Ante entrega iniciada, pasa el idEntidad al adapter")
  void anteEntregaIniciada_notificaInicio() {
    // (idEntrega, idDonacion, idEntidad)
    EntregaIniciadaEvent evento = new EntregaIniciadaEvent("ENT-1", "DON-1", "BEN-1");

    observer.anteEntregaIniciada(evento);

    verify(adapterMock).notificarInicioDeEntrega("ENT-1","DON-1", "BEN-1");
  }

  @Test
  @DisplayName("Ante entrega completada, pasa idEntidad y patenteCamion al adapter")
  void anteEntregaCompletada_notificaCompletada() {
    // (idEntrega, idDonacion, idEntidad, comprobanteFoto, patenteCamion)
    EntregaCompletadaEvent evento = new EntregaCompletadaEvent("ENT-2", "DON-2", "BEN-2", "foto.jpg", "ZZZ999");

    observer.anteEntregaCompletada(evento);

    verify(adapterMock).notificarEntregaCompletada("DON-2", "BEN-2", "foto.jpg", "ZZZ999");
  }

  @Test
  @DisplayName("Ante incidencia, pasa idEntidad y motivo al adapter")
  void anteIncidenciaLogistica_notificaIncidencia() {
    // (idEntrega, idDonacion, idEntidad, motivoNoRecepcion)
    IncidenciaLogisticaEvent evento = new IncidenciaLogisticaEvent("ENT-3", "DON-3", "BEN-3", "Lluvia");

    observer.anteIncidenciaLogistica(evento);

    verify(adapterMock).notificarIncidencia("DON-3", "BEN-3", "Lluvia");
  }

  @Test
  @DisplayName("Ante entrega reprogramada, pasa el idDonacion al adapter")
  void anteEntregaReprogramada_notificaReprogramacion() {
    EntregaReprogramadaEvent evento = new EntregaReprogramadaEvent("ENT-4", "DON-4");
    observer.anteEntregaReprogramada(evento);
    verify(adapterMock).notificarReprogramacion("DON-4");
  }
}