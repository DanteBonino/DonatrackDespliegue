package domain;

import ServiceLocator.ServiceLocator;
import domain.eventos.*;
import domain.persistencia.EntidadBase;
import exceptions.EstadoEntregaInvalidoException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Representa una entrega de una donacion independiente hacia una entidad beneficiaria.
 * La entrega mantiene su estado, comprobante de recepcion y posibles incidencias.
 *
 * <p>Convenciones:
 * - Los estados de entrega se definen en {@link EstadoEntrega}.
 * - El comprobante de recepcion se guarda como referencia a una foto (ej. URL o path).
 * - El motivo de no recepcion se guarda como texto descriptivo.
 */
@Entity
@Table(name = "Entregas")
public class Entrega extends EntidadBase {

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "id_donacion")
  private String idDonacion;

  @Column(name = "id_entidad_beneficiaria")
  private String idEntidadBeneficiaria;

  @Column(name = "comprobante_foto")
  private String comprobanteFoto;

  @Column(name = "motivo_de_no_recepcion")
  private String motivoNoRecepcion;

  @Embedded
  private Direccion direccion;

  @ElementCollection
  @CollectionTable(name = "Historial_De_Estados")
  @OrderColumn(name = "orden")
  private List<EstadoHistorico> historialDeEstados = new ArrayList<>();

  @Transient
  private EntregaPublicador publicadorDeCambiosDeEstado;

  protected Entrega(){
    this.publicadorDeCambiosDeEstado = ServiceLocator.obtenerPublicadorCambioDeEstado();
  };
  /**
   * Constructor: crea una nueva entrega en estado PLANIFICADA con
   * idEntrega random.
   *
   * @param idDonacion id de la Donacion independiente a entregar.
   * @param idEntidadBeneficiaria id de la Entidad beneficiaria destinataria.
   */
  public Entrega(String idDonacion, String idEntidadBeneficiaria, Direccion direccion, EntregaPublicador nuevoPublicador) {
    this.nombre = "ENT-" + UUID.randomUUID() + "-" + idDonacion + "-" + idEntidadBeneficiaria;
    this.idDonacion = idDonacion;
    this.idEntidadBeneficiaria = idEntidadBeneficiaria;
    this.publicadorDeCambiosDeEstado = nuevoPublicador;
    this.direccion = direccion;
    this.cambiarEstadoA(EstadoEntrega.PLANIFICADA);
  }



  /**
   * Inicia el viaje de la entrega, cambiando su estado a EN_CAMINO.
   * Postea el evento en la api de Donaciones para que se actualice el estado y, además, se envie la.
   * notificación correspondiente.
   *
   * @throws IllegalStateException si la entrega no esta en estado PLANIFICADA.
   */
  public void iniciarViaje() {
    validarEstadoEsperado(EstadoEntrega.PLANIFICADA, "iniciar viaje");
    this.cambiarEstadoA(EstadoEntrega.EN_CAMINO);
    publicadorDeCambiosDeEstado.publicarEntregaIniciada(
          new EntregaIniciadaEvent(nombre, idDonacion, this.idEntidadBeneficiaria)
      );
  }

  /**
   * Confirma la recepcion de la entrega, cambiando su estado a COMPLETADA.
   * Guarda el comprobante de recepcion y actualiza la donacion a {@link Entregada}.
   *
   * @param comprobanteFoto Foto de comprobante de recepcion.
   * @throws IllegalStateException si la entrega no esta en estado EN_CAMINO.
   */
  public void confirmarRecepcion(String comprobanteFoto, String patenteCamion) {
    validarEstadoEsperado(EstadoEntrega.EN_CAMINO, "confirmar recepcion");
    this.cambiarEstadoA(EstadoEntrega.COMPLETADA);
    this.comprobanteFoto = comprobanteFoto;
      publicadorDeCambiosDeEstado.publicarEntregaCompletada(
          new EntregaCompletadaEvent(this.nombre, this.idDonacion,
              this.idEntidadBeneficiaria, comprobanteFoto, patenteCamion)
      );
  }

  /**
   * Reporta una incidencia en la entrega, cambiando su estado a INCIDENCIA.
   * Guarda el motivo de no recepcion y actualiza la donacion a {@link EntregaFallida}.
   *
   * @param motivoNoRecepcion Texto descriptivo del motivo de la incidencia.
   * @throws IllegalStateException si la entrega no esta en estado EN_CAMINO.
   */
  public void reportarIncidencia(String motivoNoRecepcion) {
    validarEstadoEsperado(EstadoEntrega.EN_CAMINO, "reportar incidencia");
    this.cambiarEstadoA(EstadoEntrega.INCIDENCIA);
    this.motivoNoRecepcion = motivoNoRecepcion;
      publicadorDeCambiosDeEstado.publicarIncidenciaLogistica(
          new IncidenciaLogisticaEvent(this.nombre, this.idDonacion, this.idEntidadBeneficiaria,
              motivoNoRecepcion)
      );
  }

  /**
   * Reprograma la entrega tras una incidencia, devolviendola al estado PLANIFICADA.
   * La donacion regresa al deposito (EnDeposito) para ser re-asignada en el proximo
   * ciclo de planificacion.
   *
   * @throws IllegalStateException si la entrega no esta en estado INCIDENCIA.
   */
  public void reprogramar() {
    validarEstadoEsperado(EstadoEntrega.INCIDENCIA, "reprogramar");
    this.cambiarEstadoA(EstadoEntrega.PLANIFICADA);
    this.motivoNoRecepcion = null;
      publicadorDeCambiosDeEstado.publicarEntregaReprogramada(
          new EntregaReprogramadaEvent(this.nombre, this.idDonacion)
      );
  }

  /**
   * Valida que la entrega se encuentre en el estado requerido antes de ejecutar una accion.
   *
   * @param estadoRequerido Estado esperado.
   * @param accion Nombre de la accion que se intenta ejecutar.
   * @throws IllegalStateException si el estado actual no coincide con el requerido.
   */
  private void validarEstadoEsperado(EstadoEntrega estadoRequerido, String accion) {
    if (this.getEstado() != estadoRequerido) {
      throw new EstadoEntregaInvalidoException(accion, this.getEstado());
    }
  }

  private void cambiarEstadoA(EstadoEntrega nuevoEstado) {
    this.historialDeEstados.add(new EstadoHistorico(nuevoEstado));
  }

  // --- Getters ---
  public String getNombre() {
    return this.nombre;
  }

  public String getIdDonacion() {
    return this.idDonacion;
  }

  public String getIdDestinatario() {
    return idEntidadBeneficiaria;
  }

  public EstadoEntrega getEstado() {
    return historialDeEstados.get(historialDeEstados.size()-1).getEstado();
  }

  public String getComprobanteFoto() {
    return comprobanteFoto;
  }

  public String getMotivoNoRecepcion() {
    return motivoNoRecepcion;
  }

  public Boolean estaCompletada() {
    return estaEn(EstadoEntrega.COMPLETADA);
  }

  public Boolean estaFinalizada() {
    return estaCompletada() || estaEnIncidencia();
  }

  public Boolean estaEnIncidencia() {
    return estaEn(EstadoEntrega.INCIDENCIA);
  }

  public Boolean estaEnCamino() {
    return estaEn(EstadoEntrega.EN_CAMINO);
  }

  private Boolean estaEn(EstadoEntrega estadoDeComparacion) {
    return this.getEstado().equals(estadoDeComparacion);
  }
}


