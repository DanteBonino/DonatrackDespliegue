package Repositories.Administradores;

import Repositories.Generales.RepositorioBasico;
import Usuario.Administrador;
import java.util.List;

public class AdministradoresRepository implements IAdministradoresRepository, RepositorioBasico<Administrador> {
  private static final AdministradoresRepository INSTANCE = new AdministradoresRepository();

  public static AdministradoresRepository getInstance() {
    return INSTANCE;
  }

  private AdministradoresRepository() {
  }

  @Override
  public List<Administrador> getInstancias() {
    return RepositorioBasico.super.getInstancias();
  }

  @Override
  public Class<Administrador> clase() {
    return Administrador.class;
  }
}
