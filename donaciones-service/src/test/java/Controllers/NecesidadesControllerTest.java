package Controllers;

import static org.assertj.core.api.Assertions.assertThat;

import Bienes.SubCategoria.SubCategoria;
import Contacto.DatosDeContactoBuilder;
import Controllers.Necesidades.NecesidadResponseDto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import Injector.Injector;
import Necesidades.Necesidad;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Servidor.ServidorDonaciones;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;

import io.javalin.Javalin;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.FabricaDeNecesidades;
import utils.FabricaDeSubCategorias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.FabricaDeDirecciones.direccionCualquiera;

@ExtendWith(MockitoExtension.class)
class NecesidadesControllerTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {

  private BeneficiariosRepository repositorioDeBeneficiariosReal;
  private SubCategoriasRepository subCategoriasRepository;
  private CategoriasRepository categoriasRepository;
  @Mock
  private Injector inyectorDeDependencias;
  private Javalin app;

  @BeforeEach
  void setUp() {
    repositorioDeBeneficiariosReal = BeneficiariosRepository.getInstance();
    subCategoriasRepository = SubCategoriasRepository.getInstance();
    categoriasRepository = CategoriasRepository.getInstance();
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
  }


  @Nested
  class EntidadBeneficiariaSinNecesidades {
    private EntidadBeneficiaria entidadPersistida;
    private SubCategoria remera;

    @BeforeEach
    void setUp() {
      remera = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera");
      entidadPersistida = unaEntidadDePrueba();
      withTransaction(() -> {
        categoriasRepository.registrar(remera.getCategoria());
        subCategoriasRepository.registrar(remera);
        repositorioDeBeneficiariosReal.registrar(entidadPersistida);
      });
    }

