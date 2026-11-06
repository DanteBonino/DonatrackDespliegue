package Repositories.Categorias;

import Bienes.Categoria.Categoria;
import Donaciones.Lote.Lote;
import Repositories.Generales.RepositorioBasico;
import Repositories.Lotes.LotesRepository;

public class CategoriasRepository implements RepositorioBasico<Categoria> {
  private static final CategoriasRepository INSTANCE = new CategoriasRepository();

  private CategoriasRepository() {

  }
  public static CategoriasRepository getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<Categoria> clase() {
    return Categoria.class;
  }
}
