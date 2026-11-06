import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FechasUtils.ayer;
import static utils.FechasUtils.enUnMes;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Bienes.SubCategoria.SubCategoria;
import ExceptionHandler.DatosInvalidosException;
import ExceptionHandler.SubCategoriaInvalidaParaTipoDeBien;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeBienes;
import utils.FabricaDeSubCategorias;
import utils.FechasUtils;

public class BienTest {

  private Bien remeraNueva;
  private Bien remeraVieja;
  private Bien paqueteDeArrozDe500gramosVigente;
  private Bien paqueteDeArrozDe500gramosVencido;
  private Bien paqueteDeFideosDe500gramos;
  private LocalDate vencimientoVigente;

  private Bien unaRemeraEnEstado(EstadoDeUso estadoDeUso) {
    return FabricaDeBienes.unNoPerecedero(FabricaDeSubCategorias.getSubCategoriaByNombreConExcepcion("remera"), BigDecimal.valueOf(1), estadoDeUso);
  }

  private Bien unPaqueteDeArrozDe500GramosConVencimientoEn(LocalDate fechaDeVencimiento) {
    return unPerecederoDe500Gramos("ARROZ", fechaDeVencimiento);
  }

  private Bien unPerecederoDe500Gramos(String nombreSubCategoria, LocalDate vencimiento) {
    return new Perecedero(FabricaDeSubCategorias.getSubCategoriaByNombreConExcepcion(nombreSubCategoria), BigDecimal.valueOf(0.5), vencimiento);
  }

  private Boolean estaNuevo(Bien bien) {
    return bien.estaEn(EstadoDeUso.NUEVO);
  }

  private Boolean estaUsado(Bien bien) {
    return bien.estaEn(EstadoDeUso.USADO);
  }

  @BeforeEach
  void setUp() {
    vencimientoVigente = enUnMes();
    remeraVieja = unaRemeraEnEstado(EstadoDeUso.USADO);
    remeraNueva = unaRemeraEnEstado(EstadoDeUso.NUEVO);
    paqueteDeArrozDe500gramosVigente = unPaqueteDeArrozDe500GramosConVencimientoEn(vencimientoVigente);
    paqueteDeArrozDe500gramosVencido = unPaqueteDeArrozDe500GramosConVencimientoEn(ayer());
    paqueteDeFideosDe500gramos = unPerecederoDe500Gramos("FIDEOSSECOS", vencimientoVigente);
  }

  @Test
  void unBienPerteneceALaSubCategoriaQueReferencia() {
    assertTrue(paqueteDeArrozDe500gramosVigente.perteneceA(paqueteDeArrozDe500gramosVigente.getSubCategoria()));
  }

  @Test
  void unBienPerteneceAUnaSubCategoriaQueSeLlameComoLaQueReferencia() {
    SubCategoria otroArroz = FabricaDeSubCategorias.getSubCategoriaDeAlimento("arroz");
    assertTrue(paqueteDeArrozDe500gramosVigente.perteneceA(otroArroz));
  }

  @Test
  void unBienNoPerteneceAUnaSubCategoriaQueNoSeaConLaQueSeCreo() {
    assertFalse(paqueteDeArrozDe500gramosVigente.perteneceA(remeraNueva.getSubCategoria()));
  }

  @Test
  void unBienNoPerecederoSiempreEstaVigente() {
    assertTrue(remeraNueva.estaVigente());
    assertTrue(remeraVieja.estaVigente());
  }

  @Test
  void unBienPerecederoEstaVigenteSiSuVencimientoNoOcurrió() {
    assertTrue(paqueteDeArrozDe500gramosVigente.estaVigente());
  }

  @Test
  void unBienPerecederoVencidoNoEstaVigente() {
    assertFalse(paqueteDeArrozDe500gramosVencido.estaVigente());
  }

  @Test
  void unBienNoPerecederoNuevoEstaNuevo() {
    assertTrue(estaNuevo(remeraNueva));
  }

  @Test
  void unBienNoPerecederoNuevoNoEstaUsado() {
    assertFalse(estaUsado(remeraNueva));
  }

  @Test
  void unBienNoPerecederoUsadoNoEstaNuevo() {
    assertFalse(estaNuevo(remeraVieja));
  }

  @Test
  void unBienNoPerecederoUsadoEstaUsado() {
    assertTrue(estaUsado(remeraVieja));
  }

  @Test
  void unBienPerecederoNuncaEstaNuevo() {
    assertFalse(estaNuevo(paqueteDeArrozDe500gramosVigente));
    assertFalse(estaNuevo(paqueteDeArrozDe500gramosVencido));
  }

