package externo;

import dtos.CamionDto;
import dtos.DonacionDto;
import externo.planificador.PlanificadorRutasAdapter;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Tests de PlanificadorRutasAdapter")
class PlanificadorRutasAdapterTest {

  private PlanificadorRutasAdapter adapter;
  private PlanificadorRutasAdapter adapterSpy;

  @BeforeEach
  void setUp() {
    adapter = new PlanificadorRutasAdapter("http://planificador-fake.com/api");
    adapterSpy = spy(adapter);
    doNothing().when(adapterSpy).simularEnvioHttp(anyString()); // Nombre correcto
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

  private List<CamionDto> generarCamiones(int cantidad) {
    List<CamionDto> camiones = new ArrayList<>();
    for (int i = 1; i <= cantidad; i++) {
      camiones.add(new CamionDto("PAT-" + i, 10.0, 3.5, 1000.0));
    }
    return camiones;
  }

  @Test
  @DisplayName("Genera un solo lote si hay menos de 100 donaciones")
  void solicitaPlanificacionEnUnSoloLoteSiHayMenosDe100Donaciones() {
    List<DonacionDto> donaciones = generarDonaciones(50);
    List<CamionDto> camiones = generarCamiones(2);

    adapterSpy.solicitarPlanificacion(donaciones, camiones);

    verify(adapterSpy, times(1)).simularEnvioHttp(anyString()); // Nombre correcto
  }

  @Test
  @DisplayName("Genera múltiples lotes si hay más de 100 donaciones")
  void solicitaPlanificacionEnMultiplesLotesSiHayMasDe100Donaciones() {
    List<DonacionDto> donaciones = generarDonaciones(250);
    List<CamionDto> camiones = generarCamiones(3);

    adapterSpy.solicitarPlanificacion(donaciones, camiones);

    verify(adapterSpy, times(3)).simularEnvioHttp(anyString()); // Nombre correcto
  }

  @Test
  @DisplayName("El payload JSON contiene la información correcta de los DTOs")
  void elPayloadJsonContieneLaInformacionCorrecta() {
    List<DonacionDto> donaciones = List.of(
        new DonacionDto(
            "DON-99",
            "BEN-10",
        new DonacionDto.DireccionDto(
          new DonacionDto.CalleDto("Calle", 100 ),
          new DonacionDto.PosicionDto(-58.42, -34.60)
        )
    ));
    List<CamionDto> camiones = List.of(new CamionDto("ABC123", 15.5, 3.5, 2000.0));

    adapterSpy.solicitarPlanificacion(donaciones, camiones);

    ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
    verify(adapterSpy).simularEnvioHttp(jsonCaptor.capture()); // Nombre correcto

    String jsonEnviado = jsonCaptor.getValue();

    assertTrue(jsonEnviado.contains("\"idDonacion\":\"DON-99\""));
    assertTrue(jsonEnviado.contains("\"patente\":\"ABC123\""));
  }

  @Test
  @DisplayName("La lógica de dividir en lotes funciona correctamente")
  void dividirEnLotesFuncionaCorrectamente() {
    List<DonacionDto> donaciones = generarDonaciones(250);

    // Ahora funciona porque dividirEnLotes es package-private
    List<List<DonacionDto>> lotes = adapter.dividirEnLotes(donaciones, 100);

    assertEquals(3, lotes.size());
    assertEquals(100, lotes.get(0).size());
    assertEquals(100, lotes.get(1).size());
    assertEquals(50, lotes.get(2).size());
  }
}