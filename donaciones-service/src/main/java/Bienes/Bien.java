package Bienes;

import Bienes.SubCategoria.SubCategoria;
import Persistible.Persistible;
import Validaciones.Validaciones;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Bienes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_bien")
abstract public class Bien extends Persistible {
  @ManyToOne
  @JoinColumn(name = "id_subcategoria")
  private SubCategoria subCategoria;
  @Column(name = "contenido")
  private BigDecimal contenido;

  protected Bien() {

  }
  public Bien(SubCategoria subCategoria, BigDecimal cantidad) {
   this.subCategoria = subCategoria;
   this.contenido = cantidad;
   this.validarCreacion();
  }

  public BigDecimal getContenido() {
    return this.contenido;
  }

  public SubCategoria getSubCategoria() {
    return this.subCategoria;
  }

  public Boolean esAgrupableCon(Bien otroBien) {
    return this.perteneceA(otroBien.getSubCategoria()) && this.tieneCiertoContenido(otroBien.getContenidoNormalizado()) && this.getFactorDeAgrupamiento().equals(otroBien.getFactorDeAgrupamiento());
  }

  public Boolean perteneceA(SubCategoria unaSubCategoria) {
    return this.getSubCategoria().esLaMisma(unaSubCategoria);
  }

  private Boolean tieneCiertoContenido(BigDecimal unContenido) {
    return this.getContenidoNormalizado().equals(unContenido);
  }

  public abstract Object getFactorDeAgrupamiento();

  private BigDecimal getContenidoNormalizado() {
    return this.getContenido().stripTrailingZeros();
  }

  public BigDecimal cantidadSatisfechaSegunCantidadDeBien(int cantidadDeBien) {
    return this.getContenido().multiply(BigDecimal.valueOf(cantidadDeBien));
  }

  public abstract Boolean estaEn(EstadoDeUso unEstado);

  public abstract Boolean estaVigente();

  private void validarCreacion() {
    Validaciones.requerido(subCategoria, "subCategoria");
    Validaciones.requerido(contenido, "contenido");
    validacionExtra();
  };

  protected abstract void validacionExtra();

  abstract public Bien crearUnoSemejante();

}
