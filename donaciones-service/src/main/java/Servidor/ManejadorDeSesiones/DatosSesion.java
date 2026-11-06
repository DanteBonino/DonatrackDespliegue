package Servidor.ManejadorDeSesiones;
import UsuarioFinal.Rol;

public record DatosSesion(
    Rol rol,
    String idUsuario,
    String idAsociado,
    String nombreMostrable
) {
  public boolean esDonante() {
    return rol.equals(Rol.DONANTE);
  }

  public boolean esBeneficiario() {
    return rol.equals(Rol.ENTIDADBENEFICIARIA);
  }

  public boolean esAdministrado() {
    return rol.equals(Rol.ADMINISTRADOR);
  }


}