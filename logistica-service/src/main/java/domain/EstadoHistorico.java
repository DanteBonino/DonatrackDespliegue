package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class EstadoHistorico {

  @Enumerated(EnumType.STRING)
  private EstadoEntrega estado;

  @Column(name = "fecha_de_transicion")
  private LocalDateTime fechaDeTransicion;

  protected  EstadoHistorico() {

  }

  public EstadoHistorico(EstadoEntrega estado) {
    this.estado = estado;
    this.fechaDeTransicion = LocalDateTime.now();
  }

  public EstadoEntrega getEstado() {
    return estado;
  }

}
