package ObserversCambioDeEstado;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;

public interface ConNotificacionAEntidadBeneficiaria {
  default ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad) {
    return entidad;
  }
}