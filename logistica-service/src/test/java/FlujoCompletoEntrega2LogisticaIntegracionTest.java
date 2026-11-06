import builders.CalleBuilder;
import builders.DireccionBuilder;
import builders.PosicionBuilder;
import domain.*;
import domain.eventos.EntregaPublicador;
import dtos.DonacionDto;
import dtos.PlanificacionResponse;
import dtos.RutaPlanificadaDto;
import dtos.EntregaDto;
import externo.callback.CallbackPlanificadorReceiver;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepositoryEnMemoria;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de Integracion Pura (Sin Mocks): Flujo Completo de Logística " +
    "terminada la Entrega 2")
class FlujoCompletoEntrega2LogisticaIntegracionTest implements ConLimpiezaDeBaseDeDatos {

  private FlotaRepository flotaRepository;
  private RutasRepository rutasRepository;
  private CallbackPlanificadorReceiver receiver;

  @BeforeEach
  void setUp() {
    // Instancias REALES, sin mocks
    flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    rutasRepository = RutasRepositoryConPersistencia.getInstance();
    receiver = new CallbackPlanificadorReceiver();

    withTransaction(() -> {
      Camion camion1 = new Camion("ABC123", 10.0, 3.0, 5000.0, new Posicion(-34.6, -58.4));
      flotaRepository.registrar(camion1);
    });
    // Cargar flota real
  }

  @Test
  @DisplayName("Flujo E2E: Callback -> Creación de Ruta -> Inicio -> Confirmación -> Finalización")
  void flujoCompletoDesdeCallbackHastaFinalizacion() {
    // 1. Simular JSON de Callback real (Infraestructura -> Dominio)
    String jsonCallback = """
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
                }
              ],
              "donacionesSinAsignar": []
            }
            """;

    PlanificacionResponse response = receiver.procesarCallback(jsonCallback);
    RutaPlanificadaDto rutaDto = response.getRutas().get(0);

    // 2. Crear dominio real a partir del DTO (Logica de CallbackController)
    Camion camion = flotaRepository.buscarPorPatente(rutaDto.getPatenteCamion());
    Ruta ruta = new Ruta(LocalDate.now());
    camion.asignarRuta(ruta);

    for (DonacionDto entregaDto : rutaDto.getEntregas()) {
      Entrega entrega = new Entrega(entregaDto.idDonacion(),
          entregaDto.idEntidadBeneficiaria(),
          new DireccionBuilder()
              .conCalle(
                  new CalleBuilder()
                      .conNombre(entregaDto.nombreCalle())
                      .conAltura(entregaDto.altura())
                      .build()
              )
              .conPosicion(
                  new PosicionBuilder()
                      .conLongitud(entregaDto.longitud())
                      .conLatitud(entregaDto.latitud())
                      .build()
              )
              .build(),
          new EntregaPublicador());
      ruta.agregarEntrega(entrega);
    }
    rutasRepository.registrar(ruta);

    // Assertions de estado inicial
    assertEquals(EstadoRuta.PLANIFICADA, ruta.getEstado());
    assertEquals(2, ruta.getEntregas().size());

    // 3. El chofer inicia la ruta (App Movil -> Endpoint)
    camion.iniciarRuta();
    assertEquals(EstadoRuta.EN_CURSO, ruta.getEstado());
    assertEquals(EstadoEntrega.EN_CAMINO, ruta.getEntregas().get(0).getEstado());

    // 4. El chofer confirma la primera entrega
    Entrega entrega1 = ruta.getEntregas().get(0);
    entrega1.confirmarRecepcion("foto1.jpg", "ABC123");
    assertEquals(EstadoEntrega.COMPLETADA, entrega1.getEstado());

    // 5. El chofer confirma la segunda entrega
    Entrega entrega2 = ruta.getEntregas().get(1);
    entrega2.confirmarRecepcion("foto2.jpg", "ABC123");
    assertEquals(EstadoEntrega.COMPLETADA, entrega2.getEstado());

    // 6. El sistema verifica y finaliza la ruta
    camion.finalizarRuta();

    // Assertions finales
    assertEquals(EstadoRuta.FINALIZADA, ruta.getEstado());
    assertTrue(camion.estaDisponible(), "El camion debería volver a estar " +
        "disponible tras finalizar la ruta");
  }
}