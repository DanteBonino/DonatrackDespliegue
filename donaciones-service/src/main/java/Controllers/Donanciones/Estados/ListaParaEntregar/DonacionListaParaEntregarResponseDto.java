package Controllers.Donanciones.Estados.ListaParaEntregar;

import static EstadoDonacion.EstadoDonacion.ASIGNACION_REALIZADA;

import Controllers.DTOsSinController.Direccion.Response.DireccionDto;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import java.time.LocalDateTime;

public class DonacionListaParaEntregarResponseDto {
  private String idDonacion;
  private String idEntidadBeneficiaria;
  private DireccionDto direccionDestino;
  private LocalDateTime enEsperaDesde;

  public static DonacionListaParaEntregarResponseDto desde(DonacionIndependiente donacion) {
    DonacionListaParaEntregarResponseDto dto = new DonacionListaParaEntregarResponseDto();
    EntidadBeneficiaria entidad = donacion.getAsignado();
    dto.idDonacion = donacion.getIdDominio();
    dto.idEntidadBeneficiaria = entidad.getIdDominio();
    dto.direccionDestino = DireccionDto.desde(entidad.getDireccion());
    dto.enEsperaDesde = donacion.fechaDeUltimaTransicionA(ASIGNACION_REALIZADA).get();
    return dto;
  }

  public String getIdDonacion() { return idDonacion; }
  public String getIdEntidadBeneficiaria() { return idEntidadBeneficiaria; }
  public DireccionDto getDireccionDestino() { return direccionDestino; }
  public LocalDateTime getEnEsperaDesde() {
    return this.enEsperaDesde;
  }
}
