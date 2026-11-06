package Necesidades.CondicionesDeAceptacion;

import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.CondicionDeAceptacion.TipoCondicion;
import Donaciones.DonacionIndependiente;
import Persistible.Persistible;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CondicionesDeAceptacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class CondicionDeAceptacion extends Persistible {
  @ManyToOne
  @JoinColumn(name = "id_subcategoria")
  private SubCategoria subCategoria;

  protected CondicionDeAceptacion() {

  }
  public CondicionDeAceptacion(SubCategoria subCategoria) {
    validarSubCategoria(subCategoria);
    this.subCategoria = subCategoria;
  }

  public Boolean esAceptable(DonacionIndependiente donacionIndependiente) {
    return donacionIndependiente.seAgrupaPor(subCategoria) && factorDeAceptacion(donacionIndependiente);
  };

  protected abstract Boolean factorDeAceptacion(DonacionIndependiente donacionIndependiente);

  public SubCategoria getSubCategoria() {
    return this.subCategoria;
  }

  public abstract TipoCondicion getTipo();
  public abstract void validarSubCategoria(SubCategoria subCategoria);
}
