package Controllers.Donantes;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Controllers.DTOsSinController.Direccion.Request.CalleRequestDto;
import DTOs.DatosDonante;
import Direccion.Calle;
import Donantes.Donante;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import java.time.LocalDate;

public class DonanteRequestDto implements DatosDonante {
  protected TipoPersona tipoPersona;
  protected String documento;
  protected String nombreRazonSocial;
  protected String apellido;
  protected String email;
  protected String telefono;
  protected LocalDate fechaDeNacimiento;
  protected CalleRequestDto calle;
  protected Genero genero;
  protected String rubro;
  protected TipoJuridica tipoJuridico;
  protected TipoDeMedio tipoDeMedio;

  public DonanteRequestDto(
      TipoPersona tipo,
      String documento,
      String nombreRazonSocial,
      String apellido,
      String email,
      String telefono,
      LocalDate fechaDeNacimiento,
      CalleRequestDto calleRequestDto,
      Genero genero,
      String rubro,
      TipoJuridica tipoJuridica,
      TipoDeMedio tipoDeMedio
      ) {
      this.tipoPersona = tipo;
      this.documento = documento;
      this.nombreRazonSocial = nombreRazonSocial;
      this.apellido = apellido;
      this.telefono = telefono;
      this.email = email;
      this.fechaDeNacimiento = fechaDeNacimiento;
      this.calle = calleRequestDto;
      this.genero = genero;
      this.rubro = rubro;
      this.tipoJuridico = tipoJuridica;
      this.tipoDeMedio = tipoDeMedio;
  }

  public DonanteRequestDto() {

  }


  public Donante crearInstancia() {
    DatosDeContacto contacto = new DatosDeContactoBuilder()
        .conMail(email)
        .conTelefono(telefono)
        .conMedioPreferido(tipoDeMedio)
        .build();

    return Donante.crear(
        tipoPersona,
        new NombreCompleto(nombreRazonSocial, apellido),
        documento,
        contacto,
        fechaDeNacimiento,
        genero,
        getCalle(),
        rubro,
        tipoJuridico
    );
  }

  public TipoPersona getTipoPersona() { return tipoPersona; }
  public String getDocumento() { return documento; }
  public String getNombreRazonSocial() { return nombreRazonSocial; }
  public String getApellido() { return apellido; }
  public String getEmail() { return email; }
  public String getTelefono() { return telefono; }
  public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }
  public Calle getCalle() { return calle.crearInstancia(); }
  public CalleRequestDto getCalleDto() {
     return calle;
  }
  public Genero getGenero() { return genero; }
  public String getRubro() { return rubro; }
  public TipoJuridica getTipoJuridico() { return tipoJuridico; }
  public TipoDeMedio getTipoDeMedio() {
    return this.tipoDeMedio;
  }
}