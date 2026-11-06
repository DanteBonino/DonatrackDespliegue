package Controllers.SubCategorias;

import Bienes.SubCategoria.SubCategoria;
import Controllers.ConOperacionesBasicasGet;
import Repositories.Generales.RepositorioBasico;
import Repositories.SubCategorias.SubCategoriasRepository;
import io.javalin.http.Context;
import java.util.List;
import java.util.function.Function;

public class SubCategoriasController implements ConOperacionesBasicasGet<SubCategoria, SubCategoriaDto> {
  @Override
  public RepositorioBasico<SubCategoria> repositorioPrincipal() {
    return SubCategoriasRepository.getInstance();
  }

  @Override
  public Function<SubCategoria, SubCategoriaDto> conversorARespuesta() {
    return SubCategoriaDto::desde;
  }
}
