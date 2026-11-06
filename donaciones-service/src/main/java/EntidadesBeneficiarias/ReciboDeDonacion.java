package EntidadesBeneficiarias;

import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Embeddable
public class ReciboDeDonacion {
  @OneToOne
  @JoinColumn(name = "id_donacion")
  private DonacionIndependiente donacionRecibida;

  @Column(name = "fecha_de_recepcion")
  private LocalDate fechaDeRecepcion;

  protected ReciboDeDonacion() {}
  public ReciboDeDonacion(DonacionIndependiente unaDonacion) {
    this.donacionRecibida = unaDonacion;
    this.fechaDeRecepcion = LocalDate.now();
  }

  public boolean esPosteriorA(LocalDate unaFecha) {
    return !this.fechaDeRecepcion.isBefore(unaFecha);
  }

  public BigDecimal getCantidad() {
    return this.donacionRecibida.getCantidadDeSatisfaccion();
  }

  public Boolean esDe(SubCategoria unaSubCategoria) {
    return this.donacionRecibida.seAgrupaPor(unaSubCategoria);
  }
}
