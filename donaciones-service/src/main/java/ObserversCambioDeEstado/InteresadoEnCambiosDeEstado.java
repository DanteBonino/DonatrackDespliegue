package ObserversCambioDeEstado;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EventosYNotificacion.Interesado;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public interface InteresadoEnCambiosDeEstado extends Interesado {
  default void anteAsignacion(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria){

  }
  default void anteEntregaConExito(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, ComprobanteDeEntrega comprobante){

  }
  default void anteEntregaFallida(String justificacion, EntidadBeneficiaria entidadBeneficiaria, DonacionIndependiente donacionIndependiente){

  }
  default void anteInicioDeRuta(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, String idEntrega){

  }
}
