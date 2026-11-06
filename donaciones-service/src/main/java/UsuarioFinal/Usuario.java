package UsuarioFinal;

import static utils.SeguridadUtils.hashSha256;

import Persistible.Persistible;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import utils.FechasUtils;

@Entity
@Table(name = "Usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
abstract public class Usuario extends Persistible implements ConSesion {
  @Column(name = "username", unique = true, nullable = false)
  private String username;
  @Column(name = "password", nullable = false)
  private String hashpassword;
  @Column(name = "ultimo_acceso")
  private LocalDate ultimoAcceso;

  protected Usuario() {}

  public Usuario(String username, String password, LocalDate fechaUltimoAcceso) {
    this.username = username;
    this.hashpassword = hashSha256(password);
    this.ultimoAcceso = fechaUltimoAcceso;
  }

  public Usuario(String username, String password) {
    this(username, password, null);
  }

  public String getUsername() {
    return username;
  }

  public String getHashpassword() {
    return hashpassword;
  }

  public Boolean verificarContrasenia(String otraContrasenia) {
    return this.hashpassword.equals(hashSha256(otraContrasenia));
  }

  public void registrarIngreso() {
    this.ultimoAcceso = FechasUtils.hoy();
  }

  public LocalDate getUltimoAcceso() {
    return ultimoAcceso;
  }

  public abstract Rol getRol();

  public abstract String getNombreAMostrar();

  public abstract String getIdDeAsociado();

}
