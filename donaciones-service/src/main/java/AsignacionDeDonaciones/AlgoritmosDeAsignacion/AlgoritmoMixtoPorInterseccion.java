package AsignacionDeDonaciones.AlgoritmosDeAsignacion;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import java.util.List;

public class AlgoritmoMixtoPorInterseccion implements AlgoritmoDeAsignacion{
  private List<AlgoritmoDeAsignacion> algoritmos;

  public AlgoritmoMixtoPorInterseccion(List<AlgoritmoDeAsignacion> algoritmos) {
    this.algoritmos = algoritmos;
  }

  @Override
  public List<EntidadBeneficiaria> crearRanking(DonacionIndependiente donacionIndependiente, List<EntidadBeneficiaria> entidadesBeneficiarias) {
    var interseccionDeAlgoritmos = getInterseccionDeAlgoritmos(donacionIndependiente, entidadesBeneficiarias);

    return interseccionDeAlgoritmos
         .isEmpty()
        ? getResultadosPorAlgoritmo(donacionIndependiente, entidadesBeneficiarias).stream().flatMap(List::stream).distinct().toList()
        : interseccionDeAlgoritmos;
  }

  private List<EntidadBeneficiaria> getInterseccionDeAlgoritmos(DonacionIndependiente donacionIndependiente, List<EntidadBeneficiaria> entidadesBeneficiarias) {
    return getResultadosPorAlgoritmo(donacionIndependiente, entidadesBeneficiarias)
        .stream()
        .reduce(this::interseccion)
        .orElse(List.of());
  }

  private List<List<EntidadBeneficiaria>> getResultadosPorAlgoritmo(DonacionIndependiente donacionIndependiente, List<EntidadBeneficiaria> entidadesBeneficiarias) {
    return this.algoritmos.stream()
        .map(algoritmo -> algoritmo.crearRanking(donacionIndependiente, entidadesBeneficiarias))
        .toList();
  }

  private List<EntidadBeneficiaria> interseccion(List<EntidadBeneficiaria> unasEntidades, List<EntidadBeneficiaria> otrasEntidades) {
    return unasEntidades.stream().filter(otrasEntidades::contains).toList();
  }

  public static AlgoritmoMixtoPorInterseccion algoritmoEstandar() {
    return new AlgoritmoMixtoPorInterseccion(List.of(
        new CompatibilidadSemantica(),
        new PrioridadSubAtendidos()
    ));
  }
}
