package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.SubCategoria.SubCategoria;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeSubCategorias;

public class CondicionDeAceptacionPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private SubCategoria subCategoria;
  private CondicionDeAceptacion unaCondicionDeAceptacion;
  @BeforeEach
  void setUp() {
    subCategoria = FabricaDeSubCategorias.getSubCategoriaCualquiera();
    unaCondicionDeAceptacion = FabricaDeCondicionesDeAceptacion.unaDeVigencia(subCategoria);
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(subCategoria.getCategoria());
      SubCategoriasRepository.getInstance().registrar(subCategoria);
      entityManager().persist(unaCondicionDeAceptacion);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(unaCondicionDeAceptacion.getId());
  }
}
