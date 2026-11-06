package externo.donaciones;

import dtos.DonacionDto;
import java.util.List;

public interface DonacionesServiceAdapter {
  void notificarInicioDeEntrega(String idEntrega, String idDonacion, String idEntidadBeneficiaria);
  void notificarEntregaCompletada(String idDonacion, String idEntidadBeneficiaria, String comprobanteFoto, String patenteCamion);
  void notificarIncidencia(String idDonacion, String idEntidadBeneficiaria, String motivoNoRecepcion);
  void notificarReprogramacion(String idDonacion);
  List<DonacionDto> obtenerDonacionesListasParaEntregar();
  void notificarDonacionesNoPlanificadas(List<String> listaIdDonaciones);
}