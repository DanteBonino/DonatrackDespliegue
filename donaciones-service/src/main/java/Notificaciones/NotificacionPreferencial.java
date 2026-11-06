package Notificaciones;


import Contacto.DatosDeContacto;
import MedioDeNotificacion.TipoDeMedio;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PREFERENCIAL")
public class NotificacionPreferencial extends Notificacion {
  protected NotificacionPreferencial() {}
  public NotificacionPreferencial (String mensaje, DatosDeContacto contacto) {
    super(mensaje, contacto);
  }

  public TipoDeMedio getMedioDeNotificacion() {
    return this.getReceptor().getMedioDeNotificacionPreferencial();
  }

  public boolean esEnviablePorElMedio(TipoDeMedio _unMedioDeNotificacion) {
    return true;
  }
}
