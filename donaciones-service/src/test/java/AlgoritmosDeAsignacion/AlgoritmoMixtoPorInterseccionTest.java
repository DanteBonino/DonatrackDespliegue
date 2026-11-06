package AlgoritmosDeAsignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utils.FabricaDeDirecciones.direccionCualquiera;


import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoDeAsignacion;
import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoMixtoPorInterseccion;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlgoritmoMixtoPorInterseccionTest{

  @Mock private AlgoritmoDeAsignacion algoritmo1;
  @Mock private AlgoritmoDeAsignacion algoritmo2;
  @Mock private AlgoritmoDeAsignacion algoritmo3;
  private List<EntidadBeneficiaria> entidadesDisponibles;
  private List<EntidadBeneficiaria> ranking;
  @Nested
  class EntidadesMockeadas {
    @Mock private DonacionIndependiente donacion;
    @Mock private EntidadBeneficiaria   entidadA;
    @Mock private EntidadBeneficiaria   entidadB;
    @Mock private EntidadBeneficiaria   entidadC;

    @BeforeEach
    void setUp() {
      entidadesDisponibles = List.of(entidadA, entidadB, entidadC);
    }

    @Test
    void cuandoHayInterseccionNoVaciaDevuelveLaInterseccion() {
      when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadA, entidadB, entidadC));
      when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadB, entidadC));

      var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2));
      ranking = mixto.crearRanking(donacion, entidadesDisponibles);

      assertEquals(List.of(entidadB, entidadC), ranking);
      verify(algoritmo1, times(1)).crearRanking(donacion, entidadesDisponibles);
      verify(algoritmo2, times(1)).crearRanking(donacion, entidadesDisponibles);
    }

    @Test
    void cuandoLaInterseccionEsVaciaDevuelveLaUnionSinDuplicados() {
      when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadA));
      when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadB));

      var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2));

      var resultado = mixto.crearRanking(donacion, entidadesDisponibles);

      assertEquals(List.of(entidadA, entidadB), resultado);
      //Se llama 2 veces cada uno porque el método está definido de forma que se recalcula, si se
      //cambiase este número debería ser 1
      verify(algoritmo1, times(2)).crearRanking(donacion, entidadesDisponibles);
      verify(algoritmo2, times(2)).crearRanking(donacion, entidadesDisponibles);
    }

    @Test
    void cuandoHayUnSoloAlgoritmoDevuelveSuRankingTalCual() {
      when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadC, entidadA));

      var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1));

      var resultado = mixto.crearRanking(donacion, entidadesDisponibles);

      assertEquals(List.of(entidadC, entidadA), resultado);
      verify(algoritmo1, times(1)).crearRanking(donacion, entidadesDisponibles);
    }

    @Test
    void cuandoNoHayAlgoritmosDevuelveListaVacia() {
      var mixto = new AlgoritmoMixtoPorInterseccion(List.of());

      var resultado = mixto.crearRanking(donacion, entidadesDisponibles);

      assertTrue(resultado.isEmpty());
    }

    @Test
    void elRankingCon3AlgoritmosEsLaInterseccionDeLos3() {
      when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadA, entidadB, entidadC));
      when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadB, entidadC));
      when(algoritmo3.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadC));

      var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2, algoritmo3));

      var resultado = mixto.crearRanking(donacion, entidadesDisponibles);

      assertEquals(List.of(entidadC), resultado);
    }

    @Test
    void laUnionEliminaDuplicadosEntreAlgoritmos() {
      when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadA, entidadB));
      when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
          .thenReturn(List.of(entidadB, entidadC));

      var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2));

      var resultado = mixto.crearRanking(donacion, entidadesDisponibles);

      assertEquals(List.of(entidadB), resultado);
    }

    @Nested
    class ConEntidadesSinMockear {
      private DonacionIndependiente donacion;
      private EntidadBeneficiaria entidadA;
      private EntidadBeneficiaria entidadB;
      private EntidadBeneficiaria entidadC;

      @BeforeEach
      void setUp() {
        entidadA = new EntidadBeneficiariaBuilder()
            .conRazonSocial("unaRazon")
            .conDireccion(direccionCualquiera())
            .conContacto( new DatosDeContactoBuilder()
                .conTelefono("1140281989")
                .conMail("unMail@gmail.com")
                .build()
            )
            .conNecesidades(List.of())
            .build();
        entidadB = new EntidadBeneficiariaBuilder()
            .conRazonSocial("razonB")
            .conDireccion(direccionCualquiera())
            .conContacto( new DatosDeContactoBuilder()
                .conTelefono("1140281988")
                .conMail("otroMail@gmail.com")
                .build()
            )
            .conNecesidades(List.of())
            .build();
        entidadC = new EntidadBeneficiariaBuilder()
            .conRazonSocial("razonC")
            .conDireccion(direccionCualquiera())
            .conContacto( new DatosDeContactoBuilder()
                .conTelefono("1140281982")
                .conMail("mailDeC@gmail.com")
                .build()
            )
            .conNecesidades(List.of())
            .build();

        entidadesDisponibles = List.of(entidadA, entidadB, entidadC);
      }

      @Test
      void cuandoHayInterseccionNoVaciaDevuelveLaInterseccion() {
        when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadA, entidadB, entidadC));
        when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadB, entidadC));

        var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2));

        assertEquals(List.of(entidadB, entidadC), mixto.crearRanking(donacion, entidadesDisponibles));
      }

      @Test
      void cuandoLaInterseccionEsVaciaDevuelveLaUnionSinDuplicados() {
        when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadA));
        when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadB));

        var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2));

        assertEquals(List.of(entidadA, entidadB), mixto.crearRanking(donacion, entidadesDisponibles));
      }

      @Test
      void cuandoHayUnSoloAlgoritmoDevuelveSuRankingTalCual() {
        when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadC, entidadA));

        var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1));

        assertEquals(List.of(entidadC, entidadA), mixto.crearRanking(donacion, entidadesDisponibles));
      }

      @Test
      void cuandoNoHayAlgoritmosDevuelveListaVacia() {
        var mixto = new AlgoritmoMixtoPorInterseccion(List.of());

        assertTrue(mixto.crearRanking(donacion, entidadesDisponibles).isEmpty());
      }

      @Test
      void elRankingCon3AlgoritmosEsLaInterseccionDeLos3() {
        when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadA, entidadB, entidadC));
        when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadB, entidadC));
        when(algoritmo3.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadC));

        var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2, algoritmo3));

        assertEquals(List.of(entidadC), mixto.crearRanking(donacion, entidadesDisponibles));
      }

      @Test
      void laUnionEliminaDuplicadosEntreAlgoritmos() {
        when(algoritmo1.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadA, entidadB));
        when(algoritmo2.crearRanking(donacion, entidadesDisponibles))
            .thenReturn(List.of(entidadB, entidadC));

        var mixto = new AlgoritmoMixtoPorInterseccion(List.of(algoritmo1, algoritmo2));

        assertEquals(List.of(entidadB), mixto.crearRanking(donacion, entidadesDisponibles));

      }
    }
  }
}
