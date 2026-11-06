package Controllers;

import Bienes.SubCategoria.SubCategoria;
import Controllers.SubCategorias.SubCategoriaDto;
import Repositories.SubCategorias.SubCategoriasRepository;

public interface ConBusquedaDeSubCategoria {
  default SubCategoria buscarSubCategoria(String nombreSubCategoria) {
    return SubCategoriasRepository.getInstance().findByNombre(nombreSubCategoria);
  }
}
