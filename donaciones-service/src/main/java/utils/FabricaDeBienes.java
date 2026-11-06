package utils;

import static utils.FabricaDeSubCategorias.getSubCategoriaByNombreConExcepcion;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Bienes.SubCategoria.SubCategoria;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FabricaDeBienes {
  public static Perecedero unBienPerecedero(SubCategoria subCategoria, BigDecimal contenido, LocalDate fechaDeVencimiento) {
    return new Perecedero(subCategoria, contenido, fechaDeVencimiento);
  }

  public static NoPerecedero unNoPerecedero(SubCategoria subCategoria, BigDecimal contenido, EstadoDeUso estadoDeUso) {
    return new NoPerecedero(subCategoria, contenido, estadoDeUso);
  }

  public static Bien unaRemeraEnEstado(EstadoDeUso estadoDeUso) {
    return unNoPerecedero(getSubCategoriaByNombreConExcepcion("remera"), BigDecimal.valueOf(1), estadoDeUso);
  }

  public static Bien unPaqueteDeArrozDe500GramosConVencimientoEn(LocalDate fechaDeVencimiento) {
    return unPerecederoDe500Gramos("ARROZ", fechaDeVencimiento);
  }

  public static Bien unPerecederoDe500Gramos(String nombreSubCategoria, LocalDate vencimiento) {
    return new Perecedero(getSubCategoriaByNombreConExcepcion(nombreSubCategoria), BigDecimal.valueOf(0.5), vencimiento);
  }

  public static Bien remeraNueva() {
    return unaRemeraEnEstado(EstadoDeUso.NUEVO);
  }

  public static Bien remeraUsada() {
    return unaRemeraEnEstado(EstadoDeUso.USADO);
  }

  public static Bien paqueteDeArrozDe500GramosVigente() {
    return FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(LocalDate.now().plusDays(10));
  }

  public static Bien unBienNuevo(SubCategoria subCategoria) {
    return FabricaDeBienes.bienNuevoDeContenido(subCategoria, 1);
  }

  public static Bien bienNuevoDeContenido(SubCategoria subCategoria, int cantidadExacta) {
    return FabricaDeBienes.unNoPerecedero(subCategoria, BigDecimal.valueOf(cantidadExacta), EstadoDeUso.NUEVO);
  }

  public static Bien unBienComo(Bien unBien) {
    return unBien.crearUnoSemejante();
  }
}
