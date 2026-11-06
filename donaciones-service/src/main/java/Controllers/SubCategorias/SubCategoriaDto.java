package Controllers.SubCategorias;

import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.Categoria.CategoriaDto;

public class SubCategoriaDto {
  private String nombre;
  private CategoriaDto categoria;
  private Boolean esPerecedera;

  public SubCategoria crearInstancia() {
    return new SubCategoria(nombre, categoria.crearInstancia(), esPerecedera);
  }

  public String getNombre() {
    return nombre;
  }

  public CategoriaDto getCategoria() {
    return categoria;
  }

  public Boolean getEsPerecedera() {
    return esPerecedera;
  }

  public static SubCategoriaDto desde(SubCategoria unaSubCategoria) {
    SubCategoriaDto response = new SubCategoriaDto();
    response.nombre = unaSubCategoria.getNombre();
    response.categoria = CategoriaDto.desde(unaSubCategoria.getCategoria());
    response.esPerecedera = unaSubCategoria.esPerecedera();
    return response;
  }
}