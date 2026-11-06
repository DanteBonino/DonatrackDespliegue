package Contacto;

import MedioDeNotificacion.TipoDeMedio;
import Persistible.Persistible;
import Telefono.Telefono;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "DatosDeContacto")
public class DatosDeContacto extends Persistible {

  @ElementCollection
  @Column(name = "mail")
  @CollectionTable(name = "Mails", joinColumns = @JoinColumn(name = "id_datos_de_contacto"))
  private List<String> mails;

  @Embedded
  private Telefono telefono;

  @Enumerated(EnumType.STRING)
  @Column(name = "medio_preferencial")
  private TipoDeMedio medioPreferido;

  public DatosDeContacto(List<String> mails, Telefono telefono, TipoDeMedio medioPreferido) {
    this.mails = new ArrayList<>(mails);
    this.telefono = telefono;
    this.medioPreferido = medioPreferido;
  }

  protected DatosDeContacto () {}

  public List<String> getMailsParaNotificar() {
    return this.mails;
  }

  public Telefono getTelefono() {
    return this.telefono;
  }

  public TipoDeMedio getMedioDeNotificacionPreferencial() {
    return this.medioPreferido;
  }

  public void setTelefono(Telefono nuevoTelefono) {
    this.telefono = nuevoTelefono;
  }

  public void setMails(List<String> mails) {
    this.mails = mails;
  }

  public void agregarMail(String mail) {
    this.mails.add(mail);
  }

  public void setMedioPreferido(TipoDeMedio medioPreferido) {
    this.medioPreferido = medioPreferido;
  }
}
