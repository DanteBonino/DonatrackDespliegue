package ObserversCambioDeEstado.EntregaConExito;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionAEntidadBeneficiaria;

public class NotificadorEntregaConExitoEntidad extends NotificadorEntregaConExito implements ConNotificacionAEntidadBeneficiaria {

  @Override
  public String inicioMensaje(String idDonacion) {
    return "Recibiste la donacion "+ idDonacion +  " el";
  }


  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionAEntidadBeneficiaria.super.receptor(donacion, entidad);
  }
}
