package ObserversCambioDeEstado.ConfirmarAsignacion;


import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionADonante;


public class NotificadorDeDonanteAnteAsignacion extends NotificadorAnteAsignacion implements ConNotificacionADonante {

  @Override
  public String mensaje() {
    return "Tu donación fue asignada";
  }


  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionADonante.super.receptor(donacion, entidad);
  }
}
