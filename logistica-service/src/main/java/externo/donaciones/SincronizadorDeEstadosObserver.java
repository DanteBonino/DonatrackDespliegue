package externo.donaciones;

import domain.eventos.*;

public class SincronizadorDeEstadosObserver implements EntregaObserver {

  private final DonacionesServiceAdapter adapter;

  public SincronizadorDeEstadosObserver(DonacionesServiceAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public void anteEntregaIniciada(EntregaIniciadaEvent evento) {
    adapter.notificarInicioDeEntrega(
        evento.idEntrega(),
        evento.idDonacion(),
        evento.idEntidadBeneficiaria());
  }

  @Override
  public void anteEntregaCompletada(EntregaCompletadaEvent evento) {
    adapter.notificarEntregaCompletada(
        evento.idDonacion(),
        evento.idEntidadBeneficiaria(),
        evento.comprobanteFoto(),
        evento.patenteCamion());
  }

  @Override
  public void anteIncidenciaLogistica(IncidenciaLogisticaEvent evento) {
  adapter.notificarIncidencia(
      evento.idDonacion(),
      evento.idEntidadBeneficiaria(),
      evento.motivoNoRecepcion());
  }

  @Override
  public void anteEntregaReprogramada(EntregaReprogramadaEvent evento) {
    adapter.notificarReprogramacion(evento.idDonacion());
  }
}
