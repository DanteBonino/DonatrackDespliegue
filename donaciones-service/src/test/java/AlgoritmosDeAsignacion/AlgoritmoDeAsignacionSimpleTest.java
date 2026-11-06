package AlgoritmosDeAsignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FechasUtils.elDiaSiguiente;

import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoDeAsignacionSimple;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;

class AlgoritmoDeAsignacionSimpleTest {

  // Esto es para poder instanciar la clase abstracta
  private final AlgoritmoDeAsignacionSimple algoritmo = new AlgoritmoDeAsignacionSimple() {
    @Override
    protected Comparator<EntidadBeneficiaria> comparadorPara(DonacionIndependiente donacion) {
      return Comparator.comparing(Object::hashCode);
    }
  };

  private DonacionIndependiente unaDonacion() {
    return FabricaDeDonacionesIndependientes.diezPaquetesDeArrozDe500GramosConVencimientoEn(elDiaSiguiente());
  }

  private EntidadBeneficiaria entidadCompatibleCon(DonacionIndependiente donacion) {
    return FabricaDeEntidadesBeneficiarias.crearNCandidatasPara(donacion, 1).get(0);
  }

  @Test
  void limitaElRankingADiezEntidades() {
    DonacionIndependiente donacion = unaDonacion();

    var entidades = IntStream.range(0, 15)
        .mapToObj(i -> entidadCompatibleCon(donacion))
        .toList();

    var ranking = algoritmo.crearRanking(donacion, entidades);

    assertEquals(10, ranking.size());
  }

  @Test
  void conListaVaciaDevuelveListaVacia() {
    DonacionIndependiente donacion = unaDonacion();
    assertTrue(algoritmo.crearRanking(donacion, List.of()).isEmpty());
  }
}
