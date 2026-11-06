
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeDirecciones.direccionCualquiera;

import Bienes.Bien;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import ExceptionHandler.RecursoNoEncontradoException;
import ExceptionHandler.NoHayNecesidadCorrespondiente;
import Necesidades.Necesidad;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.FabricaDeBienes;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeNecesidades;
import utils.FechasUtils;

class EntidadBeneficiariaTest {
  private Bien paqueteDeArrozDe500Gramos;
  private Bien remeraNueva;
  private Necesidad necesidadDeArrozActiva;
  private Necesidad necesidadDeRemeraNuevaActiva;
  private Necesidad necesidadDeArrozNoActiva;

  private DonacionIndependiente unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad;
  private DonacionIndependiente unPaqueteDe500GramosDeArrozVigente;
  private DonacionIndependiente unaRemeraNueva;
  private EntidadBeneficiaria   entidad;

  @BeforeEach
  void setUp() {
    entidad = new EntidadBeneficiariaBuilder()
        .conRazonSocial("ONG Ejemplo")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conMails(List.of("contacto@ong.org", "otro@ong.org"))
            .conTelefono("1122334455")
            .build()
        )
        .conNecesidades(List.of())
        .build();
    paqueteDeArrozDe500Gramos = FabricaDeBienes.paqueteDeArrozDe500GramosVigente();
    remeraNueva = FabricaDeBienes.remeraNueva();
    unPaqueteDe500GramosDeArrozVigente = FabricaDeDonacionesIndependientes.donacionConBienUnitario(paqueteDeArrozDe500Gramos);
    unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad = FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(FechasUtils.enUnaSemana());
    unaRemeraNueva = FabricaDeDonacionesIndependientes.donacionConBienUnitario(remeraNueva);
    necesidadDeArrozActiva   = FabricaDeNecesidades.necesidadVigenteDe(paqueteDeArrozDe500Gramos.getSubCategoria(), 2000);
    necesidadDeRemeraNuevaActiva = FabricaDeNecesidades.necesidadEnEstadoNuevo(remeraNueva.getSubCategoria(), 20);
    necesidadDeArrozNoActiva = FabricaDeNecesidades.necesidadVigenteDe(paqueteDeArrozDe500Gramos.getSubCategoria(), 4);
  }

  @Nested
  class CantidadDeDonacionesRecibidasEnElUltimoTrimestre {
    @Test
    void unaEntidadNuevaNoTieneDonacionesEnElTrimestre() {
      assertEquals(0, entidad.cantidadDeDonacionesEnElUltimoTrimestre());
    }

    @Test
    void cantidadDeDonacionesEnElUltimoTrimestreSumaLasDeTodasLasNecesidades() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);

      entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigente);
      entidad.reicibirDonacionParaDeterminadaNecesidad(unaRemeraNueva);

      assertEquals(2, entidad.cantidadDeDonacionesEnElUltimoTrimestre());
    }
  }

  @Nested
  class ReicibirDonacionParaDeterminadaNecesidad  {
    @Test
    void reicibirDonacionParaDeterminadaNecesidadLaDerivaALaPrimeraNecesidadCorrespondiente() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);
      entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigente);
      assertEquals(unPaqueteDe500GramosDeArrozVigente.getCantidadDeSatisfaccion(), necesidadDeArrozActiva.cantidadRecibida());
    }

    @Test
    void reicibirDonacionParaDeterminadaNecesidadFallaSiNoHayNecesidadCorrespondiente() {
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);
      assertThrows(NoHayNecesidadCorrespondiente.class,
          () -> entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigente));
      assertThat(necesidadDeRemeraNuevaActiva.cantidadRecibida()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void recibirDonacionParaDeterminadaNecesidadFallaSiSiHayUnaNecesidadCorrespondienteNoActiva() {
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);
      entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad);
      assertThrows(NoHayNecesidadCorrespondiente.class,
          () -> entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigente));
    }
  }

  @Nested
  class CantidadDeNecesidadesQueSatisfaceCon  {
    @Test
    void cantidadDeNecesidadesQueSatisfaceConCuentaSoloLasQueCorrespondenYEstanActivas() {
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);
      entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad);
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);
      assertEquals(1, entidad.cantidadDeNecesidadesQueSatisfaceCon(unPaqueteDe500GramosDeArrozVigente));
    }
  }

  @Nested
  class BuscarNecesidadPara  {
    @Test
    void buscarNecesidadParaEncuentraUnaNecesidadActivaQueCorrespondeConLaDonacion() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      assertEquals(necesidadDeArrozActiva, entidad.buscarNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }

    @Test
    void buscarNecesidadParaIgnoraNecesidadesInactivasAunqueCorrespondan() {
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);
      entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad);
      assertThrows(NoHayNecesidadCorrespondiente.class,
          () -> entidad.buscarNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }

    @Test
    void buscarNecesidadParaIgnoraNecesidadesActivasQueNoCorrespondenALaSubCategoria() {
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);

      assertThrows(NoHayNecesidadCorrespondiente.class,
          () -> entidad.buscarNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }

    @Test
    void buscarNecesidadParaLanzaExcepcionSiNoHayNecesidadesCargadas() {
      assertThrows(NoHayNecesidadCorrespondiente.class,
          () -> entidad.buscarNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }
  }

  @Nested
  class TieneNecesidadPara {

    @Test
    void unaEntidadSinNecesidadNoTieneNingunaNecesidadParaUnaDonacion() {
      assertFalse(entidad.tieneNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }

    @Test
    void unaEntidadTieneNecesidadParaUnaDonacionCuandoLaDonacionCumpleConLaCondicionDeAceptacionYLaNecesidadEstaActiva() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      assertTrue(entidad.tieneNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }

    @Test
    void unaEntidadNoTieneNecesidadParaUnaDonacionSiLaNecesidadQueTieneNoEstaActiva() {
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);
      entidad.reicibirDonacionParaDeterminadaNecesidad(unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad);
      assertFalse(entidad.tieneNecesidadPara(unPaqueteDe500GramosDeArrozVigenteParaDesactivarNecesidad));
    }

    @Test
    void unaEntidadNoTieneNecesidadParaUnaDonacionSiLaNecesidadActivaQueTieneNoCumpleConLaCondicionDeAceptacion() {
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);
      assertFalse(entidad.tieneNecesidadPara(unPaqueteDe500GramosDeArrozVigente));
    }
  }

  @Nested
  class GetNecesidades {

    @Test
    void unaEntidadQueNoAgregoNecesidadesNoTieneNecesidades() {
      assertThat(entidad.getNecesidades()).isEmpty();
    }

    @Test
    void getNecesidadesTodasTraeLasNecesidades() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      entidad.agregarNecesidad(necesidadDeRemeraNuevaActiva);
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);

      assertEquals(3, entidad.getNecesidades().size());
    }
  }

  @Nested
  class BuscarNecesidadPorId {
    @Test
    void lanzaUnaExcepcionDeRecursoNoEncontradoSiNoTieneUnaNecesidadConEseId() {
      assertThrows(RecursoNoEncontradoException.class, () -> {
        entidad.buscarNecesidadPorId("un_id_inventado");
      });
    }

    @Test
    void devuelveLaNecesidadCuyoIdMatchea() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      assertEquals(necesidadDeArrozActiva, entidad.buscarNecesidadPorId(necesidadDeArrozActiva.getIdDominio()));
    }

    @Test
    void lanzaUnaExcepcionSiSeIntentaBuscarUnaNecesidadEliminada() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      necesidadDeArrozActiva.marcarComoEliminado();
      assertThrows(RecursoNoEncontradoException.class, () -> {
        entidad.buscarNecesidadPorId(necesidadDeArrozActiva.getIdDominio());
      });
    }

    @Test
    void devuelveUnaNecesidadCuyoIdMatcheaAunSiNoEstaActiva() {
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);
      assertEquals(necesidadDeArrozNoActiva, entidad.buscarNecesidadPorId(necesidadDeArrozNoActiva.getIdDominio()));
    }
  }

  @Nested
  class EliminarNecesidad {

    @Test
    void lanzaUnaExcepcionSiSeIntentaEliminarUnaNecesidadYNoTieneEsaNecesidad() {
      assertThrows(RecursoNoEncontradoException.class, () -> {
        entidad.eliminarNecesidad("un_id_inexistente");
      });
    }

    @Test
    void dejaDeSerEncontrablePorIdLaNecesidadActivaSiSeLaElimina() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      entidad.eliminarNecesidad(necesidadDeArrozActiva.getIdDominio());
      assertThrows(RecursoNoEncontradoException.class, () -> {
        entidad.buscarNecesidadPorId(necesidadDeArrozActiva.getIdDominio());
      });
    }

    @Test
    void dejaDeSerEncontrablePorIdLaNecesidadInactivaSiSeLaElimina() {
      entidad.agregarNecesidad(necesidadDeArrozNoActiva);
      entidad.eliminarNecesidad(necesidadDeArrozNoActiva.getIdDominio());
      assertThrows(RecursoNoEncontradoException.class, () -> {
        entidad.buscarNecesidadPorId(necesidadDeArrozNoActiva.getIdDominio());
      });
    }

  }

  @Nested
  class AgegarNecesidad {
    @Test
    void agregarNecesidadLaSumaALaListaDeNecesidades() {
      entidad.agregarNecesidad(necesidadDeArrozActiva);
      assertEquals(1, entidad.getNecesidades().size());
    }
  }

}