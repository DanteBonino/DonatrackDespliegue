package Controllers.Donanciones.Estados.AsignacionRealizada;

import Controllers.DTOsSinController.Direccion.Response.DireccionDto;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;

public class DonacionAsignacionRealizadaResponseDto {
  private String idDonacion;
  private String idEntidadBeneficiaria;
  private DireccionDto direccionDestino;

  public static DonacionAsignacionRealizadaResponseDto desde(DonacionIndependiente donacion) {
    DonacionAsignacionRealizadaResponseDto dto = new DonacionAsignacionRealizadaResponseDto();
    EntidadBeneficiaria entidad = donacion.getAsignado();
    dto.idDonacion = donacion.getIdDominio();
    dto.idEntidadBeneficiaria = entidad.getIdDominio();
    dto.direccionDestino = DireccionDto.desde(entidad.getDireccion());
    return dto;
  }

  public String getIdDonacion() { return idDonacion; }
  public String getIdEntidadBeneficiaria() { return idEntidadBeneficiaria; }
  public DireccionDto getDireccionDestino() { return direccionDestino; }
}
