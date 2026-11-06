package ExceptionHandler;


import Donantes.MedioContacto;

public class MedioSinNotificadorException extends RuntimeException {
  public MedioSinNotificadorException(MedioContacto medioContacto) {
    super("No existe un notificador para este medio de contacto: " + medioContacto);
  }
}