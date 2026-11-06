package UsuarioFinal;

import Donantes.Donante;
import java.time.LocalDate;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue(value = "DONANTE")
public class UsuarioDonante extends Usuario {
  @OneToOne
  @JoinColumn(name = "id_donante")
  private Donante donante;

  protected UsuarioDonante() {

  }

  public UsuarioDonante(String username, String password, Donante donante, LocalDate fechaUltimoAcceso) {
    super(username, password, fechaUltimoAcceso);
    this.donante = donante;
  }

  public UsuarioDonante(String username, String password, Donante donante) {
    this(username, password, donante, null);
  }

  @Override
  public Rol getRol() {
    return Rol.DONANTE;
  }

  @Override
  public String getNombreAMostrar() {
    return donante.getNombreAMostrar();
  }

  @Override
  public String getIdDeAsociado() {
    return donante.getIdDominio();
  }

  public Donante getDonante() {
    return donante;
  }
}
