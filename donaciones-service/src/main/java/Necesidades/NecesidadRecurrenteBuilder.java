package Necesidades;

import Bienes.Bien;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Validaciones.Validaciones;
import java.math.BigDecimal;
import java.time.Period;

public class NecesidadRecurrenteBuilder {
  private Period periodo;
  private CondicionDeAceptacion condicionDeAceptacion;
  private BigDecimal cantidadRequerida;

  public NecesidadRecurrenteBuilder conPeriodo(Period periodo) {
    this.periodo = periodo;
    return this;
  }

  public NecesidadRecurrenteBuilder conCondicionDeAceptacion(CondicionDeAceptacion condicionDeAceptacion) {
    this.condicionDeAceptacion = condicionDeAceptacion;
    return this;
  }

  public NecesidadRecurrenteBuilder conCantidadRequerida(BigDecimal cantidad) {
    this.cantidadRequerida = cantidad;
    return this;
  }

  public NecesidadRecurrente build() {
    Validaciones.requerido(periodo, "periodo");
    Validaciones.requerido(condicionDeAceptacion, "condicionDeAceptacion");
    Validaciones.requerido(cantidadRequerida, "cantidadRequerida");
    return new NecesidadRecurrente(condicionDeAceptacion, cantidadRequerida, periodo);
  }
}
