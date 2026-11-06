package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.FabricaDeDirecciones.direccionCualquiera;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Contacto.DatosDeContactoBuilder;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.Necesidad;
import Necesidades.NecesidadExtraordinaria;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeNecesidades;
import utils.FabricaDeSubCategorias;

public class EntidadBeneficiariaPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private SubCategoria arroz;
  private CondicionDeAceptacion enVigencia;
  private Necesidad necesidad;
  private EntidadBeneficiaria entidadBeneficiariaAPersistir;


  @BeforeEach
  void setUp() {
    arroz = FabricaDeSubCategorias.getSubCategoriaDeAlimento("arroz");
    enVigencia = FabricaDeCondicionesDeAceptacion.unaDeVigencia(arroz);
    necesidad = FabricaDeNecesidades.nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(enVigencia, 50);
    entidadBeneficiariaAPersistir = new EntidadBeneficiariaBuilder()
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1122334455")
            .conMail("dante@sendgrid.com")
            .build()
        )
        .conRazonSocial("salvar perros")
        .conNecesidades(List.of(necesidad))
        .build();
    withTransaction(() -> {
      CategoriasRepository.getInstance().registrar(arroz.getCategoria());
      SubCategoriasRepository.getInstance().registrar(arroz);
      entityManager().persist(entidadBeneficiariaAPersistir);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(entidadBeneficiariaAPersistir.getId());
  }
}
