package Controllers;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static utils.FabricaDeDirecciones.direccionCualquiera;
import static utils.FabricaDeDirecciones.posicionCualquiera;

import Contacto.DatosDeContactoBuilder;
import Controllers.EntidadesBenefeciarias.EntidadBeneficiariaResponseDto;
import Direccion.Calle;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import Geolocalizador.IGeolocalizador;
import Injector.Injector;
import Repositories.Beneficiarios.BeneficiariosRepository;
import ServiceLocator.ServiceLocator;
import Servidor.ServidorDonaciones;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLecturaDeRespuestas;
import utils.ConLimpiezaDeBaseDeDatos;

@ExtendWith(MockitoExtension.class)
public class BeneficiariosControllerTest implements ConLimpiezaDeBaseDeDatos, ConLecturaDeRespuestas {

  private BeneficiariosRepository repositorioDeBeneficiariosReal;
  @Mock private Injector inyectorDeDependencias;
  private Javalin app;
  @Mock private IGeolocalizador geolocalizador;

  @BeforeEach
  void setUp() {
    repositorioDeBeneficiariosReal = BeneficiariosRepository.getInstance();
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
    ServiceLocator.configurarGeolocalizadorCon(geolocalizador);
  }

  @Nested
  class SinEntidadesEliminadasEnLaBase {
    private EntidadBeneficiaria entidadPersisitida;
    @BeforeEach
    void setUp() {
      entidadPersisitida = unaEntidadDePrueba();
      withTransaction(() -> {
        repositorioDeBeneficiariosReal.registrar(entidadPersisitida);
      });
    }

