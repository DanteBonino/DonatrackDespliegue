package AsignacionDeDonaciones.AlgoritmosDeAsignacion;


import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import java.util.Comparator;

public class PrioridadSubAtendidos extends AlgoritmoDeAsignacionSimple {

  @Override
  protected Comparator<EntidadBeneficiaria> comparadorPara(DonacionIndependiente donacion) {
    return Comparator.comparing((EntidadBeneficiaria::cantidadDeDonacionesEnElUltimoTrimestre));
  }
}
