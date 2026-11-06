package ObserversCambioDeEstado.ConfirmarAsignacion;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.InteresadoEnCambiosDeEstado;
import Notificable.ConContacto;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;

abstract public class NotificadorAnteAsignacion implements InteresadoEnCambiosDeEstado {


  public void anteAsignacion(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
      NotificacionesRepository.getInstance().registrar(
          new NotificacionPreferencial(this.mensaje(), this.receptor(donacion, entidad).getDatosDeContacto())
      );
  }

  abstract public String mensaje();
  abstract protected ConContacto receptor(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidad);

}
