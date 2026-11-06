package InactividadDeDonante;


import Donantes.Donante;
import EventosYNotificacion.PublicadorObservable;



public class PublicadorDeInactividad extends PublicadorObservable<InteresadoEnInactividad> {

  public void publicarInactividad(Donante unDonante) {
    publicarA(interesado -> interesado.anteInactividad(unDonante));
  }
}