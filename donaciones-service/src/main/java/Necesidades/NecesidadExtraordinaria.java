package Necesidades;

import Bienes.SubCategoria.SubCategoria;
import EntidadesBeneficiarias.ReciboDeDonacion;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import java.math.BigDecimal;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("EXTRAORDINARIA")
public class NecesidadExtraordinaria extends Necesidad {

  protected NecesidadExtraordinaria(){}
  public NecesidadExtraordinaria(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida){
    super(condicionDeAceptacion, cantidadRequerida);
  }

  @Override
  public TipoNecesidad getTipo() {
    return TipoNecesidad.EXTRAORDINARIA;
  }

  @Override
  protected boolean condicionDeActualidad(ReciboDeDonacion unRecibo) {
    return true;
  }

}
