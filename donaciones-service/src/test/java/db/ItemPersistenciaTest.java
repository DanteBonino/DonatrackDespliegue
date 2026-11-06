package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Donaciones.Item;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeItems;

public class ItemPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private Item unItem;

  @BeforeEach
  void setUp() {
    unItem = FabricaDeItems.itemDeRemeraNuevaConNUnidades(50);
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(unItem.getBien().getSubCategoria().getCategoria());
      SubCategoriasRepository.getInstance().registrar(unItem.getBien().getSubCategoria());
      entityManager().persist(unItem);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(unItem.getId());
  }

}
