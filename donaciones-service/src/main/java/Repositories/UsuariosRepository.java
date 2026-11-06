package Repositories;
import Repositories.Generales.RepositorioBasico;
import UsuarioFinal.ConSesion;
import UsuarioFinal.Usuario;
import UsuarioFinal.UsuarioDonante;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  public List<UsuarioDonante> getUsuariosDonantesInactivos() {
    LocalDate fechaLimite = LocalDate.now().minusDays(ConSesion.DIAS_PARA_INACTIVIDAD);

    return entityManager().createQuery(
            "SELECT u FROM UsuarioDonante u WHERE u.ultimoAcceso <= :fechaLimite",
            UsuarioDonante.class)
        .setParameter("fechaLimite", fechaLimite)
        .getResultList();
  }
}
