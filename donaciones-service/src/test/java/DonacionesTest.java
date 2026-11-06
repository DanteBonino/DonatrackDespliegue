import static EstadoDonacion.EstadoDonacion.EN_DEPOSITO;
import static EstadoDonacion.EstadoDonacion.PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeDirecciones.calleCualquiera;
import static utils.FabricaDeDirecciones.direccionCualquiera;
import static utils.FechasUtils.enUnMes;
import static utils.FechasUtils.haceNDias;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteHumana.PersonaHumana;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import EstadoDonacion.EstadoDonacion;
import ExceptionHandler.NoHayNecesidadCorrespondiente;
import ExceptionHandler.TransicionInvalidaDeEstado;
import Necesidades.NecesidadExtraordinaria;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeBienes;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeNecesidades;
import utils.FabricaDeSubCategorias;

public class DonacionesTest {
  private SubCategoria pantalon;
  private Bien pantalonNuevo;
  private Bien fideosSecosDe500GramosConVencimientoHoy;
  private DonacionIndependiente diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes;
  private DonacionIndependiente diezPantalonesNuevos;
  private DonacionIndependiente diezRemerasUsadas;
  private DonacionIndependiente diezPaquetesDeFideosSecosDe500GramosVencidos;


  private PersonaHumana unDonanteDePrueba() {
    return new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("dante@example.com")
            .conTelefono("1120304050")
            .build()
        )
        .conDocumento("45459090")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("Dante", "Perez"))
        .conGenero(Genero.MASCULINO)
        .conCalle(calleCualquiera())
        .build();
  }

  private EntidadBeneficiaria unaEntidadDePrueba() {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor Los Pibes")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1145678900")
            .conMail("comedor@lospibes.org")
            .build()
        )
        .conNecesidades(List.of())
        .build();
  }

  @BeforeEach
  void setUp() {
    pantalon = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("pantalon");
    pantalonNuevo = FabricaDeBienes.unBienNuevo(pantalon);
    fideosSecosDe500GramosConVencimientoHoy = FabricaDeBienes.unPerecederoDe500Gramos("fideossecos", enUnMes());
    diezPantalonesNuevos = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(pantalonNuevo, 10);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(fideosSecosDe500GramosConVencimientoHoy, 10);
    diezRemerasUsadas = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(FabricaDeBienes.unaRemeraEnEstado(EstadoDeUso.USADO), 10);
    diezPaquetesDeFideosSecosDe500GramosVencidos = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(FabricaDeBienes.unPerecederoDe500Gramos("fideossecos", haceNDias(31)), 10);
  }

  @Test
  void unaDonacionConDiezUnidadesDeUnBienTiene10UnidadesDeEseBien() {
    assertEquals(10, diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getCantidadDeUnidades());
  }

  @Test
  void unaDonacionSatisfaceLaCantidadDeUnidadesDelBienQueTieneMultiplicadoPorElContenidoDeDichoBien() {
    assertThat(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getCantidadDeSatisfaccion()).isEqualByComparingTo(fideosSecosDe500GramosConVencimientoHoy.getContenido().multiply(BigDecimal.valueOf(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getCantidadDeUnidades())));
  }

  @Test
  void unaDonacionNoSeAgrupaPorUnaSubCategoriaDistintaALaDeSuBien() {
    assertFalse(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.seAgrupaPor(pantalon));
  }

  @Test
  void unaDonacionSeAgrupaPorUnaSubCategoriaSiEsLaMismaQueLaDeSuBien() {
    assertTrue(diezPantalonesNuevos.seAgrupaPor(FabricaDeSubCategorias.getSubCategoriaDeVestimenta("pantalon")));
  }

  @Test
  void cuandoUnaDonacionSeCreaSuEstadoActualCoincideConElDelUltimoCambioDeEstadoDelHistorial() {
    assertEquals(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getEstado(), diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.ultimoCambio().getEstadoDonacion());
  }

  @Test
  void unaDonacionQueSeCreoEnElDepositoYNoTransicionoAOtroEstadoEstaEnDeposito() {
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEnDeposito());
  }

  @Test
  void unaDonacionQuePasoAPendienteDeAsignacionNoEstaEnDeposito() {
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    assertFalse(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEnDeposito());
  }

  @Test
  void laFechaDeLaDonacionCoincideConLaDelPrimerCambioDeEstado() {
    assertEquals(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getFechaDeDonacion(), diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.primerCambio().getFecha());
  }

  @Test
  void laFechaDeLaUltimaTransicionADepositoCoincideConLaDeCreacionSiSoloHuboDosTransiciones() {
    assertEquals(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getFechaDeDonacion(), diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.fechaDeUltimaTransicionA(EN_DEPOSITO).get());
  }
  @Test
  void siSeEstaEnDepositoSePuedePasarAPendienteDeAsignacion() {
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    assertNull(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION));
  }

  @Test
  void siSeEstaEnDepositoSePuedePasarAVencida() {
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.vencer();
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.VENCIDA));
  }

  @Test
  void siSeEstaEnDepositoNoSePuedeAsignarUnaDonacionAUnaEntidadBeneficiaria() {
    assertThrows(TransicionInvalidaDeEstado.class, ()-> {
      diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(unaEntidadDePrueba());
    });
  }

  @Test
  void siSePasaAAlistarParaEntregarSeSigueTeniendoUnaEntidadAsignada() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    assertEquals(asignado, diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.LISTA_PARA_ENTREGAR));
  }

  @Test
  void siSeIniciaElTrasladoSigueTeniendoUnaEntidadAsignada() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.iniciarTraslado("unaEntregaCualquiera");
    assertEquals(asignado, diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.EN_TRASLADO));
  }



  @Test
  void noSePuedeEntregarUnaDonacionQueNoMatcheeConUnaNecesidadDeLaEntidad() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.iniciarTraslado("unaEntregaCualquiera");
    assertThrows(NoHayNecesidadCorrespondiente.class, () -> {
      diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.marcarComoEntregada(new ComprobanteDeEntrega(LocalDateTime.now(), "foto.png"));
    });
  }

  @Test
  void siSeRealizaConExitoSeSigueTeniendoUnAsignado() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    NecesidadExtraordinaria nuevaNecesidad = FabricaDeNecesidades.nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(
            FabricaDeCondicionesDeAceptacion
                .unaDeVigencia(
                    fideosSecosDe500GramosConVencimientoHoy.getSubCategoria()
                ),
            7
    );
    asignado.agregarNecesidad(nuevaNecesidad);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.iniciarTraslado("unaEntregaCualquiera");
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.marcarComoEntregada(new ComprobanteDeEntrega(LocalDateTime.now(), "foto.png"));
    assertEquals(asignado, diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.ENTREGADA));
  }

  @Test
  void siSeFallaEnLaEntregaSeSigueContandoConUnAsignadoYPasaAHaberUnaJustificacion() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.iniciarTraslado("unaEntregaCualquiera");
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.marcarComoFallida("No se encontró el domicilio");
    assertEquals(asignado, diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.ENTREGA_FALLIDA));
    assertNotNull(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getJustificacion());
  }

  @Test
  void siSePasaDeEntregaFallidaAEnDepositoSePierdeElAsignadoYLaJustificacion() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.iniciarTraslado("unaEntregaCualquiera");
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.marcarComoFallida("No se encontró el domicilio");
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.almacenarEnDeposito();
    assertNull(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EN_DEPOSITO));
    assertNull(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getJustificacion());
  }

  @Test
  void siSePasaDeEntregaFallidaAVencidaSeMantieneLaJustificacionPeroSePierdeLaEntidad() {
    EntidadBeneficiaria asignado = unaEntidadDePrueba();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.enSolicitudDeAsignacion();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.asignarA(asignado);
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.alistarParaEntregar();
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.iniciarTraslado("unaEntregaCualquiera");
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.marcarComoFallida("No se encontró el domicilio");
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.vencer();
    assertNull(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getAsignado());
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDonacion.VENCIDA));
    assertNotNull(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.getJustificacion());
  }

  @Test
  void elEstadoDelUltimoCambioCoincideConElEstadoAlQueSeTransicionoMasRecientemente() {
    diezPantalonesNuevos.enSolicitudDeAsignacion();
    assertEquals(PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION, diezPantalonesNuevos.ultimoCambio().getEstadoDonacion());
  }

  @Test
  void elEstadoDelPrimerCambioDeUnaDonacionQueSeCreoEnElDepositoDebeSerEnDeposito() {
    assertEquals(EN_DEPOSITO, diezPantalonesNuevos.primerCambio().getEstadoDonacion());
  }

  @Test
  void unaDonacionDeUnBienPerecederoSigueVigenteSiDichoBienNoEstaVencido() {
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.sigueEnVigencia());
  }

  @Test
  void unaDonacionDeUnBienPerecederoVencidoNoEstaVigente() {
    assertFalse(diezPaquetesDeFideosSecosDe500GramosVencidos.sigueEnVigencia());
  }

  @Test
  void unaDonacionDeUnBienNoPerecederoNuevoSiempreEstaVigente() {
    assertTrue(diezPantalonesNuevos.sigueEnVigencia());
  }

  @Test
  void unaDonacionDeUnBienUsadoSiempreEstaVigente() {
    assertTrue(diezRemerasUsadas.sigueEnVigencia());
  }

  @Test
  void unaDonacionCuyoBienEsNuevoEstaEnNuevo() {
    assertTrue(diezPantalonesNuevos.estaEn(EstadoDeUso.NUEVO));
  }

  @Test
  void unaDonacionCuyoBienEsNuevoNoEstaEnUsado() {
    assertFalse(diezPantalonesNuevos.estaEn(EstadoDeUso.USADO));
  }

  @Test
  void unaDonacionCuyoBienEsUsadoNoEstaEnNuevo() {
    assertFalse(diezRemerasUsadas.estaEn(EstadoDeUso.NUEVO));
  }

  @Test
  void unaDonacionCuyoBienEsUsadoEstaEnUsado() {
    assertTrue(diezRemerasUsadas.estaEn(EstadoDeUso.USADO));
  }

  @Test
  void unaDonacionCuyoBienEsPerecederoYNoEstaVencidoNoEstaEnNuevo() {
    assertFalse(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDeUso.NUEVO));
  }

  @Test
  void unaDonacionCuyoBienEsPerecederoYNoEstaVencidoNoEstaEnUsado() {
    assertFalse(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEn(EstadoDeUso.USADO));
  }

  @Test
  void unaDonacionCuyoBienEsPerecederoYEstaVencidoNoEstaEnNuevo() {
    assertFalse(diezPaquetesDeFideosSecosDe500GramosVencidos.estaEn(EstadoDeUso.NUEVO));
  }

  @Test
  void unaDonacionCuyoBienEsPerecederoYEstaVencidoNoEstaEnUsado() {
    assertFalse(diezPaquetesDeFideosSecosDe500GramosVencidos.estaEn(EstadoDeUso.USADO));
  }

  @Test
  void siSeMarcaCOmoEliminadaPasaAEstarEliminada() {
    diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.marcarComoEliminado();
    assertTrue(diezPaquetesDeFideosSecosDe500GramosConVencimientoEnUnMes.estaEliminado());
  }

}
