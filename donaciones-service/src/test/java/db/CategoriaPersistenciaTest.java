package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.Categoria.Categoria;
import Repositories.Categorias.CategoriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeCategorias;

public class CategoriaPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private Categoria categoria;
  @BeforeEach
  void setUp() {
    categoria = FabricaDeCategorias.getCategoriaCualquiera();
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(categoria);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(categoria.getId());
  }
}
