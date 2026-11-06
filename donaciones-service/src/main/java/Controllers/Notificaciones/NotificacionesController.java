package Controllers.Notificaciones;

import Controllers.ConOperacionesBasicasGet;
import Notificaciones.Notificacion;
import Repositories.Generales.RepositorioBasico;
import Repositories.Notificaciones.NotificacionesRepository;
import java.util.function.Function;

public class NotificacionesController implements ConOperacionesBasicasGet<Notificacion, NotificacionResponseDto> {
  @Override
  public Function<Notificacion, NotificacionResponseDto> conversorARespuesta() {
    return NotificacionResponseDto::desde;
  }

  @Override
  public RepositorioBasico<Notificacion> repositorioPrincipal() {
    return NotificacionesRepository.getInstance();
  }
}