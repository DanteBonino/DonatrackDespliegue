package Repositories.Notificaciones;

import Notificaciones.Notificacion;
import java.util.List;

public interface INotificacionesRepository {
  void registrar(Notificacion notificacion);

  List<Notificacion> notificacionesEnviables();

  List<Notificacion> getInstancias();
}
