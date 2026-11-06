package utils;

import static utils.FabricaDeCategorias.getCategoriaByNombreConExcepcion;
import static utils.FabricaDeCategorias.getCategoriaCualquiera;

import Bienes.Categoria.Categoria;
import Bienes.ConNombre;
import Bienes.SubCategoria.SubCategoria;
import java.util.List;
import java.util.Optional;

public class FabricaDeSubCategorias {
  public static SubCategoria getSubCategoriaConNombreCategoriaYTipoDeDurabilidad(String nombre, Categoria categoria, Boolean esPerecedera) {
    return new SubCategoria(nombre, categoria, esPerecedera);
  }

  public static SubCategoria getSubCategoriaPerecedera(String nombre, Categoria categoria) {
    return getSubCategoriaConNombreCategoriaYTipoDeDurabilidad(nombre, categoria, true);
  }

  public static SubCategoria getSubCategoriaNoPerecedera(String nombre, Categoria categoria) {
    return getSubCategoriaConNombreCategoriaYTipoDeDurabilidad(nombre, categoria, false);
  }

  public static SubCategoria getSubCategoriaPercederaConCategoriaCualquiera(String nombre) {
    return getSubCategoriaPerecedera(nombre, getCategoriaCualquiera());
  }

  public static SubCategoria getSubCategoriaNoPerecederaConCategoriaCualquiera(String nombre) {
    return getSubCategoriaNoPerecedera(nombre, getCategoriaCualquiera());
  }

  public static SubCategoria getSubCategoriaPerecederaDeCategoriaBase(String nombreSubCategoria, String nombreCategoriaBase) {
    return getSubCategoriaPerecedera(nombreSubCategoria, getCategoriaByNombreConExcepcion(nombreCategoriaBase));
  }

  public static SubCategoria getSubCategoriaNoPerecederaDeCategoriaBase(String nombreSubCategoria, String nombreCategoriaBase) {
    return getSubCategoriaNoPerecedera(nombreSubCategoria, getCategoriaByNombreConExcepcion(nombreCategoriaBase));
  }

  public static List<SubCategoria> subCategoriasBase() {
    return List.of(
        getSubCategoriaDeVestimenta("remera"),
        getSubCategoriaDeVestimenta("pantalon"),
        getSubCategoriaDeVestimenta("campera"),
        getSubCategoriaDeAlimento("pepsi"),
        getSubCategoriaDeAlimento("tomate"),
        getSubCategoriaDeAlimento("FIDEOSSECOS"),
        getSubCategoriaDeAlimento("ARROZ"),
        getSubCategoriaDeMobilario("Mesa"),
        getSubCategoriaDeMobilario("Silla")
    );
  }

  public static SubCategoria getSubCategoriaDeVestimenta(String nombre) {
    return getSubCategoriaNoPerecederaDeCategoriaBase(nombre,"VESTIMENTA");
  }

  public static SubCategoria getSubCategoriaDeAlimento(String nombre) {
    return getSubCategoriaPerecederaDeCategoriaBase(nombre,"alimento");
  }

  public static SubCategoria getSubCategoriaDeMobilario(String nombre) {
    return getSubCategoriaNoPerecederaDeCategoriaBase(nombre,"MOBILIARIO");
  }

  public static Optional<SubCategoria> getSubCategoriaBaseByNombre(String nombre) {
    return getByNombre(subCategoriasBase(),nombre);
  }

  public static SubCategoria getSubCategoriaByNombreConExcepcion(String nombre) {
    return getSubCategoriaBaseByNombre(nombre).orElseThrow();
  }

  public static SubCategoria getSubCategoriaCualquiera() {
    return subCategoriasBase().stream().findAny().get();
  }

  public static <T extends ConNombre> Optional<T> getByNombre(List<T> nombrables, String nombre) {
    return nombrables
        .stream()
        .filter(nombrable -> nombrable.seLlama(StringUtils.nombreAptoBaseDeDatos(nombre)))
        .findAny();
  }
}
