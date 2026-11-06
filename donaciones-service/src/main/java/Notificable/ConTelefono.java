package Notificable;

import Telefono.Telefono;

public interface ConTelefono extends ConContacto {
  default Telefono getTelefono() {
    return this.getDatosDeContacto().getTelefono();
  }

  default void setTelefono(Telefono nuevoTelefono) {
    this.getDatosDeContacto().setTelefono(nuevoTelefono);
  }

  default String getNumeroBase() {
    return this.getTelefono().getNumero();
  }
}