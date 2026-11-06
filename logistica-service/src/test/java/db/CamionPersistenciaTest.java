package db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Camion;
import domain.Posicion;
import domain.Ruta;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;

public class CamionPersistenciaTest implements SimplePersistenceTest {
  private Camion mack;
  private Posicion seligman = new Posicion(35.328, 112.874);
  private Ruta ruta66;
  private FlotaRepository flotaRepository;

  @BeforeEach
  void setUp() {
    mack = new Camion(
        "AA 400 AA",
        100,
        100,
        100,
        seligman
    );
    ruta66 = new Ruta(LocalDate.now());
    flotaRepository = FlotaRepositoryConPersistencia.getInstance();
    flotaRepository.registrar(mack);
  }

  @Test
  void mackDeberiaTenerUnIdNoNulo() {
    assertNotNull(mack.getId());
  }

  @Test
  void siBuscamosAMackTieneLaLongitudYLaLatitudDeSeligman() {
    Camion mackTraidoDelContextoDePersistencia = flotaRepository.obtenerPorId(mack.getId());
    assertTrue(mackTraidoDelContextoDePersistencia.getPosicionActual().esIgual(mack.getPosicionActual()));
  }

  @Test
  void noSePuedePersistirUnCamionConUnaRutaQueTodaviaNoFuePersistida() {
    Ruta rutaSinPersistir = new Ruta( LocalDate.now());
    mack.asignarRuta(rutaSinPersistir);
    assertThrows(RuntimeException.class, () -> entityManager().flush());
  }

}
