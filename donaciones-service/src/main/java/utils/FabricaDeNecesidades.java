package utils;

import static utils.FabricaDeDatos.crearN;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.Necesidad;
import Necesidades.NecesidadExtraordinaria;
import Necesidades.NecesidadExtraordinariaBuilder;
import Necesidades.NecesidadRecurrente;
import Necesidades.NecesidadRecurrenteBuilder;
import Necesidades.TipoNecesidad;
import java.math.BigDecimal;
import java.time.Period;
import java.util.List;

public class FabricaDeNecesidades {
  public static NecesidadExtraordinaria nuevaNecesidadExtraordinariaDe(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida) {
    return new NecesidadExtraordinariaBuilder()
        .conCondicionDeAceptacion(condicionDeAceptacion)
        .conCantidadRequerida(cantidadRequerida)
        .build();
  }

  public static NecesidadExtraordinaria nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(CondicionDeAceptacion condicionDeAceptacion, int cantidadRequerida) {
    return nuevaNecesidadExtraordinariaDe(condicionDeAceptacion, BigDecimal.valueOf(cantidadRequerida));
  }

  public static NecesidadExtraordinaria nuevaNecesidadExtraordinariaDeCantidadDe1000LoQueSea(CondicionDeAceptacion condicionDeAceptacion) {
    return nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(condicionDeAceptacion, 1000);
  }

  public static NecesidadRecurrente nuevaNecesidadRecurrenteDe(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida, Period periodo) {
    return new NecesidadRecurrenteBuilder()
        .conCantidadRequerida(cantidadRequerida)
        .conPeriodo(periodo)
        .conCondicionDeAceptacion(condicionDeAceptacion)
        .build();
  }

  public static NecesidadRecurrente nuevaNecesidadRecurrenteConCantidaRequeridaExacta(CondicionDeAceptacion condicionDeAceptacion, int cantidadRequerida, Period periodo) {
    return nuevaNecesidadRecurrenteDe(condicionDeAceptacion, BigDecimal.valueOf(cantidadRequerida), periodo);
  }

  public static NecesidadRecurrente nuevaNecesidadRecurrenteSemanalConCantidadExacta(CondicionDeAceptacion condicionDeAceptacion, int cantidadRequerida) {
    return nuevaNecesidadRecurrenteConCantidaRequeridaExacta(condicionDeAceptacion, cantidadRequerida, Period.ofWeeks(1));
  }

  public static NecesidadRecurrente nuevaNecesidadRecurrenteSemanalDeCantidadDe1000LoQueSea(CondicionDeAceptacion condicionDeAceptacion) {
    return nuevaNecesidadRecurrenteSemanalConCantidadExacta(condicionDeAceptacion, 1000);
  }

  public static Necesidad necesidadVigenteDe(SubCategoria unaSubCategoria, int cantidadRequerida) {
    return nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(FabricaDeCondicionesDeAceptacion.unaDeVigencia(unaSubCategoria), cantidadRequerida);
  }

  public static Necesidad necesidadEnEstadoNuevo(SubCategoria subCategoria, int cantidadRequerida) {
    return nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(FabricaDeCondicionesDeAceptacion.Nuevo(subCategoria), cantidadRequerida);
  }

  public static List<Necesidad> nNecesidadesVigentesDe(SubCategoria subCategoria, int cantidad) {
    return crearN(cantidad, i -> necesidadVigenteDe(subCategoria, 10));
  }

  public static Necesidad crearSegunTipo(TipoNecesidad tipo, CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida, Period periodo) {
    return switch (tipo) {
      case EXTRAORDINARIA -> nuevaNecesidadExtraordinariaDe(condicionDeAceptacion, cantidadRequerida);
      case RECURRENTE -> nuevaNecesidadRecurrenteDe(condicionDeAceptacion, cantidadRequerida, periodo);
    };
  }
}
