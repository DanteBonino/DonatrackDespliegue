package domain.eventos;

public interface EntregaObserver {
  void anteEntregaIniciada(EntregaIniciadaEvent evento);
  void anteEntregaCompletada(EntregaCompletadaEvent evento);
  void anteIncidenciaLogistica(IncidenciaLogisticaEvent evento);
  void anteEntregaReprogramada(EntregaReprogramadaEvent evento);
}
