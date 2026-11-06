package Necesidades;

import Bienes.Bien;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Validaciones.Validaciones;
import java.math.BigDecimal;

public class NecesidadExtraordinariaBuilder {
  private CondicionDeAceptacion condicionDeAceptacion;
  private BigDecimal cantidadRequerida;

  public NecesidadExtraordinariaBuilder conCondicionDeAceptacion(CondicionDeAceptacion condicionDeAceptacion) {
    this.condicionDeAceptacion = condicionDeAceptacion;
    return this;
  }

  public NecesidadExtraordinariaBuilder conCantidadRequerida(BigDecimal cantidad) {
    this.cantidadRequerida = cantidad;
    return this;
  }

  public NecesidadExtraordinaria build() {
    Validaciones.requerido(condicionDeAceptacion, "condicionDeAceptacion");
    Validaciones.requerido(cantidadRequerida, "cantidadRequerida");
    return new NecesidadExtraordinaria(condicionDeAceptacion, cantidadRequerida);
  }
}