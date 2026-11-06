package Controllers.DTOsSinController.Categoria;

import Bienes.Categoria.Categoria;
import Bienes.Categoria.Unidad;

public class CategoriaDto {
  private String nombre;
  private Unidad unidad;


  public Categoria crearInstancia() {
    return new Categoria(nombre, unidad);
  }

  public String getNombre() {
    return nombre;
  }

  public Unidad getUnidad() {
    return unidad;
  }

  public static CategoriaDto desde(Categoria unaCategoria) {
    CategoriaDto response = new CategoriaDto();
    response.nombre = unaCategoria.getNombre();
    response.unidad = unaCategoria.getUnidad();
    return response;
  }

}
