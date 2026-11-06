package Telefono;

public class TelefonoArgentino {
  public static Telefono  nuevoTelefono(String numero) {
    return new TelefonoBuilder()
        .conNumero(numero)
        .conCodigoDelPais("54")
        .build();
  }
}
