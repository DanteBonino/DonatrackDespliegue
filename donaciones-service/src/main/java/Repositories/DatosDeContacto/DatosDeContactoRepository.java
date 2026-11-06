package Repositories.DatosDeContacto;

import Contacto.DatosDeContacto;
import Repositories.Generales.RepositorioBasico;

public class DatosDeContactoRepository implements RepositorioBasico<DatosDeContacto> {
  private static final DatosDeContactoRepository INSTACE = new DatosDeContactoRepository();

  private DatosDeContactoRepository () {}

  public static DatosDeContactoRepository getInstance() {
    return INSTACE;
  }

  public boolean elMailYaEstaEnUso(String mail) {
    Long count = (Long) this.entityManager() // o el context/entity manager que uses
        .createQuery("SELECT COUNT(d) FROM DatosDeContacto d WHERE :mail MEMBER OF d.mails", Long.class)
        .setParameter("mail", mail)
        .getSingleResult();

    return count > 0;
  }
  public boolean elTelefonoYaEstaEnUso(String telefono) {
    return existeSegun("telefono.numero", telefono);
  }

  @Override
  public Class<DatosDeContacto> clase() {
    return DatosDeContacto.class;
  }
}
