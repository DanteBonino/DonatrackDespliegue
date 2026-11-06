package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.Ruta;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RutaPersistenciaTest implements SimplePersistenceTest {
  private Ruta ruta66;

  @BeforeEach
  void setUp() {
    ruta66 = new Ruta( LocalDate.now());
    entityManager().persist(ruta66);
  }

  @Test
  void laRuta66DebeTenerUnRutaIdNoNuloYaQueFuePersistida() {
    assertNotNull(ruta66.getId());
  }
}
