package ObserversCambioDeEstado.EntregaFallida;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionAEntidadBeneficiaria;

public class NotificadorEntregaFallidaEntidad extends NotificadorEntregaFallida implements ConNotificacionAEntidadBeneficiaria {

  @Override
  public String inicioDelMensaje(){
    return "La donacion id: ";
  }

  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionAEntidadBeneficiaria.super.receptor(donacion, entidad);
  }
}
