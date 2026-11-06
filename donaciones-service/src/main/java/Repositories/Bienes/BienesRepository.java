package Repositories.Bienes;

import Bienes.Bien;
import Bienes.SubCategoria.SubCategoria;
import Repositories.Generales.RepositorioBasico;

public class BienesRepository implements RepositorioBasico<Bien> {
  private static final BienesRepository INSTACE = new BienesRepository();

  public static BienesRepository getInstance() {
    return INSTACE;
  }

  private BienesRepository() {

  }

  @Override
  public Class<Bien> clase() {
    return Bien.class;
  }

}
