package Bienes;

import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.Bien.TipoBien;
import ExceptionHandler.SubCategoriaInvalidaParaTipoDeBien;
import Validaciones.Validaciones;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PERECEDERO")
public class Perecedero extends Bien {
  @Column(name = "fecha_vencimiento")
  private LocalDate vencimiento;

  protected Perecedero() {

  }
  public Perecedero(SubCategoria subCategoria, BigDecimal contenido, LocalDate fechaDeVencimiento) {
    super(subCategoria, contenido);
    this.vencimiento = Validaciones.requerido(fechaDeVencimiento, "vencimiento");;
  }

  public Object getFactorDeAgrupamiento() {
    return this.vencimiento;
  }

  @Override
  public Boolean estaEn(EstadoDeUso unEstado) {
    return false;
  }

  @Override
  public Boolean estaVigente() {
    return this.vencimiento.isAfter(LocalDate.now());
  }

  public LocalDate getVencimiento() {
    return vencimiento;
  }

  protected void validacionExtra() {
    if(!getSubCategoria().esPerecedera()) throw new SubCategoriaInvalidaParaTipoDeBien(getSubCategoria().getNombre(), TipoBien.PERECEDERO);
  }

  public Bien crearUnoSemejante() {
    return new Perecedero(getSubCategoria(), getContenido(), vencimiento);
  }
}
