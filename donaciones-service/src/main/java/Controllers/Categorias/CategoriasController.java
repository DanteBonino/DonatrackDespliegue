package Controllers.Categorias;

import Bienes.Categoria.Categoria;
import Controllers.ConOperacionesBasicasGet;
import Controllers.DTOsSinController.Categoria.CategoriaDto;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Generales.RepositorioBasico;
import java.util.function.Function;

public class CategoriasController implements ConOperacionesBasicasGet<Categoria, CategoriaDto> {

  @Override
  public RepositorioBasico<Categoria> repositorioPrincipal() {
    return CategoriasRepository.getInstance();
  }

  @Override
  public Function<Categoria, CategoriaDto> conversorARespuesta() {
    return CategoriaDto::desde;
  }
}
