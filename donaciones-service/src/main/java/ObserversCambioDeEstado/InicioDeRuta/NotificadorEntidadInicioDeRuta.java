package ObserversCambioDeEstado.InicioDeRuta;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionAEntidadBeneficiaria;

public class NotificadorEntidadInicioDeRuta extends NotificadorInicioDeRuta implements ConNotificacionAEntidadBeneficiaria {

  public NotificadorEntidadInicioDeRuta(
      GeneradorLinkDeTrackeoEntrega generadorLinkDeTrackeoEntrega) {
    super(generadorLinkDeTrackeoEntrega);
  }

  @Override
  public String mensaje(String donacionId) {
    return "Te asignaron la donacion " + donacionId + ", acceda al siguiente link para seguirla: ";
  }


  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionAEntidadBeneficiaria.super.receptor(donacion, entidad);
  }
}
