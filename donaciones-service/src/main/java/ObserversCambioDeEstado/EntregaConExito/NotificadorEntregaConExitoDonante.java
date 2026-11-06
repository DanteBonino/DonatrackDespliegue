package ObserversCambioDeEstado.EntregaConExito;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionADonante;

public class NotificadorEntregaConExitoDonante extends NotificadorEntregaConExito implements ConNotificacionADonante {

  @Override
  public String inicioMensaje(String idDonacion) {
    return "Tu donacion "+ idDonacion + " fue entregada el";
  }

  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionADonante.super.receptor(donacion, entidad);
  }
}
