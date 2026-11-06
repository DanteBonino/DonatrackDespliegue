package Donantes.DonanteJuridico;

import Contacto.DatosDeContacto;
import Validaciones.Validaciones;

public class DonanteJuridicoBuilder {
  private String razonSocial;
  private TipoJuridica tipoJuridica;
  private String rubro;
  private String cuit;
  private DatosDeContacto contacto;

  public DonanteJuridicoBuilder conRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
    return this;
  }

  public DonanteJuridicoBuilder conTipoJuridico (TipoJuridica tipo) {
    this.tipoJuridica = tipo;
    return this;
  }

  public DonanteJuridicoBuilder conRubro(String rubro) {
    this.rubro = rubro;
    return this;
  }

  public DonanteJuridicoBuilder conCuit(String cuit) {
    this.cuit = cuit;
    return this;
  }

  public DonanteJuridicoBuilder conContacto(DatosDeContacto contacto) {
    this.contacto = contacto;
    return this;
  }

  public PersonaJuridica build() {
    Validaciones.requerido(razonSocial, "razonSocial");
    Validaciones.requerido(tipoJuridica, "tipoJuridico");
    Validaciones.requerido(rubro, "rubro");
    Validaciones.requerido(cuit, "cuit");
    Validaciones.requerido(contacto, "contacto");

    return new PersonaJuridica(
        this.razonSocial,
        this.cuit,
        this.contacto,
        this.tipoJuridica,
        this.rubro
    );
  }
}
