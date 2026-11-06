import domain.*;
import exceptions.RutaSinEntregasException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import org.mockito.Mockito;
import utility.FabricaDeDatos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Estos tests cubren toda la lógica de dominio que en el main original estaba
 * mezclada con la simulación de red y el Thread.sleep. Acá no hay nada async:
 * armamos directamente los objetos de dominio (sin pasar por JSON ni por el
 * adapter) y verificamos comportamiento con asserts en lugar de println.
 *
 * AJUSTAR SEGÚN TU MODELO REAL:
 * - Constructores de Bien, SubCategoria, DonacionIndependiente, AsignacionRealizada
 * - Nombres exactos de los estados (ListaParaEntregar, EnDeposito, etc.)
 * - Si Entrega expone getEstado() o si el estado vive en Donacion
 */
class RutaEntregaDominioTest {

  private Camion camion;
  private String idDonacion1;
  private String idDonacion2;
  private String idDestinatario;

  @BeforeEach
  void setUp() {
    camion = new Camion("ABC123", 10.0, 3.0,
        5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
    idDonacion1 = "DON-001";
    idDonacion2 = "DON-002";
    idDestinatario = "ENT-BEN-01";
  }

  @Nested
  class CreacionYInicioDeRuta {

    @Test
    void unaRutaConEntregasYDonacionesListasParaEntregarPuedeDarInicio() {
      Ruta ruta = new Ruta( LocalDate.now());
      camion.asignarRuta(ruta);

      Entrega entrega = FabricaDeDatos.nuevaEntrega(idDonacion1, idDestinatario);
      ruta.agregarEntrega(entrega);

      camion.iniciarRuta();
      assertEquals(EstadoRuta.EN_CURSO, ruta.getEstado());
    }

    @Test
    void unaRutaConMultiplesEntregasLasConservaTodas() {
      Ruta ruta = new Ruta( LocalDate.now().plusDays(1));
      camion.asignarRuta(ruta);

      Entrega entrega1 = FabricaDeDatos.nuevaEntrega(idDonacion1, idDestinatario);
      Entrega entrega2 = FabricaDeDatos.nuevaEntrega(idDonacion2, idDestinatario);
      ruta.agregarEntrega(entrega1);
      ruta.agregarEntrega(entrega2);

      assertEquals(2, ruta.getTotalDeEntregas());
    }

    @Test
    void unaRutaSinEntregasNoPuedeDarInicio() {
      Ruta rutaVacia = new Ruta(LocalDate.now());
      Camion camionLibre = new Camion("GHI789", 12.0, 3.2,
          6000.0, new Posicion(-34.59855526311623, -58.42013582713193));
      camionLibre.asignarRuta(rutaVacia);
      assertThrows(RutaSinEntregasException.class, () -> {
        rutaVacia.darInicio(camionLibre);
      });
    }
  }

   /*@Nested
   class TransicionesDeEstadoDeDonacion {


    @Test
    void unaDonacionEnTransitoNoPuedePasarDirectamenteAEnDeposito() {
      donacion1.cambiarEstado(new ListaParaEntregar());
      // Simulamos que ya está en camino (EN_TRANSLADO) tras iniciar una ruta
      Ruta ruta = new Ruta("RUTA-1", LocalDate.now().plusDays(1));
      ruta.asignarCamion(camion);
      ruta.agregarEntrega(new Entrega(donacion1, beneficiario));
      ruta.darInicio();

      assertThrows(TransicionInvalidaDeEstado.class,
          () -> donacion1.cambiarEstado(new EnDeposito()));
    }
  }

  @Test
  void integracionCallbackConDominio_creaRutasYActualizaEstados() {
    String json = "{"
        + "\"rutas\": ["
        + "  { \"patenteCamion\": \"ABC123\", \"entregas\": ["
        + "      { \"idDonacion\": \"DON-1\", \"direccion\": \"Calle 1\" } ] }"
        + "],"
        + "\"donacionesSinAsignar\": []"
        + "}";

    CallbackPlanificadorReceiver receiver = new CallbackPlanificadorReceiver();
    PlanificacionResponse response = receiver.procesarCallback(json);

    DonacionIndependiente donacion = donacion1;

    RutaPlanificadaDto rutaDto = response.getRutas().get(0);
    Ruta ruta = new Ruta("RUTA-" + rutaDto.getPatenteCamion(), LocalDate.now().plusDays(1));
    ruta.asignarCamion(camion);

    EntregaDto entregaDto = rutaDto.getEntregas().get(0);
    Entrega entrega = new Entrega(donacion, new EntidadBeneficiaria(
        "Entidad X", entregaDto.getDireccion(), "123", List.of("mail@x.com"), List.of()));
    ruta.agregarEntrega(entrega);

    donacion.cambiarEstado(new ListaParaEntregar());
    ruta.darInicio();

    assertEquals(1, ruta.getEntregas().size());
    assertEquals(EstadoRuta.EN_CURSO, ruta.getEstado());
  }
    */

}