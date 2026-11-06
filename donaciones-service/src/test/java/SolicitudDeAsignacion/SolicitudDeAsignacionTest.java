package SolicitudDeAsignacion;

import static EstadoDonacion.EstadoDonacion.ASIGNACION_REALIZADA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeDirecciones.direccionCualquiera;
import static utils.FechasUtils.enNDias;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;

import ExceptionHandler.DatosInvalidosException;
import ExceptionHandler.EntidadNoEsCandidata;
import Necesidades.Necesidad;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeNecesidades;

@ExtendWith(MockitoExtension.class)
public class SolicitudDeAsignacionTest {
  private Necesidad necesitoCincoKilosDeArrozSinVencer;
  private EntidadBeneficiaria candidataPerteneciente;

  private EntidadBeneficiaria candidataNoPerteneciente;
  
  private List<EntidadBeneficiaria> entidadesPertenecientes;
  private SolicitudDeAsignacion solicitudDeAsignacion;

  private DonacionIndependiente diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes;

  @BeforeEach
  void setUp() {
    diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes = FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(enNDias(31));
    necesitoCincoKilosDeArrozSinVencer = FabricaDeNecesidades.necesidadVigenteDe(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes.getSubCategoria(), 5);

    candidataPerteneciente   = new EntidadBeneficiariaBuilder()
        .conRazonSocial("No tiene cura")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conMail("asadoYFernet@gmail.com")
            .conTelefono("1140596878")
            .build()
        )
        .conNecesidades(List.of(necesitoCincoKilosDeArrozSinVencer))
        .build();

    candidataNoPerteneciente = new EntidadBeneficiariaBuilder()
        .conRazonSocial("asd")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conMail("email@gmail.com")
            .conTelefono("11")
            .build()
        )
        .conNecesidades(List.of())
        .build();

    solicitudDeAsignacion = new SolicitudDeAsignacion(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes, List.of(candidataPerteneciente));
  }
  @Nested
  class EsCandidataValida {
    @Test
    void laEntidadEsCandidataSoloSiSeEncuentraEntreLasCandidatasDeLaSolicitud() {
      assertTrue(solicitudDeAsignacion.esCandidataValida(candidataPerteneciente));
    }

    @Test
    void unaEntidadQueNoSeEncuentraEntreLasCandidatasNoEsCandidataValida() {{
      assertFalse(solicitudDeAsignacion.esCandidataValida(candidataNoPerteneciente));
    }
    }
  }
  @Nested
  class Aceptar {
    @Test
    void siLaEntidadNoEsCandidataValidaNoSePuedeAceptarLaSolicitud() {
      assertThrows(DatosInvalidosException.class, () -> {
        solicitudDeAsignacion.aceptar(candidataNoPerteneciente);
      });
    }

    @Nested
    class AceptarConLaSolicitudYaAceptada {
      @BeforeEach
      void setUp() {
        solicitudDeAsignacion.aceptar(candidataPerteneciente);
      }

      @Test
      void cuandoSeAceptaUnaSolicitudLaEntidadQuedaComoEntidadElegida() {
        assertEquals(candidataPerteneciente, solicitudDeAsignacion.getEntidadAsignada());
      }

      @Test
      void cuandoSeAceptaUnaSolicitudLaDonacionQuedaEnEstadoAsignada() {
        assertTrue(diezPaquetesDeArrozDe500GramosConVencimientoEnUnMes.estaEn(ASIGNACION_REALIZADA));
      }

      @Test
      void unaSolicitudYaAceptadaNoSePuedeVolverAAceptar() {
        assertThrows(DatosInvalidosException.class, () -> {
          solicitudDeAsignacion.aceptar(candidataPerteneciente);
        });
      }
    }
  }
}
