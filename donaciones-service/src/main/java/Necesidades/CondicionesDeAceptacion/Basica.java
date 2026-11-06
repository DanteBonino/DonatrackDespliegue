package Necesidades.CondicionesDeAceptacion;

import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.CondicionDeAceptacion.TipoCondicion;
import Donaciones.DonacionIndependiente;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BASICA")
public class Basica extends CondicionDeAceptacion {

  protected Basica() {}
  public Basica(SubCategoria subCategoria) {
    super(subCategoria);
  }

  @Override
  public Boolean factorDeAceptacion(DonacionIndependiente donacionIndependiente) {
    return donacionIndependiente.sigueEnVigencia();
  }

  @Override
  public TipoCondicion getTipo() {
    return TipoCondicion.BASICA;
  }

  @Override
  public void validarSubCategoria(SubCategoria subCategoria) {

  }


}
