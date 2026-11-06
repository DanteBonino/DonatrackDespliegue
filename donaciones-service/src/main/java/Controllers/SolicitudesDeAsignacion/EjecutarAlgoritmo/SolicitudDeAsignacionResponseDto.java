package Controllers.SolicitudesDeAsignacion.EjecutarAlgoritmo;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Controllers.Donanciones.Get.DonacionResponseDto;
import java.util.List;

public class SolicitudDeAsignacionResponseDto {
  private String id;
  private DonacionResponseDto donacion;
  private List<EntidadCandidataResponseDto> candidatas;
  private String idEntidadAsignada;

  public static SolicitudDeAsignacionResponseDto desde(SolicitudDeAsignacion solicitud) {
    System.out.println("Estamos en desde: " + solicitud.getCandidatas().size());
    SolicitudDeAsignacionResponseDto dto = new SolicitudDeAsignacionResponseDto();
    dto.id = solicitud.getIdDominio();
    dto.donacion = DonacionResponseDto.desde(solicitud.getDonacion());
    dto.candidatas = solicitud.getCandidatas().stream().map(EntidadCandidataResponseDto::desde).toList();
    dto.idEntidadAsignada = solicitud.estaAceptada() ? solicitud.getEntidadAsignada().getIdDominio() : null;
    return dto;
  }

  public String getId() { return id; }
  public DonacionResponseDto getDonacion() { return donacion; }
  public List<EntidadCandidataResponseDto> getCandidatas() { return candidatas; }
  public String getIdEntidadAsignada() { return idEntidadAsignada; }
}