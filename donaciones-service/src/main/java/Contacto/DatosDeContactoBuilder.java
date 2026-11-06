package Contacto;

import ExceptionHandler.DatosInvalidosException;
import MedioDeNotificacion.TipoDeMedio;
import Telefono.*;
import Validaciones.Validaciones;
import java.util.List;

public class DatosDeContactoBuilder {
  private List<String> mails;
  private TipoDeMedio tipoDeMedio;
  private String telefono;

  public DatosDeContactoBuilder conTelefono(String telefono) {
    this.telefono = telefono;
    return this;
  }

  public DatosDeContactoBuilder conMails(List<String> mails) {
    this.mails = mails;
    return this;
  }

  public DatosDeContactoBuilder conMail(String mail){
    this.mails = List.of(mail);
    return this;
  }

  public DatosDeContactoBuilder conMedioPreferido(TipoDeMedio medio) {
    this.tipoDeMedio = medio;
    return this;
  }

  public DatosDeContacto build() {
    Validaciones.requerido(telefono, "telefono");
    Validaciones.requerido(mails, "mails");
    if(mails.isEmpty()) {
      throw new DatosInvalidosException("Debe haber como mínimo un mail de contacto");
    }
    if(tipoDeMedio == null) {
      tipoDeMedio = TipoDeMedio.MAIL;
    }
    Telefono tel = TelefonoArgentino.nuevoTelefono(telefono);
    return new DatosDeContacto(mails, tel, tipoDeMedio);
  }

}
