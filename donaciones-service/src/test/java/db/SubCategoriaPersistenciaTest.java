package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.Categoria.Categoria;
import Bienes.SubCategoria.SubCategoria;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeCategorias;
import utils.FabricaDeSubCategorias;

public class SubCategoriaPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private SubCategoria subCategoria;

  @BeforeEach
  void setUp() {
    subCategoria = FabricaDeSubCategorias.getSubCategoriaCualquiera();
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(subCategoria.getCategoria());
      SubCategoriasRepository.getInstance().registrar(subCategoria);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(subCategoria.getId());
  }

}
