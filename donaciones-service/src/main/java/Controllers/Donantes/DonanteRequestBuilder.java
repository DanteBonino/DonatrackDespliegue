package Controllers.Donantes;

import Controllers.DTOsSinController.Direccion.Request.CalleRequestDto;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import java.time.LocalDate;

public class DonanteRequestBuilder {
  private TipoPersona tipoPersona;
  private String documento;
  private String nombreRazonSocial;
  private String apellido;
  private String email;
  private String telefono;
  private LocalDate fechaDeNacimiento;
  private CalleRequestDto calleRequestDto;
  private Genero genero;
  private String rubro;
  private TipoJuridica tipoJuridico;
  private TipoDeMedio tipoDeMedio;

  public DonanteRequestBuilder tipoPersona(TipoPersona tipoPersona) {
    this.tipoPersona = tipoPersona;
    return this;
  }

  public DonanteRequestBuilder documento(String documento) {
    this.documento = documento;
    return this;
  }

  public DonanteRequestBuilder nombreRazonSocial(String nombreRazonSocial) {
    this.nombreRazonSocial = nombreRazonSocial;
    return this;
  }

  public DonanteRequestBuilder apellido(String apellido) {
    this.apellido = apellido;
    return this;
  }

  public DonanteRequestBuilder email(String email) {
    this.email = email;
    return this;
  }

  public DonanteRequestBuilder telefono(String telefono) {
    this.telefono = telefono;
    return this;
  }

  public DonanteRequestBuilder fechaDeNacimiento(LocalDate fechaDeNacimiento) {
    this.fechaDeNacimiento = fechaDeNacimiento;
    return this;
  }

  public DonanteRequestBuilder calle(CalleRequestDto calleRequestDto) {
    this.calleRequestDto = calleRequestDto;
    return this;
  }

  public DonanteRequestBuilder genero(Genero genero) {
    this.genero = genero;
    return this;
  }

  public DonanteRequestBuilder rubro(String rubro) {
    this.rubro = rubro;
    return this;
  }

  public DonanteRequestBuilder tipoJuridico(TipoJuridica tipoJuridico) {
    this.tipoJuridico = tipoJuridico;
    return this;
  }

  public DonanteRequestBuilder tipoDeMedio(TipoDeMedio tipoDeMedio) {
    this.tipoDeMedio = tipoDeMedio;
    return this;
  }

  public DonanteRequestDto build() {
    return new DonanteRequestDto(
        this.tipoPersona,
        this.documento,
        this.nombreRazonSocial,
        this.apellido,
        this.email,
        this.telefono,
        this.fechaDeNacimiento,
        this.calleRequestDto,
        this.genero,
        this.rubro,
        this.tipoJuridico,
        tipoDeMedio
    );
  }
}
