package AsignacionDeDonaciones.AlgoritmosDeAsignacion;

import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import java.util.List;

public interface AlgoritmoDeAsignacion {

  List<EntidadBeneficiaria> crearRanking(DonacionIndependiente donacionIndependiente, List<EntidadBeneficiaria> entidadesBeneficiarias);

}
