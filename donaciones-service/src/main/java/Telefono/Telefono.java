package Telefono;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Telefono {
  @Column(name = "numero_de_telefono")
  private String numero;

  @Column(name = "codigo_del_pais")
  private String codigoDePais;

  protected Telefono() {

  }

  public Telefono(String numero, String codigoDePais) {
    this.numero = numero;
    this.codigoDePais = codigoDePais;
  }

  public String getNumeroSms() {
    return getNumeroValido(codigoDePais + numero);
  }

  public String getNumeroWpp() {
    return getNumeroValido(codigoDePais + "9" + numero);
  }

  private String getNumeroValido(String numero) {
    return "+" + numero;
  }

  public String getNumero() {
    return numero;
  }
}