    // =========================================================================
    // GET /entidades-beneficiarias/:id
    // =========================================================================
    @Test
    void siSeBuscaUnaEntidadExistenteDeberiaDevolver200ConSusDatos() {

      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/entidades-beneficiarias/" + entidadPersisitida.getIdDominio());
        assertThat(respuestaHttp.code()).isEqualTo(200);

        assertThat(respuestaHttp.body().string()).contains("\"razonSocial\":\"Comedor Los Pibes\"");
      });
    }
    @Test
    void siSeBuscaUnaEntidadInexistenteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/entidades-beneficiarias/id-inexistente");

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }
    @Disabled
    // =========================================================================
    // GET /entidades-beneficiarias
    // =========================================================================
    @Test
    void siSeListanLasEntidadesDeberiaDevolverTodasLasRegistradas() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/entidades-beneficiarias");

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).contains("\"razonSocial\":\"Comedor Los Pibes\"");
      });
    }

    // =========================================================================
    // DELETE /entidades-beneficiarias/:id
    // =========================================================================
    @Test
    void siSeEliminaUnaEntidadDeberiaDevolver204YQuitarlaDelRepositorio() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/entidades-beneficiarias/" + entidadPersisitida.getIdDominio());

        assertThat(respuestaHttp.code()).isEqualTo(204);
        assertThat(repositorioDeBeneficiariosReal.getInstancias()).isEmpty();
      });
    }

    @Test
    void siSeIntentaEliminarUnaEntidadQueNoExisteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/entidades-beneficiarias/CUALQUIER_COSA");

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }

    @Test
    void noSePuedeCrearDosEntidadesConLaMismaRazonSocial() {
      when(geolocalizador.getPosicionDe(any(Calle.class))).thenReturn(posicionCualquiera());
      JavalinTest.test(app, (server, client) -> {
        String jsonDuplicado = """
          {
             "razonSocial": "Comedor Los Pibes",
             "direccion": {
                "nombre": "otra direccion",
                "altura": 111
             },
             "mails": ["otro@mail.com"],
             "telefono": "111"
          }
          """;

        Response respuestaHttp = client.post("/entidades-beneficiarias", jsonDuplicado);

        assertThat(respuestaHttp.code()).isEqualTo(409);
        assertThat(repositorioDeBeneficiariosReal.getInstancias()).hasSize(1);
      });
    }

    // =========================================================================
    // PATCH /entidades-beneficiarias/:id
    // =========================================================================
    @Test
    void siSeActualizaParcialmenteUnaEntidadDeberiaDevolver204YModificarSoloEseCampo() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.patch("/entidades-beneficiarias/" + entidadPersisitida.getIdDominio(),
            """
            { "telefono": "1198765432" }
            """);
        EntidadBeneficiaria entidadActualizada = actualizado(entidadPersisitida.getIdDominio());
        assertThat(respuestaHttp.code()).isEqualTo(204);
        assertThat(entidadActualizada.getNumeroBase()).isEqualTo("1198765432");
        assertThat(entidadActualizada.getDireccion().getCalle().getFormatoLegible()).isEqualTo("Avenida Rivadavia 1234");
        assertThat(entidadActualizada.getRazonSocial()).isEqualTo("Comedor Los Pibes");
      });
    }

      @Test
    void sePuedeCorregirLaRazonSocialViaPatch() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.patch("/entidades-beneficiarias/" + entidadPersisitida.getIdDominio(),
            """
            { "razonSocial": "Comedor Los Pibes (corregido)" }
            """);

        EntidadBeneficiaria entidadActualizada = actualizado(entidadPersisitida.getIdDominio());
        assertThat(entidadActualizada.getRazonSocial()).isEqualTo("Comedor Los Pibes (corregido)");
      });
    }
  }

  @Nested
  class SinEntidadesPersistidas {
    // =========================================================================
    // GET /entidades-beneficiarias
    // =========================================================================
    @Disabled
    @Test
    void siNoHayEntidadesRegistradasGetAllDeberiaDevolverListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/entidades-beneficiarias");

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).isEqualTo("[]");
      });
    }

    // =========================================================================
    // POST /entidades-beneficiarias
    // =========================================================================
    @Test
    void siSeIntentaCrearUnaEntidadInvalidaDeberiaDevolver400YElMensajeDeError() {
      JavalinTest.test(app, (server, client) -> {
        String jsonInvalido = """
          {
             "razonSocial": "Comedor Los Pibes",
             "mails": ["comedor@lospibes.org"],
             "telefono": "1145678900"
          }
          """; // falta direccion

        Response respuestaHttp = client.post("/entidades-beneficiarias", jsonInvalido);
        assertThat(respuestaHttp.code()).isEqualTo(400);
        assertThat(respuestaHttp.body().string()).contains("\"mensaje\":\"la direccion es necesaria\"");
        assertThat(repositorioDeBeneficiariosReal.getInstancias()).isEmpty();
      });
    }

    @Test
    void siSeCreaUnaEntidadValidaDeberiaDevolver201ConLocationYPersistirla() {
      when(geolocalizador.getPosicionDe(any(Calle.class))).thenReturn(posicionCualquiera());
      JavalinTest.test(app, (server, client) -> {
        String jsonValido = """
          {
             "razonSocial": "Comedor Los Pibes",
             "direccion": {
                "nombre": "otra direccion",
                "altura": 111
             },
             "mails": ["comedor@lospibes.org"],
             "telefono": "1145678900"
          }
          """;

        Response respuestaHttp = client.post("/entidades-beneficiarias", jsonValido);

        assertThat(respuestaHttp.code()).isEqualTo(201);
        assertThat(repositorioDeBeneficiariosReal.getInstancias()).hasSize(1);

        EntidadBeneficiaria creada = repositorioDeBeneficiariosReal.getInstancias().get(0);
        assertThat(respuestaHttp.headers().get("Location"))
            .isNotNull()
            .contains("/entidades-beneficiarias/" + creada.getIdDominio());
      });
    }
  }

  @Nested
  class ConEntidadesEliminadasPersistidas {
    private EntidadBeneficiaria entidadPersisitida;
    @BeforeEach
    void setUp() {
      entidadPersisitida = unaEntidadDePrueba();
      entidadPersisitida.marcarComoEliminado();
      withTransaction(() -> {
        repositorioDeBeneficiariosReal.registrar(entidadPersisitida);
      });
    }

    // =========================================================================
    // GET /entidades-beneficiarias/:id
    // =========================================================================

    @Test
    void siSeBuscaUnaEntidadQueFueEliminadaDeberiaDevolver404() {

      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/entidades-beneficiarias/" + entidadPersisitida.getIdDominio());

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });

    }

    @Disabled
    // =========================================================================
    // GET /entidades-beneficiarias
    // =========================================================================
    @Test
    void siLaUnicaEntidadBeneficiariaPersistidaEstaEliminadaEntoncesDeberiaDevolverLaListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/entidades-beneficiarias");

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).isEqualTo("[]");
      });
    }

    // =========================================================================
    // DELETE /entidades-beneficiarias/:id
    // =========================================================================
    @Test
    void siSeIntentaEliminarUnaEntidadQueNoExisteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/entidades-beneficiarias/CUALQUIER_COSA");

        assertThat(respuestaHttp.code()).isEqualTo(404);
        assertThat(repositorioDeBeneficiariosReal.getInstancias()).isEmpty();
      });
    }

    // =========================================================================
    // POST /entidades-beneficiarias/:id
    // =========================================================================
    @Test
    void sePuedeCrearOtraEntidadBeneficiariaConLaMismaRazonSocialQueLaDeUnRegistroEliminado() {
      when(geolocalizador.getPosicionDe(any(Calle.class))).thenReturn(posicionCualquiera());

      JavalinTest.test(app, (server, client) -> {
        String jsonValido = """
          {
             "razonSocial": "Comedor Los Pibes",
             "direccion": {
                "nombre": "otra direccion",
                "altura": 111
             },
             "mails": ["comedor@lospibes.org"],
             "telefono": "1145678900"
          }
          """;

        Response respuestaHttp = client.post("/entidades-beneficiarias", jsonValido);

        assertThat(respuestaHttp.code()).isEqualTo(201);
        assertThat(repositorioDeBeneficiariosReal.getInstancias()).hasSize(1);

        EntidadBeneficiaria creada = repositorioDeBeneficiariosReal.getInstancias().get(0);
        assertThat(respuestaHttp.headers().get("Location"))
            .isNotNull()
            .contains("/entidades-beneficiarias/" + creada.getIdDominio());
      });
    }

    }


  private EntidadBeneficiaria unaEntidadDePrueba() {
    return new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor Los Pibes")
        .conDireccion(direccionCualquiera())
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1145678900")
            .conMail("comedor@lospibes.org")
            .build()
        )
        .conNecesidades(List.of())
        .build();
  }

  private EntidadBeneficiariaResponseDto leer(Response unaRespuesta) throws IOException {
    return leerComo(unaRespuesta, EntidadBeneficiariaResponseDto.class);
  }

  private List<EntidadBeneficiariaResponseDto> leerLista(Response unaRespuesta) throws IOException {
    return leerListaComo(unaRespuesta, EntidadBeneficiariaResponseDto.class);
  }

  private EntidadBeneficiaria actualizado(String id) {
    entityManager().clear();
    return repositorioDeBeneficiariosReal.findById(id);
  }
}
