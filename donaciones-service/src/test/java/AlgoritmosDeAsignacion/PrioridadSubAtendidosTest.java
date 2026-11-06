package AlgoritmosDeAsignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FabricaDeEntidadesBeneficiarias.agregarNNecesidadesCompatiblesConA;
import static utils.FechasUtils.enUnMes;

import AsignacionDeDonaciones.AlgoritmosDeAsignacion.PrioridadSubAtendidos;
import Bienes.EstadoDeUso;
import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import EstadoDonacion.EstadoDonacion;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Necesidades.NecesidadExtraordinariaBuilder;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;

class PrioridadSubAtendidosTest {
  private DonacionIndependiente diezPaquetesDeArroz500gVenceEnUnMes;
  private PrioridadSubAtendidos algoritmo;
  private EntidadBeneficiaria pocoAtendida;
  private EntidadBeneficiaria moderadamenteAtendida;
  private EntidadBeneficiaria frecuentementeAtendida;
  private EntidadBeneficiaria conMismaCantidadDeAtencion1;
  private EntidadBeneficiaria conMismaCantidadDeAtencion2;

  @BeforeEach
  void setUp() {
    algoritmo = new PrioridadSubAtendidos();
    diezPaquetesDeArroz500gVenceEnUnMes = FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(enUnMes());
    pocoAtendida = FabricaDeEntidadesBeneficiarias.unaEntidad(1, List.of());
    moderadamenteAtendida = FabricaDeEntidadesBeneficiarias.unaEntidad(2, List.of());
    frecuentementeAtendida = FabricaDeEntidadesBeneficiarias.unaEntidad(3, List.of());
    conMismaCantidadDeAtencion1 = FabricaDeEntidadesBeneficiarias.unaEntidad(4, List.of());
    conMismaCantidadDeAtencion2 = FabricaDeEntidadesBeneficiarias.unaEntidad(5, List.of());
  }

  private void recibirNVecesLaDonacion(int cantidadDeVeces, EntidadBeneficiaria receptora, DonacionIndependiente donacionIndependiente) {
    for (int i = 0; i < cantidadDeVeces; i++) receptora.reicibirDonacionParaDeterminadaNecesidad(donacionIndependiente);
  }

  @Test
  void ordenaDeMenorAMayorCantidadDeDonacionesEnElTrimestre() {
    agregarNNecesidadesCompatiblesConA(1, diezPaquetesDeArroz500gVenceEnUnMes, pocoAtendida);
    agregarNNecesidadesCompatiblesConA(10, diezPaquetesDeArroz500gVenceEnUnMes, moderadamenteAtendida);
    agregarNNecesidadesCompatiblesConA(100, diezPaquetesDeArroz500gVenceEnUnMes, frecuentementeAtendida);


    recibirNVecesLaDonacion(1, pocoAtendida, diezPaquetesDeArroz500gVenceEnUnMes);
    recibirNVecesLaDonacion(5, moderadamenteAtendida, diezPaquetesDeArroz500gVenceEnUnMes);
    recibirNVecesLaDonacion(20, frecuentementeAtendida, diezPaquetesDeArroz500gVenceEnUnMes);

    var entidades = List.of(frecuentementeAtendida, pocoAtendida, moderadamenteAtendida);

    assertEquals(
        List.of(pocoAtendida, moderadamenteAtendida, frecuentementeAtendida),
        algoritmo.crearRanking(diezPaquetesDeArroz500gVenceEnUnMes, entidades)
    );
  }


  @Test
  void conEntidadesConIgualCantidadLasMantieneEnElRanking() {
    agregarNNecesidadesCompatiblesConA(1, diezPaquetesDeArroz500gVenceEnUnMes, conMismaCantidadDeAtencion1);
    agregarNNecesidadesCompatiblesConA(1, diezPaquetesDeArroz500gVenceEnUnMes, conMismaCantidadDeAtencion2);
    recibirNVecesLaDonacion(1, conMismaCantidadDeAtencion1, diezPaquetesDeArroz500gVenceEnUnMes);
    recibirNVecesLaDonacion(1, conMismaCantidadDeAtencion2, diezPaquetesDeArroz500gVenceEnUnMes);
    var ranking = algoritmo.crearRanking(diezPaquetesDeArroz500gVenceEnUnMes, List.of(conMismaCantidadDeAtencion1, conMismaCantidadDeAtencion2));
    assertEquals(2, ranking.size());
    assertTrue(ranking.containsAll(List.of(conMismaCantidadDeAtencion1, conMismaCantidadDeAtencion2)));
  }
}