package Controllers;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteHumana.PersonaHumana;
import Injector.Injector;
import Repositories.Donantes.DonantesRepository;
import Servidor.ServidorDonaciones;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.FabricaDeDirecciones.calleCualquiera;

@ExtendWith(MockitoExtension.class)
class DonantesControllerTest implements ConLimpiezaDeBaseDeDatos {

  private PersonaHumana unDonanteHumanoDePrueba() {
    return new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("dante@example.com")
            .conTelefono("1120304050")
            .build()
        )
        .conDocumento("45459090")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("Dante", "Perez"))
        .conGenero(Genero.MASCULINO)
        .conCalle(calleCualquiera())
        .build();
  }

  private DonantesRepository repositorioDeDonantesReal;
  @Mock
  private Injector inyectorDeDependencias;
  private Javalin app;

  @BeforeEach
  void setUp() {
    app = ServidorDonaciones.crearApp(inyectorDeDependencias);
    repositorioDeDonantesReal = DonantesRepository.getInstance();
  }

  @Nested
  class SinEntidadesEliminadasEnLaBase {
    private Donante donantePersistido;

    @BeforeEach
    void setUp() {
      donantePersistido = unDonanteHumanoDePrueba();
      withTransaction(() -> {
        repositorioDeDonantesReal.registrar(donantePersistido);
      });
    }

    // =========================================================================
    // GET /donantes/:id
    // =========================================================================
    @Test
    void siSeBuscaUnDonanteExistenteDeberiaDevolver200ConSusDatos() {

      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donantes/" + donantePersistido.getIdDominio());

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).contains("\"documento\":\"45459090\"");
      });
    }

    @Test
    void siSeBuscaUnDonanteInexistenteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donantes/id-inexistente");

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }

    // =========================================================================
    // GET /donantes
    // =========================================================================
    @Test
    void siSeListanLosDonantesDeberiaDevolverTodosLosRegistrados() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donantes");

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).contains("\"documento\":\"45459090\"");
      });
    }

    // =========================================================================
    // DELETE /donantes/:id
    // =========================================================================
    @Test
    void siSeEliminaUnDonanteDeberiaDevolver204YQuitarloDelRepositorio() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/donantes/" + donantePersistido.getIdDominio());

        assertThat(respuestaHttp.code()).isEqualTo(204);
        assertThat(repositorioDeDonantesReal.getInstancias()).isEmpty();
      });
    }

    @Test
    void siSeIntentaEliminarUnDonanteQueNoExisteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/donantes/CUALQUIER_COSA");

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });
    }

    // =========================================================================
    // PATCH /donantes/:id
    // =========================================================================
    @Test
    void siSeActualizaParcialmenteUnDonanteDeberiaDevolver204YModificarSoloEseCampo() {
      PersonaHumana dante = unDonanteHumanoDePrueba();
      withTransaction(() -> {
        repositorioDeDonantesReal.registrar(dante);
      });
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.patch("/donantes/" + dante.getIdDominio(),
            """
                { "genero": "FEMENINO" }
                """);

        assertThat(respuestaHttp.code()).isEqualTo(204);
        entityManager().clear();
        PersonaHumana danteActualizado = (PersonaHumana) repositorioDeDonantesReal.findById(dante.getIdDominio());
        assertThat(danteActualizado.getGenero()).isEqualTo(Genero.FEMENINO);
        assertThat(danteActualizado.getCaracteristicaDeDiferenciacion()).isEqualTo("45459090"); // el resto no debería cambiar
      });
    }

  }

  @Nested
  class SinEntidadesPersistidas {
    // =========================================================================
    // GET /donantes
    // =========================================================================

    @Test
    void siNoHayDonantesRegistradosGetAllDeberiaDevolverListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donantes");

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).isEqualTo("[]");
      });
    }

    // =========================================================================
    // POST /donantes
    // =========================================================================
    @Test
    void siSeIntentaCrearUnDonanteInvalidoDeberiaDevolver400YElMensajeDeError() {
      JavalinTest.test(app, (server, client) -> {

        String jsonInvalido = """
            {
               "tipoPersona": "HUMANA",
                "documento":"1123",
                "apellido": "Pérez",
                "email": "juan@example.com",
                "telefono": "1122334455",
                "fechaDeNacimiento": "1990-05-20"
            }
            """;

        Response respuestaHttp = client.post("/donantes", jsonInvalido);

        assertThat(respuestaHttp.code()).isEqualTo(400);

        String bodyJson = respuestaHttp.body().string();
        assertThat(bodyJson).contains("\"mensaje\":\"La calle es necesaria\"");

        assertThat(repositorioDeDonantesReal.getInstancias()).isEmpty();
      });
    }

    @Test
    void siSeCreaUnDonanteValidoDeberiaDevolver201ConLocationYPersistirlo() {
      JavalinTest.test(app, (server, client) -> {
        String jsonValido = """
            {
               "tipoPersona": "HUMANA",
                    "documento": "12345678",
                     "nombreRazonSocial": "Juan",
                     "apellido": "Pérez",
                     "email": "juan@example.com",
                     "telefono": "1122334455",
                     "fechaDeNacimiento": "1990-05-20",
                     "calle":{
                     "nombre":"Segurola",
                     "altura": 3000
                     },
                     "genero": "MASCULINO"
            }
            """;

        Response respuestaHttp = client.post("/donantes", jsonValido);

        assertThat(respuestaHttp.code()).isEqualTo(201);
        assertThat(repositorioDeDonantesReal.getInstancias()).hasSize(1);

        Donante creado = repositorioDeDonantesReal.getInstancias().get(0);

        assertThat(respuestaHttp.headers().get("Location"))
            .isNotNull()
            .contains("/donantes/" + creado.getIdDominio());
      });
    }
  }

  @Nested
  class ConEntidadesEliminadasPersistidas {
    private Donante eliminado;

    @BeforeEach
    void setUp() {
      eliminado = unDonanteHumanoDePrueba();
      eliminado.marcarComoEliminado();
      withTransaction(() -> {
        repositorioDeDonantesReal.registrar(eliminado);
      });
    }

    // =========================================================================
    // GET /donantes/:id
    // =========================================================================
    @Test
    void siSeBuscaUnDonanteQueFueEliminadaDeberiaDevolver404() {

      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donantes/" + eliminado.getIdDominio());

        assertThat(respuestaHttp.code()).isEqualTo(404);
      });

    }


    // =========================================================================
    // GET /donantes
    // =========================================================================
    @Test
    void siElUnicoDonantePersistidaEstaEliminadaEntoncesDeberiaDevolverLaListaVacia() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.get("/donantes");

        assertThat(respuestaHttp.code()).isEqualTo(200);
        assertThat(respuestaHttp.body().string()).isEqualTo("[]");
      });
    }

    // =========================================================================
    // DELETE /donantes/:id
    // =========================================================================
    @Test
    void siSeIntentaEliminarUnDonanteQueNoExisteDeberiaDevolver404() {
      JavalinTest.test(app, (server, client) -> {
        Response respuestaHttp = client.delete("/donantes/CUALQUIER_COSA");

        assertThat(respuestaHttp.code()).isEqualTo(404);
        assertThat(repositorioDeDonantesReal.getInstancias()).isEmpty();
      });
    }

  }
}






