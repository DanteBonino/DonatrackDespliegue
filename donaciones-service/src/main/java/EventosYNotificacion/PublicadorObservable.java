package EventosYNotificacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


public class PublicadorObservable <I extends Interesado>{
  private List<I> consumidores;


  public PublicadorObservable() {
    this.consumidores = new ArrayList<>();
  }

  public void suscribir(I ... consumidor) {
    Collections.addAll(this.consumidores, consumidor);
  }

  public void desuscribir(I consumidor) {
    this.consumidores.remove(consumidor);
  }

  protected void publicarA(Consumer<I> accion) {
    this.consumidores.forEach(accion);
  }
}
