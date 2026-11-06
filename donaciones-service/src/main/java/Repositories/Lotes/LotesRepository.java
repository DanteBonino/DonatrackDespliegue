package Repositories.Lotes;

import Donaciones.Lote.Lote;
import Repositories.Generales.RepositorioBasico;

public class LotesRepository implements RepositorioBasico<Lote> {
  private static final LotesRepository INSTANCE = new LotesRepository();

  private LotesRepository() {

  }
  public static LotesRepository getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<Lote> clase() {
    return Lote.class;
  }
}