  @Test
  void unBienPerecederoNuncaEstaUsado() {
    assertFalse(estaUsado(paqueteDeArrozDe500gramosVigente));
    assertFalse(estaUsado(paqueteDeArrozDe500gramosVencido));
  }

  @Test
  void unBienSatisfaceSuContenidoPorLaCantidadDeBienQueLePasenPorParametro() {
    int cantidadDeBien = 5;
    assertEquals(paqueteDeArrozDe500gramosVigente.getContenido().multiply(BigDecimal.valueOf(cantidadDeBien)), paqueteDeArrozDe500gramosVigente.cantidadSatisfechaSegunCantidadDeBien(cantidadDeBien));
  }

  @Test
  void unBienEsAgrupableConsigoMismo() {
    assertTrue(paqueteDeArrozDe500gramosVigente.esAgrupableCon(paqueteDeArrozDe500gramosVigente));
  }

  @Test
  void unBienPerecederoNoEsAgrupableConOtroConDistintaFechaDeVencimiento() {
    assertFalse(paqueteDeArrozDe500gramosVigente.esAgrupableCon(paqueteDeArrozDe500gramosVencido));
  }

  @Test
  void unBienNoPerecederoNoEsAgrupableConOtroConDistintoEstadoDeUso() {
    assertFalse(remeraNueva.esAgrupableCon(remeraVieja));
  }

  @Test
  void unBienPerecederoNoEsAgrupableConUnoNoPerecedero() {
    assertFalse(paqueteDeArrozDe500gramosVigente.esAgrupableCon(remeraNueva));
  }

  @Test
  void dosBienesNoSonAgrupablesSiNoCompartenSubCategoria() {
    assertFalse(paqueteDeFideosDe500gramos.esAgrupableCon(paqueteDeArrozDe500gramosVigente));
  }

  @Test
  void dosBienesNoSonAgrupablesSiNoTienenIgualContenido() {
    Bien paqueteDeArrozDe250Gramos = FabricaDeBienes.unBienPerecedero(paqueteDeArrozDe500gramosVigente.getSubCategoria(), BigDecimal.valueOf(0.250), vencimientoVigente);
    assertFalse(paqueteDeArrozDe250Gramos.esAgrupableCon(paqueteDeArrozDe500gramosVigente));
  }

  @Test
  void noSePuedeCrearUnBienPerecederoSinSuSubCategoria() {
    assertThrows(DatosInvalidosException.class, () -> {
      new Perecedero(null, null, null);
    });
  }

  @Test
  void noSePuedeCrearUnBienPercederoSinSuContenido() {
    assertThrows(DatosInvalidosException.class, () -> {
      new Perecedero(paqueteDeArrozDe500gramosVigente.getSubCategoria(), null, null);
    });
  }

  @Test
  void noSePuedeCrearUnBienPercederoSinSuVencimiento() {
    assertThrows(DatosInvalidosException.class, () -> {
      new Perecedero(paqueteDeArrozDe500gramosVigente.getSubCategoria(), BigDecimal.valueOf(1), null);
    });
  }

  @Test
  void noSePuedeCrearUnBienPerecederoConUnaSubCategoriaNoPerecedera() {
    assertThrows(SubCategoriaInvalidaParaTipoDeBien.class, () -> {
      new Perecedero(remeraVieja.getSubCategoria(), BigDecimal.valueOf(1), FechasUtils.enUnMes());
    });
  }

  @Test
  void noSePuedeCrearUnBienNoPerecederoSinSuSubCategoria() {
    assertThrows(DatosInvalidosException.class, () -> {
      new NoPerecedero(null, null, null);
    });
  }

  @Test
  void noSePuedeCrearUnBienNoPercederoSinSuContenido() {
    assertThrows(DatosInvalidosException.class, () -> {
      new NoPerecedero(remeraVieja.getSubCategoria(), null, null);
    });
  }

  @Test
  void noSePuedeCrearUnBienNoPercederoSinSuEstadoDeUso() {
    assertThrows(DatosInvalidosException.class, () -> {
      new NoPerecedero(remeraVieja.getSubCategoria(), BigDecimal.valueOf(1), null);
    });
  }

  @Test
  void noSePuedeCrearUnBienNoPerecederoConUnaSubCategoriaPerecedera() {
    assertThrows(SubCategoriaInvalidaParaTipoDeBien.class, () -> {
      new NoPerecedero(paqueteDeFideosDe500gramos.getSubCategoria(), BigDecimal.valueOf(1), EstadoDeUso.USADO);
    });
  }
}
