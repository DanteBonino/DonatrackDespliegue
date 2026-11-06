import dtos.DonacionDto;
import externo.planificador.PlanificadorRutasAdapter;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NOTA: dividirEnLotes está declarado private en tu clase original.
 * Para poder testearlo directamente, te recomiendo cambiarlo a package-private
 * (sin modificador) o extraerlo a una clase utilitaria tipo Lotes.dividir(...).
 * Acá asumo que lo dejaste package-private y este test está en el mismo paquete.
 */
class PlanificadorDeRutasAdapterTest {

  private PlanificadorRutasAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new PlanificadorRutasAdapter("http://url-de-prueba.com");
  }

  private List<DonacionDto> generarDonaciones(int cantidad) {
    List<DonacionDto> donaciones = new ArrayList<>();
    for (int i = 1; i <= cantidad; i++) {
      donaciones.add(new DonacionDto(
          "DON-" + i,
          "Direccion " + i,
          new DonacionDto.DireccionDto(
              new DonacionDto.CalleDto("Calle " + i, 100 * i),
              new DonacionDto.PosicionDto(-58.42, -34.60))));
    }
    return donaciones;
  }

  @Nested
  class DivisionEnLotes {

    @Test
    void conMenosDeUnLoteCompletoGeneraUnSoloLote() {
      List<DonacionDto> donaciones = generarDonaciones(50);

      List<List<DonacionDto>> lotes = adapter.dividirEnLotes(donaciones, 100);

      assertEquals(1, lotes.size());
      assertEquals(50, lotes.get(0).size());
    }

    @Test
    void conExactamenteUnLoteCompletoGeneraUnSoloLote() {
      List<DonacionDto> donaciones = generarDonaciones(100);

      List<List<DonacionDto>> lotes = adapter.dividirEnLotes(donaciones, 100);

      assertEquals(1, lotes.size());
      assertEquals(100, lotes.get(0).size());
    }

    @Test
    void con250DonacionesGeneraTresLotesElUltimoIncompleto() {
      List<DonacionDto> donaciones = generarDonaciones(250);

      List<List<DonacionDto>> lotes = adapter.dividirEnLotes(donaciones, 100);

      assertEquals(3, lotes.size());
      assertEquals(100, lotes.get(0).size());
      assertEquals(100, lotes.get(1).size());
      assertEquals(50, lotes.get(2).size());
    }

    @Test
    void conListaVaciaNoGeneraLotes() {
      List<DonacionDto> donaciones = new ArrayList<>();

      List<List<DonacionDto>> lotes = adapter.dividirEnLotes(donaciones, 100);

      assertTrue(lotes.isEmpty());
    }

    @Test
    void losLotesPreservanElOrdenOriginal() {
      List<DonacionDto> donaciones = generarDonaciones(150);

      List<List<DonacionDto>> lotes = adapter.dividirEnLotes(donaciones, 100);
      assertEquals("DON-1", lotes.get(0).get(0).idDonacion());
      assertEquals("DON-100", lotes.get(0).get(99).idDonacion());
      assertEquals("DON-101", lotes.get(1).get(0).idDonacion());
      assertEquals("DON-150", lotes.get(1).get(49).idDonacion());
    }
  }

}