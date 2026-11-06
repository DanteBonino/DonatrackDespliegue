package Necesidades.CondicionesDeAceptacion;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.CondicionDeAceptacion.TipoCondicion;
import Donaciones.DonacionIndependiente;
import ExceptionHandler.SubCategoriaInvalidaException;
import Validaciones.Validaciones;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("ENESTADO")
public class EnEstado extends CondicionDeAceptacion {
  @Enumerated
  @Column(name = "estado_de_uso")
  private EstadoDeUso estado;

  protected EnEstado() {}
  public EnEstado(SubCategoria subCategoria, EstadoDeUso estado) {
    super(subCategoria);
    this.estado = estado;
  }

  @Override
  protected Boolean factorDeAceptacion(DonacionIndependiente donacionIndependiente) {
    return donacionIndependiente.estaEn(estado);
  }


  @Override
  public TipoCondicion getTipo() {
    if(estado.equals(EstadoDeUso.NUEVO)) return TipoCondicion.NUEVO;
    return TipoCondicion.USADO;
  }

  @Override
  public void validarSubCategoria(SubCategoria subCategoria) {
    if(subCategoria.esPerecedera()) {
      throw new SubCategoriaInvalidaException();
    }
  }
}
