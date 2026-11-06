package Telefono;

import Validaciones.Validaciones;

public class TelefonoBuilder {
  private String numero;
  private String codigoDelPais;

  public TelefonoBuilder conNumero(String numero) {
    this.numero = numero;
    return this;
  }

  public TelefonoBuilder conCodigoDelPais(String codigo) {
    this.codigoDelPais = codigo;
    return this;
  }

  public Telefono build() {
    Validaciones.requerido(numero, "numero");
    Validaciones.requerido(codigoDelPais, "codigoDelPais");
    return new Telefono(numero, codigoDelPais);
  }

}