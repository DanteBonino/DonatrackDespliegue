package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.Bien;
import Donaciones.DonacionIndependiente;
import Donaciones.Lote.Lote;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Lotes.LotesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDonacionesIndependientes;

public class LotePersistenceTest implements ConLimpiezaDeBaseDeDatos {
  private Lote lote;
  private Bien remeraNueva;
  private DonacionIndependiente donacionDelLote;

  @BeforeEach
  void setUp() {
    remeraNueva = FabricaDeBienes.remeraNueva();
    donacionDelLote = FabricaDeDonacionesIndependientes.donacionConBienUnitario(remeraNueva);
    lote = new Lote(donacionDelLote.getDonante(), Set.of(donacionDelLote));
    withTransaction(() -> {
      entityManager().persist(donacionDelLote.getDonante());
      CategoriasRepository.getInstance().registrar(remeraNueva.getSubCategoria().getCategoria());
      SubCategoriasRepository.getInstance().registrar(remeraNueva.getSubCategoria());
      LotesRepository.getInstance().registrar(lote);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(lote.getId());
  }
}
