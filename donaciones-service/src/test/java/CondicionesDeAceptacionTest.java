import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FechasUtils.haceNDias;

import Bienes.Bien;
import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import ExceptionHandler.SubCategoriaInvalidaException;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeBienes;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeSubCategorias;

public class CondicionesDeAceptacionTest {
  private CondicionDeAceptacion nueva;
  private CondicionDeAceptacion usada;
  private CondicionDeAceptacion basicaPerecedera;
  private CondicionDeAceptacion basicaNoPerecedera;
  private SubCategoria unaSubCategoriaPerecedera;
  private SubCategoria subCategoriaQueNoMatchea;
  private Bien paqueteDeArrozDe500gramosVigente;
  private SubCategoria remera;
  private DonacionIndependiente donacionDeArrozVigente;
  private DonacionIndependiente donacionDeArrozVencido;
  private DonacionIndependiente donacionDeRemeraNueva;
  private DonacionIndependiente donacionRemeraUsada;

  @BeforeEach
  void setUp() {
    remera = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera");
    subCategoriaQueNoMatchea = FabricaDeSubCategorias.getSubCategoriaDeAlimento("manteca");
    unaSubCategoriaPerecedera = FabricaDeSubCategorias.getSubCategoriaPercederaConCategoriaCualquiera("perecedera");
    paqueteDeArrozDe500gramosVigente = FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(haceNDias(31));
    nueva = FabricaDeCondicionesDeAceptacion.Nuevo(remera);
    usada = FabricaDeCondicionesDeAceptacion.Usado(remera);
    basicaPerecedera = FabricaDeCondicionesDeAceptacion.unaDeVigencia(unaSubCategoriaPerecedera);
    basicaNoPerecedera = FabricaDeCondicionesDeAceptacion.unaDeVigencia(remera);
    donacionDeArrozVigente = FabricaDeDonacionesIndependientes.donacionConBienUnitario(paqueteDeArrozDe500gramosVigente);
    donacionDeRemeraNueva = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.remeraNueva());
    donacionRemeraUsada = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.remeraUsada());
    donacionDeArrozVencido = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(LocalDate.now().minusDays(10)));
  }

  @Test
  void noSePuedeCrearUnaCondicionDeAceptabilidadSegunElEstadoNuevoParaUnaSubCategoriaPerecedera() {
    assertThrows(SubCategoriaInvalidaException.class, () -> {
      FabricaDeCondicionesDeAceptacion.Nuevo(unaSubCategoriaPerecedera);
    });
  }

  @Test
  void noSePuedeCrearUnaCondicionDeAceptabilidadSegunElEstadoUsadoParaUnaSubCategoriaPerecedera() {
    assertThrows(SubCategoriaInvalidaException.class, () -> {
      FabricaDeCondicionesDeAceptacion.Usado(unaSubCategoriaPerecedera);
    });
  }

  @Test
  void nuncaSeAceptaUnaDonacionQueNoCoincidaConLaSubCategoriaDeLaCondicion() {
    assertFalse(nueva.esAceptable(donacionDeArrozVigente));
    assertFalse(usada.esAceptable(donacionDeArrozVigente));
    assertFalse(basicaPerecedera.esAceptable(donacionDeArrozVigente));
    assertFalse(basicaPerecedera.esAceptable(donacionDeRemeraNueva));
    assertFalse(basicaNoPerecedera.esAceptable(donacionDeArrozVigente));
  }

  @Test
  void unaCondicionPorEstadoNuevoAceptaUnaDonacionQueCoincideEnSubCategoriaSiEstaEnEseEstado() {
    assertTrue(nueva.esAceptable(donacionDeRemeraNueva));
  }

  @Test
  void unaCondicionPorEstadoNuevoAceptaUnaDonacionQueEstaEnEstadoUsado() {
    assertFalse(nueva.esAceptable(donacionRemeraUsada));
  }

  @Test
  void unaBasicaPerecederaRechazaDonacionesQueCoincidenConLaSubCategoriaYCuyosBienesEstanVencidos() {
    assertFalse(basicaPerecedera.esAceptable(donacionDeArrozVencido));
  }

  @Test
  void unaBasicaNoPerecederaAceptaDonacionesUsadasSiempreYCuandoCoincidanLasSubCategorias() {
    assertTrue(basicaNoPerecedera.esAceptable(donacionRemeraUsada));
  }

  @Test
  void unaBasicaNoPerecederaAceptaDonacionesNuevasSiempreYCuandoCoincidanLasSubCategorias() {
    assertTrue(basicaNoPerecedera.esAceptable(donacionDeRemeraNueva));
  }

}
