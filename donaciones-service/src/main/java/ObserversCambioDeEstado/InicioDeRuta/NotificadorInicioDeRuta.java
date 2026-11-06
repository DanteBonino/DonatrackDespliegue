package ObserversCambioDeEstado.InicioDeRuta;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Notificable.ConContacto;
import ObserversCambioDeEstado.InteresadoEnCambiosDeEstado;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;

abstract public class NotificadorInicioDeRuta implements InteresadoEnCambiosDeEstado {
  GeneradorLinkDeTrackeoEntrega generadorLinkDeTrackeoEntrega;

  public NotificadorInicioDeRuta(
      GeneradorLinkDeTrackeoEntrega generadorLinkDeTrackeoEntrega) {
    this.generadorLinkDeTrackeoEntrega = generadorLinkDeTrackeoEntrega;
  }

  @Override
  public void anteInicioDeRuta(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, String idEntrega) {
    String url = generadorLinkDeTrackeoEntrega.generarLink(idEntrega);
    NotificacionesRepository.getInstance().registrar(new NotificacionPreferencial(this.mensaje(donacionIndependiente.getIdDominio()) + url, receptor(donacionIndependiente, entidadBeneficiaria).getDatosDeContacto()));
  }

  abstract public String mensaje(String donacionId);
  abstract ConContacto receptor(DonacionIndependiente donacion, EntidadBeneficiaria entidad);
}
