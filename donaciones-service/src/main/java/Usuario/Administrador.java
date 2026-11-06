package Usuario;

import MedioDeNotificacion.TipoDeMedio;
import Notificable.ConContacto;
import Notificable.ConMailIndividual;
import Notificable.ConMedioPreferencial;
import Notificable.ConTelefono;
import Persistible.Persistible;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import Contacto.DatosDeContacto;

@Entity
@Table(name = "Administradores")
public class Administrador extends Persistible implements ConMailIndividual, ConTelefono, ConMedioPreferencial {
  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_datos_de_contacto")
  private DatosDeContacto datosDeContacto;

  protected Administrador() {
  }

  public Administrador(DatosDeContacto datosDeContacto) {
    this.datosDeContacto = datosDeContacto;
  }

  public DatosDeContacto getDatosDeContacto() {
    return this.datosDeContacto;
  }

}
