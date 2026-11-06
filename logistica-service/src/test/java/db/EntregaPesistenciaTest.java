package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.Entrega;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.FabricaDeDatos;

public class EntregaPesistenciaTest implements SimplePersistenceTest {
  private Entrega rayoMcQueenAControlRemoto;
  private LocalDateTime fecha;

  @BeforeEach
  void setUp() {
    rayoMcQueenAControlRemoto = FabricaDeDatos.nuevaEntrega("DON-4545", "BEN-123");
    entityManager().persist(rayoMcQueenAControlRemoto);
  }

  @Test
  void laEntregaTieneQueTenerUnIdNoNuloTraseSerIntegradaAlContextoDePersistencia() {
    assertNotNull(rayoMcQueenAControlRemoto.getId());
  }

}
