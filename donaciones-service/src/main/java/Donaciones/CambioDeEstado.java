package Donaciones;

import EntidadesBeneficiarias.EntidadBeneficiaria;
import EstadoDonacion.EstadoDonacion;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class CambioDeEstado {

  @Column(name = "fecha_de_transicion")
  private LocalDateTime fecha;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado_donacion")
  private EstadoDonacion estadoDonacion;

  @ManyToOne
  @JoinColumn(name = "id_entidad_beneficiaria_asignada")
  private EntidadBeneficiaria asignado;

  protected CambioDeEstado() {
  }

  public CambioDeEstado(LocalDateTime fecha, EstadoDonacion estadoDonacion, EntidadBeneficiaria asignado) {
    this.fecha = fecha;
    this.estadoDonacion = estadoDonacion;
    this.asignado = asignado;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public EstadoDonacion getEstadoDonacion() {
    return estadoDonacion;
  }

  public EntidadBeneficiaria getAsignado() {
    return asignado;
  }
}
