package Repositories.Administradores;

import Repositories.Generales.RepositorioBasico;
import Repositories.Generales.RepositorioConDatosDeContacto;
import Usuario.Administrador;
import java.util.List;

public class AdministradoresRepository implements IAdministradoresRepository, RepositorioConDatosDeContacto<Administrador> {
  private static final AdministradoresRepository INSTANCE = new AdministradoresRepository();

  public static AdministradoresRepository getInstance() {
    return INSTANCE;
  }

  private AdministradoresRepository() {
  }

  @Override
  public List<Administrador> getInstancias() {
    return RepositorioConDatosDeContacto.super.getInstancias();
  }

  @Override
  public Class<Administrador> clase() {
    return Administrador.class;
  }
}
