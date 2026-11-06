package externo;
import dtos.PlanificacionResponse;
import dtos.RutaPlanificadaDto;
import externo.callback.CallbackPlanificadorReceiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de CallbackPlanificadorReceiver")
class CallbackPlanificadorReceiverTest {

  private CallbackPlanificadorReceiver receiver;

  @BeforeEach
  void setUp() {
    receiver = new CallbackPlanificadorReceiver();
  }

  @Test
  @DisplayName("Deserializa correctamente un JSON completo con rutas y donaciones sin asignar")
  void procesarCallbackDeserializaCorrectamenteUnJsonCompleto() {
    String json = """
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
              "donacionesSinAsignar": ["D3", "D4", "D5"]
            }
            """;

    PlanificacionResponse response = receiver.procesarCallback(json);

    assertNotNull(response);
    assertEquals(2, response.getRutas().size());

    RutaPlanificadaDto ruta1 = response.getRutas().get(0);
    assertEquals("ABC123", ruta1.getPatenteCamion());
    assertEquals(2, ruta1.getEntregas().size());
    assertEquals("DON-1", ruta1.getEntregas().get(0).idDonacion());

    assertEquals(3, response.getDonacionesSinAsignar().size());
    assertTrue(response.getDonacionesSinAsignar().contains("D3"));
  }

  @Test
  @DisplayName("Maneja correctamente un JSON con listas vacías")
  void procesarCallbackManejaUnJsonVacio() {
    String json = """
            {
              "rutas": [],
              "donacionesSinAsignar": []
            }
            """;

    PlanificacionResponse response = receiver.procesarCallback(json);

    assertNotNull(response);
    assertTrue(response.getRutas().isEmpty());
    assertTrue(response.getDonacionesSinAsignar().isEmpty());
  }
}