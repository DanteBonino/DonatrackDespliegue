package ObserversCambioDeEstado.EntregaFallida;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.InteresadoEnCambiosDeEstado;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;

abstract public class NotificadorEntregaFallida implements InteresadoEnCambiosDeEstado {

  public void anteEntregaFallida(String justificacion, EntidadBeneficiaria entidadBeneficiaria, DonacionIndependiente donacionIndependiente) {
    NotificacionesRepository.getInstance().registrar(
        new NotificacionPreferencial(this.inicioDelMensaje() + donacionIndependiente.getIdDominio() +
        "no se pudo entregar, motivo: " + justificacion, receptor(donacionIndependiente, entidadBeneficiaria).getDatosDeContacto())
    );
  }

  abstract public String inicioDelMensaje();
  abstract ConContacto receptor(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria);
}
