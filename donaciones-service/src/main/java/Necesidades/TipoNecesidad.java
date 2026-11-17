package Necesidades;

import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import java.math.BigDecimal;
import java.time.Period;
import utils.FabricaDeNecesidades;

public enum TipoNecesidad {
  RECURRENTE {
    @Override
    public Necesidad crearInstancia(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida, Period periodo) {
      return FabricaDeNecesidades.nuevaNecesidadRecurrenteDe(condicionDeAceptacion, cantidadRequerida, periodo);
    }

    public Boolean requierePeriodo() {
      return true;
    }
  },
  EXTRAORDINARIA {
    @Override
    public Necesidad crearInstancia(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida, Period periodo) {
      return FabricaDeNecesidades.nuevaNecesidadExtraordinariaDe(condicionDeAceptacion, cantidadRequerida);
    }

    public Boolean requierePeriodo() {
      return false;
    }

  };

  public abstract Necesidad crearInstancia(CondicionDeAceptacion condicion, BigDecimal cantidadRequerida, Period periodo);
  public abstract Boolean requierePeriodo();
}
