package Repositories.SubCategorias;

import Bienes.Categoria.Categoria;
import Bienes.SubCategoria.SubCategoria;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Generales.RepositorioBasico;
import utils.StringUtils;

public class SubCategoriasRepository implements RepositorioBasico<SubCategoria> {
  private static final SubCategoriasRepository INSTANCE = new SubCategoriasRepository();

  private SubCategoriasRepository() {

  }
  public static SubCategoriasRepository getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<SubCategoria> clase() {
    return SubCategoria.class;
  }

  public SubCategoria findByNombre(String nombre) {
    return obtenerUnoSegun("nombre", StringUtils.nombreAptoBaseDeDatos(nombre));
  }
}
