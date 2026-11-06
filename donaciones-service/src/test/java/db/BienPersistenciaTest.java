package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.Bien;
import Bienes.SubCategoria.SubCategoria;
import Repositories.Bienes.BienesRepository;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeSubCategorias;
import utils.FechasUtils;

public class BienPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private Bien unBien;

  @BeforeEach
  void setUp() {
    unBien = FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(FechasUtils.enUnMes());
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(unBien.getSubCategoria().getCategoria());
      SubCategoriasRepository.getInstance().registrar(unBien.getSubCategoria());
      BienesRepository.getInstance().registrar(unBien);
    });
  }


  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(unBien.getId());
  }

}
