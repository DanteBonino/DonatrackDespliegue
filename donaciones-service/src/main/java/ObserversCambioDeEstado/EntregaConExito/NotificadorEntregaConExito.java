package ObserversCambioDeEstado.EntregaConExito;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.InteresadoEnCambiosDeEstado;
import Notificable.ConContacto;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;

abstract public class NotificadorEntregaConExito implements InteresadoEnCambiosDeEstado {

  @Override
  public void anteEntregaConExito(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, ComprobanteDeEntrega comprobanteDeEntrega) {
    NotificacionesRepository.getInstance().registrar(
        new NotificacionPreferencial(this.inicioMensaje(donacionIndependiente.getIdDominio())
        + comprobanteDeEntrega.timestamp()
        + " por el camión " + comprobanteDeEntrega.patenteCamion(),
       this.receptor(donacionIndependiente, entidadBeneficiaria).getDatosDeContacto()
        )
    );
  }

  abstract String inicioMensaje(String idDonacion);

  abstract ConContacto receptor(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria);

}

//Yo buscaria una abstracción que unifique los 3 parámetros