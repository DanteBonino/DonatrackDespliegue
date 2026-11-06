package Controllers.EntidadesBenefeciarias;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Controllers.DTOsSinController.Direccion.Request.DireccionRequestDto;
import Controllers.DTOsSinController.Direccion.Response.DireccionDto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import MedioDeNotificacion.TipoDeMedio;
import java.util.List;

public class EntidadBeneficiariaRequestDto implements DatosEntidadesBeneficiarias {
  private String          razonSocial;
  private DireccionRequestDto direccion;
  private List<String>    mails;
  private String          telefono;
  private TipoDeMedio     tipoDeMedio;

  public EntidadBeneficiaria crearInstancia() {
    DatosDeContacto contacto = new DatosDeContactoBuilder()
        .conMails(mails)
        .conMedioPreferido(tipoDeMedio)
        .conTelefono(telefono)
        .build();
    return new EntidadBeneficiariaBuilder()
        .conDireccion(direccion.crearInstancia())
        .conContacto(contacto)
        .conRazonSocial(razonSocial)
        .build();
  }

  public String getRazonSocial() {
    return this.razonSocial;
  }

  public DireccionRequestDto getDireccion() {
    return  this.direccion;
  }

  public List<String> getMails() {
    return  this.mails;
  }

  public String getTelefono() {
    return  this.telefono;
  }

  public TipoDeMedio getTipoDeMedio() {
    return tipoDeMedio;
  }

}