    // =========================================================================
    // GET /entidades-beneficiarias/:id/necesidades
    // =========================================================================
    @Test
    void siLaEntidadNoTieneNecesidadesDeberiaDevolverListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(urlNecesidades(entidadPersistida.getIdDominio()));

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).isEqualTo("[]");
      });
    }

    // =========================================================================
    // POST /entidades-beneficiarias/:id/necesidades
    // =========================================================================
    @Test
    void siSeCreaUnaNecesidadValidaDeberiaDevolver201ConLocationYPersistirla() {
      JavalinTest.test(app, (server, client) -> {
        String jsonValido = """
            {
                "tipo": "EXTRAORDINARIA",
                "nombreSubCategoria": "REMERA",
                "tipoCondicion": "BASICA",
                "cantidadRequerida": 5
               }
            }
            """;

        Response respuestaHttp = client.post(urlNecesidades(entidadPersistida.getIdDominio()), jsonValido);
        assertThat(respuestaHttp.code()).isEqualTo(201);

        entityManager().clear();
        EntidadBeneficiaria entidadActualizada = repositorioDeBeneficiariosReal.findById(entidadPersistida.getIdDominio());

        assertThat(entidadActualizada.getNecesidades()).hasSize(1);

        Necesidad necesidadCreada = entidadActualizada.getNecesidades().get(0);

        assertThat(respuestaHttp.headers().get("Location"))
            .isNotNull()
            .contains("/entidades-beneficiarias/" + entidadPersistida.getIdDominio() + "/necesidades/" + necesidadCreada.getIdDominio());
      });
    }

    @Test
    void siSeIntentaCrearUnaNecesidadConUnJsonIncorrectoSeObtieneUn400() {
      JavalinTest.test(app, (server, client) -> {
        String jsonValido = """
            {
               "tipo": "EXTRAORDINARIA",
               "bienRequerido": "XD"
            }
            """;

        Response respuestaHttp = client.post(urlNecesidades(entidadPersistida.getIdDominio()), jsonValido);
        assertThat(respuestaHttp.code()).isEqualTo(400);
      });
    }
  }

  @Nested
  class EntidadBeneficiariaConNecesidadesPersistidas {
    private EntidadBeneficiaria entidadPersistida;
    private Necesidad necesidadPersistida;
    private SubCategoria remera;

    @BeforeEach
    void setUp() {
      remera = FabricaDeSubCategorias.getSubCategoriaDeVestimenta("remera");
      entidadPersistida = unaEntidadDePrueba();
      necesidadPersistida = FabricaDeNecesidades.necesidadEnEstadoNuevo(remera , 50);

      entidadPersistida.agregarNecesidad(necesidadPersistida);

      withTransaction(() -> {
        categoriasRepository.registrar(remera.getCategoria());
        subCategoriasRepository.registrar(remera);
        repositorioDeBeneficiariosReal.registrar(entidadPersistida);
      });
    }

    // =========================================================================
    // GET /entidades-beneficiarias/:id/necesidades
    // =========================================================================
    @Test
    void siSeListanLasNecesidadesDeberiaDevolverTodasLasDeEsaEntidad() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(urlNecesidades(entidadPersistida.getIdDominio()));
        assertThat(respuestaHttp.code()).isEqualTo(200);
        List<NecesidadResponseDto> respuestas = leerLista(respuestaHttp);
        assertEquals(1, respuestas.size());
        assertEquals(respuestas.get(0).getId(), necesidadPersistida.getIdDominio());
      });
    }

    // =========================================================================
    // GET /entidades-beneficiarias/:id/necesidades/:idNec
    // =========================================================================
    @Test
    void siSeBuscaUnaNecesidadExistenteDeberiaDevolver200ConSusDatos() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(urlNecesidad(entidadPersistida.getIdDominio(), necesidadPersistida.getIdDominio()));
        NecesidadResponseDto respuesta = leer(respuestaHttp);
        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertEquals(respuesta.getId(), necesidadPersistida.getIdDominio());
      });
    }

    @Test
    void siSeBuscaUnaNecesidadInexistenteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get(urlNecesidad(entidadPersistida.getIdDominio(), "CUALQUIER_COSA"));

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }

    // =========================================================================
    // DELETE /entidades-beneficiarias/:id/necesidades/:idNec
    // =========================================================================
    @Test
    void siSeEliminaUnaNecesidadDeberiaDevolver204YQuitarlaDeLaEntidad() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete(urlNecesidad(entidadPersistida.getIdDominio(), necesidadPersistida.getIdDominio()));

        assertThat(respuestaHttp.code()).isEqualTo(204);

        entityManager().clear();
        EntidadBeneficiaria entidadFija = repositorioDeBeneficiariosReal.findById(entidadPersistida.getIdDominio());

        assertThat(entidadFija.getNecesidades()).isEmpty();
      });
    }

    @Test
    void siSeIntentaEliminarUnaNecesidadQueYaHabiaSidoEliminadaFallaPorNoEncontrarla() {
      withTransaction(() -> {
        entidadPersistida.eliminarNecesidad(necesidadPersistida.getIdDominio());
      });
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete(urlNecesidad(entidadPersistida.getIdDominio(), necesidadPersistida.getIdDominio()));

        assertThat(respuestaHttp.code()).isEqualTo(404);

        entityManager().clear();
        EntidadBeneficiaria entidadFija = repositorioDeBeneficiariosReal.findById(entidadPersistida.getIdDominio());

        assertThat(entidadFija.getNecesidades()).isEmpty();
      });
    }
  }

  // Helpers para no escribir las URLs repetitivas a mano
  private String urlNecesidades(String idBeneficiario) {
    return "/entidades-beneficiarias/" + idBeneficiario + "/necesidades";
  }

  private String urlNecesidad(String idBeneficiario, String idNecesidad) {
    return urlNecesidades(idBeneficiario) + "/" + idNecesidad;
  }

  // Helper para crear una entidad base sin necesidades
  private EntidadBeneficiaria unaEntidadDePrueba() {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor Los Pibes")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder().conMail("test@test.com").conTelefono("1234").build())
        .conNecesidades(List.of()) // Inicialmente vacía
        .build();
  }

  private NecesidadResponseDto leer(Response unaRespuesta) throws IOException {
    return leerComo(unaRespuesta, NecesidadResponseDto.class);
  }

  private List<NecesidadResponseDto> leerLista(Response unaRespuesta) throws IOException {
    return leerListaComo(unaRespuesta, NecesidadResponseDto.class);
  }
}