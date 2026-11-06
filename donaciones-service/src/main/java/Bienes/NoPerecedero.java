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
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("NOPERECEDERO")
public class NoPerecedero extends Bien {
  @Enumerated
  @Column(name = "estado_de_uso")
  private EstadoDeUso estadoDeUso;

  protected NoPerecedero() {

  }
  public NoPerecedero(SubCategoria subCategoria, BigDecimal contenido, EstadoDeUso estadoDeUso) {
    super(subCategoria, contenido);
    this.estadoDeUso = Validaciones.requerido(estadoDeUso, "estadoDeUso");;
  }

  public Object getFactorDeAgrupamiento() {
    return this.estadoDeUso;
  }

  @Override
  public Boolean estaEn(EstadoDeUso unEstado) {
    return this.estadoDeUso.equals(unEstado);
  }
  @Override
  public Boolean estaVigente() {
    return true;
  }

  protected void validacionExtra() {
    if(getSubCategoria().esPerecedera()) throw new SubCategoriaInvalidaParaTipoDeBien(getSubCategoria().getNombre(), TipoBien.NOPERECEDERO);

  }

  public Bien crearUnoSemejante() {
    return new NoPerecedero(getSubCategoria(), getContenido(), estadoDeUso);
  }
}
