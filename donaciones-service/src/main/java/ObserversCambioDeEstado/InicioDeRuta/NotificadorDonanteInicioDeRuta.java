package ObserversCambioDeEstado.InicioDeRuta;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.ConNotificacionADonante;

public class NotificadorDonanteInicioDeRuta extends NotificadorInicioDeRuta implements ConNotificacionADonante {

  public NotificadorDonanteInicioDeRuta(
      GeneradorLinkDeTrackeoEntrega generadorLinkDeTrackeoEntrega
  ) {
    super(generadorLinkDeTrackeoEntrega);
  }

  @Override
  public String mensaje(String donacionId) {
    return "Tu donacion " + donacionId + "comenzo a viajar, acceda al siguiente link para seguir tu donacion: ";
  }


  @Override
  public ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return ConNotificacionADonante.super.receptor(donacion, entidad);
  }
}


