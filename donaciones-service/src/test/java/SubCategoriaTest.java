import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Bienes.Categoria.Categoria;
import Bienes.Categoria.Unidad;
import Bienes.SubCategoria.SubCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeDatos;
import utils.FabricaDeSubCategorias;
import utils.StringUtils;

public class SubCategoriaTest {
  private SubCategoria remera;
  private SubCategoria pepsi;
  private SubCategoria creadaConNombreEnMinuscula;
  private String nombreEnMinuscula = "sopa";

  @BeforeEach
  void setUp() {
    creadaConNombreEnMinuscula = FabricaDeSubCategorias.getSubCategoriaDeAlimento(nombreEnMinuscula);
    remera = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera");
  }

  @Test
  void dosSubCategoriasSonIgualesSiSeLlamanIgualYSusCategoriasSonIguales() {
    assertTrue(FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera").esLaMisma(remera));
  }

  @Test
  void dosCategoriasQueSeLlamanDistintoSonDistintas() {
    assertFalse(creadaConNombreEnMinuscula.esLaMisma(remera));
  }

  @Test
  void elNombreDeUnaSubCategoriaQueSeCreaEnMinusculaSeNormaliza() {
    assertTrue(creadaConNombreEnMinuscula.seLlama(StringUtils.nombreAptoBaseDeDatos(nombreEnMinuscula)));
  }
}
