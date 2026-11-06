package Repositories;
import Repositories.Generales.RepositorioBasico;
import UsuarioFinal.Usuario;
import java.util.List;
import java.util.Optional;

public class UsuariosRepository implements  RepositorioBasico<Usuario> {
  private static final UsuariosRepository INSTANCE = new UsuariosRepository();

  public static UsuariosRepository getInstance() {
    return INSTANCE;
  }

  private UsuariosRepository() {
  }

  @Override
  public List<Usuario> getInstancias() {
    return RepositorioBasico.super.getInstancias();
  }

  @Override
  public Class<Usuario> clase() {
    return Usuario.class;
  }

  public Optional<Usuario> getByUsename(String usuario) {
    return obtenerUnoSiExisteSegun("username", usuario);
  }

  public Boolean estaElUsernameDisponible(String username) {
    return getByUsename(username).isEmpty();
  }
}
