package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.Bien;
import Donaciones.DonacionIndependiente;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDonacionesIndependientes;

public class DonacionIndependientePersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private DonacionIndependiente donacionQueSePersiste;
  private Bien remeraNueva;

  @BeforeEach
  void setUp() {
    remeraNueva = FabricaDeBienes.remeraNueva();
    donacionQueSePersiste = FabricaDeDonacionesIndependientes.donacionConBienUnitario(remeraNueva);
    withTransaction(() -> {
      DonantesRepository.getInstance().registrar(donacionQueSePersiste.getDonante());
      CategoriasRepository.getInstance().registrar(remeraNueva.getSubCategoria().getCategoria());
      SubCategoriasRepository.getInstance().registrar(remeraNueva.getSubCategoria());
      entityManager().persist(donacionQueSePersiste);
    });
  }

  @Test
  void laDonacionIndependienteTieneQueTenerUnIdNoNulo() {
    assertNotNull(donacionQueSePersiste.getId());
  }
}
