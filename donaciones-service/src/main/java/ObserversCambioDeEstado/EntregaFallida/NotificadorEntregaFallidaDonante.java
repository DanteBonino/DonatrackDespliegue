package ObserversCambioDeEstado.EntregaFallida;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionADonante;

public class NotificadorEntregaFallidaDonante extends NotificadorEntregaFallida implements ConNotificacionADonante {

  @Override
  public String inicioDelMensaje() {
    return "Tu donacion id: ";
  }


  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionADonante.super.receptor(donacion, entidad);
  }
}
