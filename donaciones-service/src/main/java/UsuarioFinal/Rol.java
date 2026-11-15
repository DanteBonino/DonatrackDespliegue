package UsuarioFinal;

import Contacto.DatosDeContacto;
import Controllers.Web.AdminWebController;
import Controllers.Web.BeneficiariaWebController;
import Controllers.Web.DonanteWebController;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Generales.RepositorioConDatosDeContacto;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;
import java.util.Optional;

public enum Rol implements RouteRole {
  DONANTE {
    @Override public void showDashBoard(Context context) { new DonanteWebController().dashboard(context); }
    @Override public Optional<DatosDeContacto> obtenerDatosDeContacto(Context context) {
      return ManejadorDeSesion.obtenerIdAsoaciado(context)
          .flatMap(DonantesRepository.getInstance()::obtenerDatosDeContactoDe);
    }
  },
  ADMINISTRADOR {
    @Override public void showDashBoard(Context context) { new AdminWebController().dashboard(context); }
    @Override public Optional<DatosDeContacto> obtenerDatosDeContacto(Context context) {
      return ManejadorDeSesion.obtenerIdAsoaciado(context)
          .flatMap(AdministradoresRepository.getInstance()::obtenerDatosDeContactoDe);
    }
  },
  ENTIDADBENEFICIARIA {
    @Override public void showDashBoard(Context context) { new BeneficiariaWebController().dashboard(context); }
    @Override public Optional<DatosDeContacto> obtenerDatosDeContacto(Context context) {
      return ManejadorDeSesion.obtenerIdAsoaciado(context)
          .flatMap(BeneficiariosRepository.getInstance()::obtenerDatosDeContactoDe);
    }
  },
  CUALQUIERA,
  SINSESIONACTIVA,
  CONSESIONACTIVA;

  public void showDashBoard(Context context) {}

  public Optional<DatosDeContacto> obtenerDatosDeContacto(Context context) {
    return Optional.empty();
  }
}