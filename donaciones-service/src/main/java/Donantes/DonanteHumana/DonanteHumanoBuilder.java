package Donantes.DonanteHumana;

import Contacto.DatosDeContacto;
import Direccion.Calle;
import Validaciones.Validaciones;
import java.time.LocalDate;
import java.util.Objects;


public class DonanteHumanoBuilder {
  private NombreCompleto nombreCompleto;
  private LocalDate fechaDeNacimiento;
  private Genero genero;
  private Calle calle;
  private String documento;
  private DatosDeContacto contacto;

  public DonanteHumanoBuilder conNombreCompleto(NombreCompleto nombreCompleto) {
    this.nombreCompleto = nombreCompleto;
    return this;
  }

  public DonanteHumanoBuilder conFechaDeNacimiento (LocalDate fechaDeNacimiento) {
    this.fechaDeNacimiento = fechaDeNacimiento;
    return this;
  }

  public DonanteHumanoBuilder conGenero(Genero genero) {
    this.genero = Objects.requireNonNullElse(genero, Genero.X);
    return this;
  }

  public DonanteHumanoBuilder conCalle(Calle calle) {
    this.calle = calle;
    return this;
  }

  public DonanteHumanoBuilder conDocumento(String documento) {
    this.documento = documento;
    return this;
  }

  public DonanteHumanoBuilder conContacto(DatosDeContacto contacto) {
    this.contacto = contacto;
    return this;
  }

//  public DonanteHumanoBuilder conMedioDePreferencia(MedioDeNotificacion unMedio) {
//    if(unMedio == null) {
//      this.
//    }
//  }

  public PersonaHumana build() {
    Validaciones.requerido(nombreCompleto, "nombreCompleto");
    Validaciones.requerido(documento, "documento");
    Validaciones.requerido(fechaDeNacimiento, "fechaDeNacimiento");
    Validaciones.requerido(genero, "genero");
    Validaciones.requerido(calle, "direccion");
    Validaciones.requerido(contacto, "contacto");

    return new PersonaHumana(
        this.nombreCompleto.getNombre(),
        this.nombreCompleto.getApellido(),
        this.documento,
        this.contacto,
        this.fechaDeNacimiento,
        this.genero,
        this.calle
    );
  }

}
