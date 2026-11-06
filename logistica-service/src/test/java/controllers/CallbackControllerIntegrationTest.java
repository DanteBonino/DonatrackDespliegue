package controllers;

import controllers.utils.AsignadorDePlanificacion;
import domain.Camion;
import domain.Entrega;
import domain.Posicion;
import domain.Ruta;
import domain.eventos.EntregaPublicador;
import externo.callback.CallbackPlanificadorReceiver;
import externo.donaciones.DonacionesServiceAdapter;
import java.time.LocalDate;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepositoryEnMemoria;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepositoryEnMemoria;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import server.ExceptionHandler;
import server.FiltroGlobalDeTransacciones;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Integración: CallbackController (HTTP Real -> Dominio -> Repository)")
public class CallbackControllerIntegrationTest implements ConLimpiezaDeBaseDeDatos {

  private FlotaRepository flotaRepository;
  private RutasRepository rutasRepository;
  private Javalin app;

  @Mock
  private DonacionesServiceAdapter donacionesServiceAdapterMock;

  @BeforeEach
  void setUp() {
    flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    rutasRepository = RutasRepositoryConPersistencia.getInstance();
    withTransaction(() -> {
      flotaRepository.registrar(new Camion("ABC123", 10.0, 3.0,
          5000.0, new Posicion(-34.59855526311623, -58.42013582713193)));
      flotaRepository.registrar(new Camion("XYZ987", 12.0, 3.5,
          6000.0, new Posicion(-34.59855526311623, -58.42013582713193)));
    });

    CallbackPlanificadorReceiver receiver = new CallbackPlanificadorReceiver();


    CallbackController controller = new CallbackController(
        receiver,
        new AsignadorDePlanificacion(new EntregaPublicador()),
        donacionesServiceAdapterMock
    );

    app = Javalin.create(config -> {
      new FiltroGlobalDeTransacciones().aplicarTransaccion(config);
      ExceptionHandler.registrar(config);
      config.routes.post("/externo/callback-planificacion", controller::recibirPlanificacion);
    });
  }

  @Test
  @DisplayName("POST con JSON válido crea Rutas y Entregas en el repositorio real")
  void callbackConJsonValidoCreaRutasYEntregas() {
    String jsonValido = """
            {
              "rutas": [
                {
                  "patenteCamion": "ABC123",
                 "entregas": [
                    {
                      "idDonacion": "DON-1",
                       "idEntidadBeneficiaria": "BEN-1",
                        "direccionDestino": {
                           "calle": {
                             "nombre": "Avenida Rivadavia",
                             "altura": 1021
                           },
                           "posicion": {
                             "longitud": -37.380474,
                             "latitud": -13.608398000000001
                           }
                         }
                    },
                    {
                      "idDonacion": "DON-2",
                       "idEntidadBeneficiaria": "BEN-2",
                       "direccionDestino": {
                           "calle": {
                             "nombre": "Avenida Rivadavia",
                             "altura": 1021
                           },
                           "posicion": {
                             "longitud": -37.380474,
                             "latitud": -13.608398000000001
                           }
                       }
                    }
                  ]
                },
                {
                  "patenteCamion": "XYZ987",
                  "entregas": []
                }
              ],
              "donacionesSinAsignar": ["DON-3"]
            }
            """;

    JavalinTest.test(app, (server, client) -> {
      // --- ACT: Hacemos el POST real ---
      var response = client.post("/externo/callback-planificacion", jsonValido);

      // --- ASSERT 1: Verificar HTTP Status ---
      assertThat(response.code()).isEqualTo(200);
      assertThat(response.body().string()).contains("Planificación recibida con éxito");

      // --- ASSERT 2: Verificar que el Repositorio REAL guardó los datos ---
      List<Ruta> rutasCreadas = rutasRepository.obtenerTodas();
      assertThat(rutasCreadas).hasSize(2); // Se crearon 2 rutas
      entityManager().clear();
      Ruta ruta1 = flotaRepository.buscarPorPatente("ABC123").rutaAsignada().get();
      assertThat(ruta1.getEntregas()).hasSize(2);
      assertThat(ruta1.getEntregas().get(0).getEstado().name()).isEqualTo("PLANIFICADA");
      verify(donacionesServiceAdapterMock).notificarDonacionesNoPlanificadas(List.of("DON-3"));
    });
  }

