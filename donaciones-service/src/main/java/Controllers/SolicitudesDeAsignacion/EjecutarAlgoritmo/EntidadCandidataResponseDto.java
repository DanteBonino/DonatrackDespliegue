package Controllers.SolicitudesDeAsignacion.EjecutarAlgoritmo;

import Controllers.DTOsSinController.Direccion.Response.DireccionDto;
import EntidadesBeneficiarias.EntidadBeneficiaria;

public class EntidadCandidataResponseDto {
  private String id;
  private String razonSocial;
  private DireccionDto direccion;

  public static EntidadCandidataResponseDto desde(EntidadBeneficiaria entidad) {
    EntidadCandidataResponseDto dto = new EntidadCandidataResponseDto();
    dto.id = entidad.getIdDominio();
    dto.razonSocial = entidad.getRazonSocial();
    dto.direccion = DireccionDto.desde(entidad.getDireccion());
    return dto;
  }

  public String getId() { return id; }
  public String getRazonSocial() { return razonSocial; }
  public DireccionDto getDireccion() { return direccion; }
}