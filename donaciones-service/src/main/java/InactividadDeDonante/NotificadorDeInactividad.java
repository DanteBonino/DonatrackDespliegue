package InactividadDeDonante;


import Donantes.Donante;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;

public class NotificadorDeInactividad implements InteresadoEnInactividad {

  @Override
  public void anteInactividad(Donante unDonante) {
    NotificacionesRepository.getInstance().registrar(new NotificacionPreferencial("Te extrañamos", unDonante.getDatosDeContacto()));
  }
}
