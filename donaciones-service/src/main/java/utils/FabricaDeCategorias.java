package utils;

import static utils.FabricaDeSubCategorias.getByNombre;

import Bienes.Categoria.Categoria;
import Bienes.Categoria.Unidad;
import java.util.List;
import java.util.Optional;

public class FabricaDeCategorias {
  //Todo esto es mejorable
  public static List<Categoria> categoriasBase() {
    return List.of(
        new Categoria("VESTIMENTA", Unidad.UNIDADES),
        new Categoria("ALIMENTO", Unidad.KILOGRAMOS),
        new Categoria("MOBILIARIO", Unidad.UNIDADES)
    );
  }

  public static Optional<Categoria> getCategoriaByNombre(String nombre) {
    return getByNombre(categoriasBase(), nombre);
  }

  public static Categoria getCategoriaByNombreConExcepcion(String nombre) {
    return getCategoriaByNombre(StringUtils.nombreAptoBaseDeDatos(nombre)).orElseThrow();
  }

  public static Categoria getCategoriaCualquiera() {
    return categoriasBase().get(0);
  }

  public static Categoria getCategoriaEnUnidades(String nombre) {
    return getCategoriaConNombreYUnidad(nombre, Unidad.UNIDADES);
  }

  public static Categoria getCategoriaEnKilogramos(String nombre) {
    return getCategoriaConNombreYUnidad(nombre, Unidad.KILOGRAMOS);
  }

  public static Categoria getCategoriaConNombreYUnidad(String nombre, Unidad unidad) {
    return new Categoria(nombre, unidad);
  }
}
