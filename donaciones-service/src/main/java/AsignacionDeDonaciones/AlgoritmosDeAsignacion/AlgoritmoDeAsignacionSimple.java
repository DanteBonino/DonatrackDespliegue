package AsignacionDeDonaciones.AlgoritmosDeAsignacion;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import java.util.Comparator;
import java.util.List;

abstract public class AlgoritmoDeAsignacionSimple implements AlgoritmoDeAsignacion {

  @Override
  public List<EntidadBeneficiaria> crearRanking(DonacionIndependiente donacion, List<EntidadBeneficiaria> entidadesBeneficiarias) {
    return entidadesBeneficiarias.stream()
        .filter(entidad -> entidad.tieneNecesidadPara(donacion))
        .sorted(this.comparadorPara(donacion))
        .limit(10)
        .toList();
  }

  protected abstract Comparator<EntidadBeneficiaria> comparadorPara(DonacionIndependiente donacion);
}