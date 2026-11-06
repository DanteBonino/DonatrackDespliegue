package UsuarioFinal;
import Usuario.Administrador;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue(value = "ADMINISTRADOR")
public class UsuarioAdministrador extends Usuario {
  @OneToOne
  @JoinColumn(name = "id_administrador")
  private Administrador administrador;

  protected UsuarioAdministrador() {

  }

  public UsuarioAdministrador(String username, String password, Administrador administrador) {
    super(username, password);
    this.administrador = administrador;
  }

  @Override
  public Rol getRol() {
    return Rol.ADMINISTRADOR;
  }

  @Override
  public String getNombreAMostrar() {
    return getUsername();
  }

  @Override
  public String getIdDeAsociado() {
    return administrador.getIdDominio();
  }
}