  @Test
  @DisplayName("POST con patente inexistente devuelve 404 y no guarda nada")
  void callbackConPatenteInexistenteDevuelve404() {
    String jsonConPatenteInexistente = """
            {
              "rutas": [
                {
                  "patenteCamion": "PATENTE-INEXISTENTE",
                  "entregas": [
                    {
                      "idDonacion": "DON-1",
                       "idEntidadBeneficiaria": "BEN-1",
                        "direccionDestino": {
                           "calle": {
                             "nombre": "Avenida Rivadavia",
                             "altura": 1021
                           },
                           "posicion": {
                             "longitud": -37.380474,
                             "latitud": -13.608398000000001
                           }
                         }
                    },
                    {
                      "idDonacion": "DON-2",
                       "idEntidadBeneficiaria": "BEN-2",
                       "direccionDestino": {
                           "calle": {
                             "nombre": "Avenida Rivadavia",
                             "altura": 1021
                           },
                           "posicion": {
                             "longitud": -37.380474,
                             "latitud": -13.608398000000001
                           }
                       }
                    }
                  ]
                }
              ],
              "donacionesSinAsignar": []
            }
            """;

    JavalinTest.test(app, (server, client) -> {
      var response = client.post("/externo/callback-planificacion", jsonConPatenteInexistente);

      assertThat(response.code()).isEqualTo(404);

      assertThat(rutasRepository.obtenerTodas()).isEmpty();
    });
  }

  @Test
  @DisplayName("POST con un camión no disponible aborta toda la operación (Fail-Fast) devolviendo 409")
  void failFastConCamionNoDisponibleDevuelve409() {
    Ruta rutaPrevia = new Ruta(LocalDate.now());
    withTransaction(() -> {
      Camion camion = flotaRepository.buscarPorPatente("XYZ987");
      camion.asignarRuta(rutaPrevia);
      rutasRepository.registrar(rutaPrevia);
    });

    String jsonConFallaParcial = """
          {
            "rutas": [
              {
                "patenteCamion": "ABC123",
                "entregas": [
                    {
                      "idDonacion": "DON-1",
                       "idEntidadBeneficiaria": "BEN-1",
                        "direccionDestino": {
                           "calle": {
                             "nombre": "Avenida Rivadavia",
                             "altura": 1021
                           },
                           "posicion": {
                             "longitud": -37.380474,
                             "latitud": -13.608398000000001
                           }
                         }
                    }
                  ]
              },
              {
                "patenteCamion": "XYZ987",
                "entregas": [
                   {
                      "idDonacion": "DON-2",
                       "idEntidadBeneficiaria": "BEN-2",
                       "direccionDestino": {
                           "calle": {
                             "nombre": "Avenida Rivadavia",
                             "altura": 1021
                           },
                           "posicion": {
                             "longitud": -37.380474,
                             "latitud": -13.608398000000001
                           }
                       }
                    }
                ]
              }
            ],
            "donacionesSinAsignar": []
          }
          """;

    JavalinTest.test(app, (server, client) -> {
      var response = client.post("/externo/callback-planificacion", jsonConFallaParcial);

      assertThat(response.code()).isEqualTo(409);

      entityManager().clear();

      List<Ruta> todasLasRutas = rutasRepository.obtenerTodas();
      assertThat(todasLasRutas).hasSize(1);
      assertThat(todasLasRutas.get(0).getId()).isEqualTo(rutaPrevia.getId());
    });
  }

  @Test
  @DisplayName("POST con JSON malformado devuelve 400 Bad Request")
  void jsonMalformadoDevuelve400() {
    String jsonRoto = "{ ruta: [ { patenteCamion: faltan_comillas } ] "; // JSON inválido

    JavalinTest.test(app, (server, client) -> {
      var response = client.post("/externo/callback-planificacion", jsonRoto);

      assertThat(response.code()).isEqualTo(400);
      assertThat(rutasRepository.obtenerTodas()).isEmpty();
    });
  }
}