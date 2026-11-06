import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Bienes.Categoria.Categoria;
import Bienes.Categoria.Unidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeCategorias;
import utils.FabricaDeDatos;
import utils.StringUtils;

public class CategoriaTest {
  private Categoria vestimenta;
  private Categoria alimento;
  private Categoria creadaConNombreEnMinuscula;
  private String nombreEnMinuscula = "juegos";

  @BeforeEach
  void setUp() {
    vestimenta = FabricaDeCategorias.getCategoriaByNombreConExcepcion("vestimenta");
    alimento = FabricaDeCategorias.getCategoriaByNombreConExcepcion("alimento");
    creadaConNombreEnMinuscula = FabricaDeCategorias.getCategoriaEnUnidades(nombreEnMinuscula);
  }

  @Test
  void dosCategoriasQueSeLlamanDistintoSonDistintas() {
    assertFalse(vestimenta.esLaMisma(alimento));
  }

  @Test
  void elNombreDeUnaCategoriaQueSeCreaEnMinusculaSeNormaliza() {
    assertTrue(creadaConNombreEnMinuscula.seLlama(StringUtils.nombreAptoBaseDeDatos(nombreEnMinuscula)));
  }

  @Test
  void dosCategoriasQueSeLlamanIgualSonIguales() {
    Categoria alimentoFake = new Categoria("alimento", Unidad.KILOGRAMOS);
    assertTrue(alimentoFake.esLaMisma(alimento));
  }
}
