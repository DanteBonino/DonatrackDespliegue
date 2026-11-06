package Donaciones;


import static EstadoDonacion.EstadoDonacion.ASIGNACION_REALIZADA;
import static EstadoDonacion.EstadoDonacion.ENTREGADA;
import static EstadoDonacion.EstadoDonacion.ENTREGA_FALLIDA;
import static EstadoDonacion.EstadoDonacion.EN_DEPOSITO;
import static EstadoDonacion.EstadoDonacion.EN_TRASLADO;
import static EstadoDonacion.EstadoDonacion.LISTA_PARA_ENTREGAR;
import static EstadoDonacion.EstadoDonacion.PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION;
import static EstadoDonacion.EstadoDonacion.VENCIDA;

import Actualizable.Actualizable;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Controllers.Donanciones.Update.DatosDonacion;
import Donantes.Donante;
import Eliminable.Eliminable;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import EstadoDonacion.EstadoDonacion;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Persistible.Persistible;
import ServiceLocator.ServiceLocator;
import Validaciones.Validaciones;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "DonacionesIndependientes")
public class DonacionIndependiente extends Persistible implements Eliminable {

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_item")
  private Item item;

  @ElementCollection
  @CollectionTable(name = "Historial_Cambios_de_Estado", joinColumns = @JoinColumn(name = "id_donacion_independiente"))
  @OrderColumn(name = "orden_de_transicion")
  private List<CambioDeEstado> historialEstado = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "id_donante")
  private Donante donante;

  @Column(name = "esta_eliminado")
  private boolean estaEliminado;

  @Column(name = "justificacion")
  private String justificacion;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado_actual")
  private EstadoDonacion estadoActual;

  @Transient
  private PublicadorDeCambiosEstados publicadorDeCambiosEstados;

  protected DonacionIndependiente() {
    this.publicadorDeCambiosEstados = ServiceLocator.obtenerPublicadorCambioDeEstado();
  }
  public DonacionIndependiente(Item item, EstadoDonacion estadoDonacion, Donante donante, PublicadorDeCambiosEstados publicador){
    this.item = item;
    this.donante = donante;
    this.estadoActual = estadoDonacion;
    this.publicadorDeCambiosEstados = publicador;
    this.agregarAHistorialDeEstados(estadoDonacion, null);
  }

  public List<CambioDeEstado> getHistorialEstados() {
    return List.copyOf(historialEstado);
  }

  private void cambiarEstado(EstadoDonacion otroEstado) {
    this.cambiarEstado(otroEstado, this.getAsignado());
  }

  private void cambiarEstado(EstadoDonacion otroEstado, EntidadBeneficiaria unaEntidad) {
    this.getEstado().validarTransicion(otroEstado);
    this.estadoActual = otroEstado;
    this.agregarAHistorialDeEstados(otroEstado, unaEntidad);
  }

  private void agregarAHistorialDeEstados(EstadoDonacion nuevoEstado, EntidadBeneficiaria unaEntidadBeneficiaria) {
    this.historialEstado.add(new CambioDeEstado(LocalDateTime.now(), nuevoEstado, unaEntidadBeneficiaria));
  }

    public Optional<LocalDateTime> fechaDeUltimaTransicionA(EstadoDonacion estadoDonacion) {
    return historialEstado.stream()
        .filter(cambio -> cambio.getEstadoDonacion().equals(estadoDonacion))
        .reduce((primero, ultimo) -> ultimo)
        .map(CambioDeEstado::getFecha);
  }

  public Bien getBienDonado(){
    return item.getBien();
  }

  public Donante getDonante() {
     return this.donante;
  }

  public Boolean estaEnDeposito() {
    return this.estaEn(EN_DEPOSITO);
  }

  public EstadoDonacion getEstado() {
      return estadoActual;
  }

  public LocalDateTime getFechaDeDonacion() {
    return this.primerCambio().getFecha();
  }

  public void asignarA(EntidadBeneficiaria asignado) {
    this.cambiarEstado(ASIGNACION_REALIZADA, asignado);
    publicadorDeCambiosEstados.publicarAsignacion(this, asignado);
  }

  public void almacenarEnDeposito() {
    this.justificacion = null;
    this.cambiarEstado(EN_DEPOSITO, null);
  }

  public void enSolicitudDeAsignacion() {
    this.cambiarEstado(PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION);
  }

  public void iniciarTraslado(String idEntrega) {
    this.cambiarEstado(EN_TRASLADO);
    publicadorDeCambiosEstados.publicarInicioDeRuta(this, this.getAsignado(), idEntrega);
  }

  public void alistarParaEntregar() {
    this.cambiarEstado(LISTA_PARA_ENTREGAR);
  }

  public void marcarComoEntregada(ComprobanteDeEntrega comprobanteDeEntrega) {
    EntidadBeneficiaria destino = this.getAsignado();
    this.cambiarEstado(ENTREGADA);
    destino.reicibirDonacionParaDeterminadaNecesidad(this);
    publicadorDeCambiosEstados.publicarEntregaConExito(this, destino, comprobanteDeEntrega);
  }

  public void marcarComoFallida(String justificacion) {
    Validaciones.requerido(justificacion, "justificacion");
    this.justificacion = justificacion;
    this.cambiarEstado(ENTREGA_FALLIDA);
    publicadorDeCambiosEstados.publicarEntregaFallida(this, this.getAsignado(), justificacion);
  }

  public void vencer() {
    this.cambiarEstado(VENCIDA, null);
  }


  public boolean estaEn(EstadoDonacion unEstado) {
    return this.getEstado().esDeTipo(unEstado);
  }

  public boolean estaEliminado() {
    return estaEliminado;
  }

  public void marcarComoEliminado() {
    estaEliminado = true;
  }

  public CambioDeEstado primerCambio() {
    return this.getHistorialEstados().get(0);
  }

  public CambioDeEstado ultimoCambio() {
    return this.getHistorialEstados().get(this.getHistorialEstados().size()-1);
  }

  public String getJustificacion() {
    return this.justificacion;
  }

  public EntidadBeneficiaria getAsignado() {
    return ultimoCambio().getAsignado();
  }


  public Boolean sigueEnVigencia() {
    return this.getBienDonado().estaVigente();
  }

  public Boolean estaEn(EstadoDeUso estadoDelBien) {
    return this.getBienDonado().estaEn(estadoDelBien);
  }

  public int getCantidadDeUnidades() {
    return item.getCantidadDeUnidades();
  }

  public BigDecimal getCantidadDeSatisfaccion() {
   return this.item.getCantidadDeSatisfaccion();
  }

  public Boolean seAgrupaPor(SubCategoria unaSubCategoria){
    return this.getBienDonado().perteneceA(unaSubCategoria);
  }

  public SubCategoria getSubCategoria() {
    return item.getSubCategoria();
  }

  public List<String> getFotos() {
    return this.item.getFotos();
  }

  public Item getItem() {
    return item;
  }

}
