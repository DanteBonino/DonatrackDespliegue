package Controllers.Donantes;

import Controllers.DTOsSinController.Direccion.Response.CalleDto;
import Controllers.DTOsSinController.Direccion.Response.DireccionDto;
import Donantes.Donante;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.PersonaHumana;
import Donantes.DonanteJuridico.PersonaJuridica;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import java.time.LocalDate;

public class DonanteResponseDto {
  private String id;
  private TipoPersona tipoPersona;
  private String documento;
  private String nombreRazonSocial;
  private String apellido;
  private String email;
  private String telefono;
  private LocalDate fechaDeNacimiento;
  private CalleDto calleDto;
  private Genero genero;
  private String rubro;
  private TipoJuridica tipoJuridico;
  private TipoDeMedio tipoDeMedio;

  private DonanteResponseDto() {}

  public static DonanteResponseDto desde(Donante donante) {
    DonanteResponseDto dto = new DonanteResponseDto();
    dto.id = donante.getIdDominio();
    dto.email = donante.getEmail();
    dto.telefono = donante.getTelefono().getNumero();
    dto.documento = donante.getCaracteristicaDeDiferenciacion();
    dto.tipoDeMedio = donante.getMedioDeNotificacionPreferencial();

    if (donante instanceof PersonaHumana humano) {
      dto.tipoPersona = TipoPersona.HUMANA;
      dto.nombreRazonSocial = humano.getNombre();
      dto.apellido = humano.getApellido();
      dto.fechaDeNacimiento = humano.getFechaDeNacimiento();
      dto.genero = humano.getGenero();
      dto.calleDto = CalleDto.desde(humano.getCalle());
    } else if (donante instanceof PersonaJuridica juridica) {
      dto.tipoPersona = TipoPersona.JURIDICA;
      dto.nombreRazonSocial = juridica.getRazonSocial();
      dto.rubro = juridica.getRubro();
      dto.tipoJuridico = juridica.getTipo();
    }
    return dto;
  }

  public String getId() { return id; }
  public TipoPersona getTipoPersona() { return tipoPersona; }
  public String getDocumento() { return documento; }
  public String getNombreRazonSocial() { return nombreRazonSocial; }
  public String getApellido() { return apellido; }
  public String getEmail() { return email; }
  public String getTelefono() { return telefono; }
  public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }
  public CalleDto getCalleDto() { return calleDto; }
  public Genero getGenero() { return genero; }
  public String getRubro() { return rubro; }
  public TipoJuridica getTipoJuridico() { return tipoJuridico; }
  public TipoDeMedio getTipoDeMedio() {
    return tipoDeMedio;
  }
}