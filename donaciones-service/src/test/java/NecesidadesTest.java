import Bienes.Bien;
import Donaciones.DonacionIndependiente;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.NecesidadExtraordinaria;
import Necesidades.NecesidadRecurrente;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.FabricaDeBienes;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeNecesidades;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FechasUtils.ayer;
import static utils.FechasUtils.elDiaSiguiente;

@DisplayName("Tests de Necesidades")
class NecesidadesTest {
  private Bien remeraNueva;
  private DonacionIndependiente donacionDe10RemerasNuevas;
  private DonacionIndependiente donacionDe25RemerasNuevas;
  private DonacionIndependiente donacionSupremaDeRemerasNuevas;
  private CondicionDeAceptacion soloRemerasNuevas;
  private BigDecimal cantidadRequerida = BigDecimal.valueOf(35);

  @BeforeEach
  void setUp() {
    remeraNueva = FabricaDeBienes.remeraNueva();
    donacionDe10RemerasNuevas = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(remeraNueva, 10);
    donacionDe25RemerasNuevas  = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(remeraNueva, 25);
    donacionSupremaDeRemerasNuevas = FabricaDeDonacionesIndependientes.donacionConBienYCiertaCantidadDeUnidades(remeraNueva, 1000);
    soloRemerasNuevas = FabricaDeCondicionesDeAceptacion.Nuevo(remeraNueva.getSubCategoria());
  }

  @Nested
  @DisplayName("NecesidadesExtraordinarias")
  class NecesidadExtraordinariaTest {

    private NecesidadExtraordinaria necesidad;

    @BeforeEach
    void setUp() {
      necesidad = FabricaDeNecesidades.nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(soloRemerasNuevas, 35);
    }

    @Nested
    class estaSatisfecha {
      @Test
      void sinRecibirBienesNoEstaSatisfecha() {
        assertFalse(necesidad.estaSatisfecha());
      }

      @Test
      void noEstaSatisfechaConDonacionesParciales() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        assertFalse(necesidad.estaSatisfecha());
      }

      @Test
      void estaSatisfechaAlIgualarLaCantidadRequerida() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        necesidad.recibirBien(donacionDe25RemerasNuevas);
        assertTrue(necesidad.estaSatisfecha());
      }

      @Test
      void estaSatisfechaAlSuperarLaCantidadRequerida() {
        necesidad.recibirBien(donacionSupremaDeRemerasNuevas);
        assertTrue(necesidad.estaSatisfecha());
      }
    }

    @Nested
    class estaActiva {

      @Test
      void sinRecibirBienesEstaActiva() {
        assertTrue(necesidad.estaActiva());
      }

      @Test
      void sigueActivaSiRecibeDonacionesPeroNoLaSatisfacenCompletamente() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        assertTrue(necesidad.estaActiva());
      }

      @Test
      void noEstaActivaCuandoEstaSatisfecha() {
        necesidad.recibirBien(donacionSupremaDeRemerasNuevas);
        assertFalse(necesidad.estaActiva());
      }

    }

    @Nested
    class cantidadRecibida {
      @Test
      void cantidadRecibidaEsCeroSinDonaciones() {
        assertThat(necesidad.cantidadRecibida()).isEqualByComparingTo(BigDecimal.ZERO);
      }

      @Test
      void recibirUnaDonacionAumentaLaCantidadRecibidaEnTantaCantidadDeSatisfaccionTengaLaDonacion() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        assertThat(necesidad.cantidadRecibida()).isEqualByComparingTo(donacionDe10RemerasNuevas.getCantidadDeSatisfaccion());
      }
    }


    @Test
    void laCantidadRequeridaEsLaDeclaradaAlCreala() {
      assertThat(necesidad.cantidadRequerida()).isEqualByComparingTo(BigDecimal.valueOf(35));
    }

    @Nested
    class bienesRecibidosActuales {
      @Test
      void noHayBienesRecibidosSiNoSeRecibioNinguno() {
        assertThat(necesidad.bienesRecibidosActuales()).isEmpty();
      }

      @Test
      void todosLosBienesRecibidosHistoricamenteSonLosActuales() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        necesidad.recibirBien(donacionDe25RemerasNuevas);
        assertEquals(2, necesidad.bienesRecibidosActuales().size());
      }

    }

    @Nested
    class cantidadDeDonacionesRecibidasDespuesDeXTiempo {
        @Test
        void noSeRecibieronDonacionesEnElFuturo() {
          assertEquals(0, necesidad.cantidadDeDonacionesRecibidasDespuesDe(elDiaSiguiente()));
        }

        @Test
        void siSeRecibeUnaDonacionHoyEntoncesCualquierFechaAnteriorLaCuentaEnElHistorico() {
          necesidad.recibirBien(donacionDe10RemerasNuevas);
          assertEquals(1, necesidad.cantidadDeDonacionesRecibidasDespuesDe(ayer()));
        }
    }

  }

  @Nested
  @DisplayName("NecesidadesRecurrentes")
  class NecesidadRecurrenteTest {

    private NecesidadRecurrente necesidad;

    @BeforeEach
    void setUp() {
      necesidad = FabricaDeNecesidades.nuevaNecesidadRecurrenteSemanalConCantidadExacta(soloRemerasNuevas, 35);
    }

    @Nested
    class estaSatisfecha {
      @Test
      void noEstaSatisfechaConDonacionesParciales() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        assertFalse(necesidad.estaSatisfecha());
      }

      @Test
      void estaSatisfechaConDonacionesDelPeriodoActual() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        necesidad.recibirBien(donacionDe25RemerasNuevas);
        assertTrue(necesidad.estaSatisfecha());
      }

      @Test
      void estaSatisfechaAlSuperarLaCantidadRequerida() {
        necesidad.recibirBien(donacionSupremaDeRemerasNuevas);
        assertTrue(necesidad.estaSatisfecha());
      }
    }

    @Nested
    class estaActiva {

      @Test
      void sinRecibirBienesEstaActiva() {
        assertTrue(necesidad.estaActiva());
      }

      @Test
      void sigueActivaSiRecibeDonacionesDuranElPeriodoPeroNoLaSatisfacenCompletamente() {
        necesidad.recibirBien(donacionDe10RemerasNuevas);
        assertTrue(necesidad.estaActiva());
      }

      @Test
      void noEstaActivaCuandoEstaSatisfecha() {
        necesidad.recibirBien(donacionSupremaDeRemerasNuevas);
        assertFalse(necesidad.estaActiva());
      }

    }
  }
}
