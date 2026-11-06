package Notificaciones;

import Contacto.DatosDeContacto;
import MedioDeNotificacion.TipoDeMedio;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("FIJA")
public class NotificacionFija extends Notificacion {
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_de_medio_fijo")
  private TipoDeMedio medio;

  protected NotificacionFija() {}
  public NotificacionFija (String mensaje, DatosDeContacto contacto, TipoDeMedio unMedio) {
    super(mensaje, contacto);
    this.medio = unMedio;
  }

  public TipoDeMedio getMedioDeNotificacion() {
    return this.medio;
  }

  @Override
  public boolean esEnviablePorElMedio(TipoDeMedio unMedioDeNotificacion) {
    return this.medio == unMedioDeNotificacion;
  }
}
