package ObserversCambioDeEstado;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PublicadorDeCambiosEstados {
  private Set<InteresadoEnCambiosDeEstado> interesadoEnCambioDeEstado = new HashSet<>();

  public int cantidadDeInteresados() {
    return  interesadoEnCambioDeEstado.size();
  }

  public void suscribir(InteresadoEnCambiosDeEstado interesadoEnCambioDeEstado) {
    this.interesadoEnCambioDeEstado.add(interesadoEnCambioDeEstado);
  }

  public void desuscribir(InteresadoEnCambiosDeEstado interesadoEnCambioDeEstado) {
    this.interesadoEnCambioDeEstado.remove(interesadoEnCambioDeEstado);
  }

  public void publicarAsignacion(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria) {
    this.publicarA(interesado -> interesado.anteAsignacion(donacionIndependiente, entidadBeneficiaria));
  }

  public void publicarInicioDeRuta(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, String idEntrega) {
    this.publicarA(interesado -> interesado.anteInicioDeRuta(donacionIndependiente, entidadBeneficiaria, idEntrega));
  }

  public void publicarEntregaConExito(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, ComprobanteDeEntrega comprobanteDeEntrega) {
    this.publicarA(interesado -> interesado.anteEntregaConExito(donacionIndependiente, entidadBeneficiaria, comprobanteDeEntrega));
  }

  public void publicarEntregaFallida(DonacionIndependiente donacionIndependiente, EntidadBeneficiaria entidadBeneficiaria, String justificacion) {
    this.publicarA(interesado -> interesado.anteEntregaFallida(justificacion, entidadBeneficiaria, donacionIndependiente));
  }

  private void publicarA(Consumer<InteresadoEnCambiosDeEstado> accion) {
    this.interesadoEnCambioDeEstado.forEach(accion);
  }

  public void desuscribirTodos() {
    interesadoEnCambioDeEstado.clear();
  }
}
