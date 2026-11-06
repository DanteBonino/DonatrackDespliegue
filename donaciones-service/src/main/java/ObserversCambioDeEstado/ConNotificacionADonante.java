package ObserversCambioDeEstado;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;

public interface ConNotificacionADonante {
  default ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return donacion.getDonante();
  }
}
