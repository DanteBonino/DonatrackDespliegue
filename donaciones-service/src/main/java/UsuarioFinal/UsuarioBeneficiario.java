package UsuarioFinal;

import EntidadesBeneficiarias.EntidadBeneficiaria;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue(value = "ENTIDADBENEFICIARIA")
public class UsuarioBeneficiario extends Usuario{
  @OneToOne
  @JoinColumn(name = "id_beneficiario")
  private EntidadBeneficiaria beneficiario;

  protected UsuarioBeneficiario() {}

  public UsuarioBeneficiario(String username, String password, EntidadBeneficiaria entidadBeneficiaria) {
    super(username, password);
    this.beneficiario = entidadBeneficiaria;
  }

  @Override
  public Rol getRol() {
    return Rol.ENTIDADBENEFICIARIA;
  }

  @Override
  public String getNombreAMostrar() {
    return beneficiario.getRazonSocial();
  }

  @Override
  public String getIdDeAsociado() {
    return beneficiario.getIdDominio();
  }
}
