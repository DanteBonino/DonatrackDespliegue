package AlgoritmosDeAsignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeEntidadesBeneficiarias.agregarNNecesidadesCompatiblesConA;
import static utils.FechasUtils.elDiaSiguiente;

import AsignacionDeDonaciones.AlgoritmosDeAsignacion.CompatibilidadSemantica;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;

class CompatibilidadSemanticaTest {
  private DonacionIndependiente diezPaquetesDeArrozConVencimientoMañana;
  private EntidadBeneficiaria conCantidadModerada;
  private EntidadBeneficiaria conCantidadInfima;
  private EntidadBeneficiaria conCantidadAbundante;
  private EntidadBeneficiaria conMismaCantidad1;
  private EntidadBeneficiaria conMismaCantidad2;
  private CompatibilidadSemantica algoritmo;



  @BeforeEach
  void setUp() {
    diezPaquetesDeArrozConVencimientoMañana = FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(elDiaSiguiente());
    conCantidadInfima = FabricaDeEntidadesBeneficiarias.unaEntidad(1, List.of());
    conCantidadModerada = FabricaDeEntidadesBeneficiarias.unaEntidad(2, List.of());
    conCantidadAbundante = FabricaDeEntidadesBeneficiarias.unaEntidad(3, List.of());
    conMismaCantidad1 = FabricaDeEntidadesBeneficiarias.unaEntidad(4, List.of());
    conMismaCantidad2 = FabricaDeEntidadesBeneficiarias.unaEntidad(5, List.of());
    algoritmo = new CompatibilidadSemantica();
  }

  @Test
  void ordenaDeMayorAMenorLasEntidadesSegunLaCantidadDeNecesidadesPublicadasQuePidanElTipoDeBienDeLaDonacionIndependiente() {

    agregarNNecesidadesCompatiblesConA(1, diezPaquetesDeArrozConVencimientoMañana, conCantidadInfima);
    agregarNNecesidadesCompatiblesConA(10, diezPaquetesDeArrozConVencimientoMañana, conCantidadModerada);
    agregarNNecesidadesCompatiblesConA(100, diezPaquetesDeArrozConVencimientoMañana, conCantidadAbundante);

    var entidades = List.of(conCantidadAbundante, conCantidadInfima, conCantidadModerada);
    assertEquals(
        List.of(conCantidadAbundante, conCantidadModerada, conCantidadInfima),
        algoritmo.crearRanking(diezPaquetesDeArrozConVencimientoMañana, entidades)
    );
  }


  @Test
  void conEntidadesConIgualCantidadLasMantieneEnElRanking() {
    agregarNNecesidadesCompatiblesConA(5, diezPaquetesDeArrozConVencimientoMañana, conMismaCantidad1);
    agregarNNecesidadesCompatiblesConA(5, diezPaquetesDeArrozConVencimientoMañana, conMismaCantidad2);
    var ranking = algoritmo.crearRanking(diezPaquetesDeArrozConVencimientoMañana, List.of(conMismaCantidad1, conMismaCantidad2));

    assertEquals(2, ranking.size());
    assertTrue(ranking.containsAll(List.of(conMismaCantidad1, conMismaCantidad2)));
  }
}