package Notificable;

import MedioDeNotificacion.TipoDeMedio;

public interface ConMedioPreferencial extends ConContacto {
  default void setMedioPreferido(TipoDeMedio nuevoMedio) {
    this.getDatosDeContacto().setMedioPreferido(nuevoMedio);
  }

  default TipoDeMedio getMedioDeNotificacionPreferencial() {
    return this.getDatosDeContacto().getMedioDeNotificacionPreferencial();
  }
}
