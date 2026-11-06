package ObserversCambioDeEstado.ConfirmarAsignacion;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionAEntidadBeneficiaria;

public class NotificadorDeEntidadBeneficiariaAnteAsignacion extends NotificadorAnteAsignacion implements ConNotificacionAEntidadBeneficiaria {

  @Override
  public String mensaje() {
    return "Se te asignó una donación";
  }


  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionAEntidadBeneficiaria.super.receptor(donacion, entidad);
  }
}
