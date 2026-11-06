package EntidadesBeneficiarias;

import Contacto.DatosDeContacto;
import Direccion.Direccion;
import Necesidades.Necesidad;
import Validaciones.Validaciones;
import java.util.ArrayList;
import java.util.List;

public class EntidadBeneficiariaBuilder {
  private String razonSocial;
  private Direccion direccion;
  private DatosDeContacto datosDeContacto;
  private List<Necesidad> necesidades = new ArrayList<>();

  public EntidadBeneficiariaBuilder conRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
    return this;
  }

  public EntidadBeneficiariaBuilder conDireccion(Direccion direccion) {
    this.direccion = direccion;
    return this;
  }

  public EntidadBeneficiariaBuilder conContacto(DatosDeContacto contacto) {
    this.datosDeContacto = contacto;
    return this;
  }

  public EntidadBeneficiariaBuilder conNecesidades(List<Necesidad> necesidades) {
    this.necesidades = necesidades;
    return this;
  }

  public EntidadBeneficiaria build() {
    Validaciones.requerido(razonSocial, "razonSocial");
    Validaciones.requerido(direccion, "direccion");
    Validaciones.requerido(datosDeContacto, "datosDecontacto");
    return new EntidadBeneficiaria(razonSocial, direccion,  datosDeContacto, necesidades);
  }
}