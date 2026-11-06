package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.Necesidad;
import Necesidades.NecesidadExtraordinaria;
import Necesidades.NecesidadRecurrenteBuilder;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import java.time.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeNecesidades;
import utils.FabricaDeSubCategorias;

public class NecesidadesPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private SubCategoria arroz;
  private CondicionDeAceptacion vigenciaParaRecurrente;
  private CondicionDeAceptacion vigenciaParaExtraordinaria;
  private Necesidad necesidadRecurrente;
  private Necesidad necesidadExtraordinaria;

  @BeforeEach
  void setUp() {
    arroz = FabricaDeSubCategorias.getSubCategoriaDeAlimento("arroz");
    vigenciaParaExtraordinaria = FabricaDeCondicionesDeAceptacion.unaDeVigencia(arroz);
    vigenciaParaRecurrente = FabricaDeCondicionesDeAceptacion.unaDeVigencia(arroz);
    necesidadExtraordinaria = FabricaDeNecesidades.nuevaNecesidadRecurrenteSemanalConCantidadExacta(vigenciaParaRecurrente, 50);
    necesidadRecurrente = FabricaDeNecesidades.nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(vigenciaParaExtraordinaria, 50);
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(arroz.getCategoria());
      SubCategoriasRepository.getInstance().registrar(arroz);
      entityManager().persist(necesidadRecurrente);
      entityManager().persist(necesidadExtraordinaria);
    });
  }

  @Test
  void laNecesidadRecurrenteTieneQueTenerUnIdNoNulo() {
    assertNotNull(necesidadRecurrente.getId());
  }

  @Test
  void laNecesidadExtraordinariaTieneQueTenerUnIdNoNulo() {
    assertNotNull(necesidadExtraordinaria.getId());
  }

}
