package Controllers.EntidadesBenefeciarias;

import Controllers.DTOsSinController.Direccion.Response.DireccionDto;
import Controllers.Necesidades.NecesidadResponseDto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import MedioDeNotificacion.TipoDeMedio;
import java.util.List;

public class EntidadBeneficiariaResponseDto {
  private String id;
  private String razonSocial;
  private DireccionDto direccion;
  private List<String> mails;
  private String telefono;
  private List<NecesidadResponseDto> necesidades;
  private TipoDeMedio tipoDeMedio;

  public static EntidadBeneficiariaResponseDto desde(EntidadBeneficiaria unaEntidadBeneficiaria) {
    if(unaEntidadBeneficiaria != null) {
      EntidadBeneficiariaResponseDto response = new EntidadBeneficiariaResponseDto();
      response.id = unaEntidadBeneficiaria.getIdDominio();
      response.razonSocial = unaEntidadBeneficiaria.getRazonSocial();
      response.direccion = DireccionDto.desde(unaEntidadBeneficiaria.getDireccion());
      response.mails = unaEntidadBeneficiaria.getMails();
      response.telefono = unaEntidadBeneficiaria.getTelefono().getNumero();
      response.necesidades = unaEntidadBeneficiaria
          .getNecesidades()
          .stream()
          .map(NecesidadResponseDto::desde)
          .toList();
      response.tipoDeMedio = unaEntidadBeneficiaria.getMedioDeNotificacionPreferencial();
      return response;
    }
    return null;
  }

  public List<NecesidadResponseDto> getNecesidades() {
    return necesidades;
  }

  public String getRazonSocial() {
    return this.razonSocial;
  }

  public DireccionDto getDireccion() {
    return  this.direccion;
  }

  public List<String> getMails() {
    return  this.mails;
  }

  public String getTelefono() {
    return  this.telefono;
  }

  public String getId() {
    return  this.id;
  }

  public TipoDeMedio getTipoDeMedio() {
    return this.tipoDeMedio;
  }
}
