package domain.eventos;

import java.util.ArrayList;
import java.util.List;

/**
 * Publicador de eventos de entregas.
 * Mantiene la lista de observers suscritos y les notifica cuando ocurren
 * cambios de estado en a=las entregas.
 */
public class EntregaPublicador {

  private final List<EntregaObserver> observers = new ArrayList<>();

  public void suscribir(EntregaObserver observer) {
    this.observers.add(observer);
  }

  public void desuscribirTodos() {
    observers.clear();
  }

  public void publicarEntregaIniciada(EntregaIniciadaEvent evento) {
    observers.forEach(o -> o.anteEntregaIniciada(evento));
  }

  public void publicarEntregaCompletada(EntregaCompletadaEvent evento) {
    observers.forEach(o -> o.anteEntregaCompletada(evento));
  }

  public void publicarIncidenciaLogistica(IncidenciaLogisticaEvent evento) {
    observers.forEach(o -> o.anteIncidenciaLogistica(evento));
  }

  public void publicarEntregaReprogramada(EntregaReprogramadaEvent evento) {
    observers.forEach(o -> o.anteEntregaReprogramada(evento));
  }
}
