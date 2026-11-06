package InactividadDeDonante;


import Donantes.Donante;
import EventosYNotificacion.Interesado;

public interface InteresadoEnInactividad extends Interesado {
  void anteInactividad(Donante unDonante);
}
