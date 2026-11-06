package repositories;

import domain.Camion;
import domain.Entrega;
import domain.Posicion;
import domain.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import repositories.FlotaRepository.FlotaRepositoryEnMemoria;
import utility.FabricaDeDatos;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de FlotaRepositoryEnMemoria (Filtros)")
class FlotaRepositoryTest {

  private FlotaRepositoryEnMemoria repository;

  @BeforeEach
  void setUp() {
    repository = new FlotaRepositoryEnMemoria();
  }

  @Test
  @DisplayName("obtenerDisponibles() devuelve solo camiones sin ruta activa")
  void obtenerDisponiblesFiltraCorrectamente() {
    Camion camionDisponible = new Camion("AAA111", 10.0, 3.0,
        5000.0, new Posicion(-34.59855526311623, -58.42013582713193));
    Camion camionOcupado = new Camion("BBB222", 12.0, 3.5,
        6000.0, new Posicion(-34.59855526311623, -58.42013582713193));

    Ruta ruta = new Ruta(LocalDate.now());
    ruta.agregarEntrega(FabricaDeDatos.nuevaEntrega( "DON-1", "BEN-1"));
    camionOcupado.asignarRuta(ruta);

    repository.registrar(camionDisponible);
    repository.registrar(camionOcupado);

    List<Camion> disponibles = repository.obtenerDisponibles();

    assertEquals(1, disponibles.size());
    assertEquals("AAA111", disponibles.get(0).getPatente());
    assertTrue(disponibles.get(0).estaDisponible());
  }

  @Test
  @DisplayName("buscarPorPatente() devuelve null si la patente no existe")
  void buscarPorPatenteDevuelveNullSiNoExiste() {
    assertNull(repository.buscarPorPatente("ZZZ999"));
  }
}