package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;

public class DatosDeContactoPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private DatosDeContacto datosAPersistir;

  @BeforeEach
  void setUp() {
    datosAPersistir = new DatosDeContactoBuilder()
        .conMail("unMail@gmail.com")
        .conTelefono("1122334455")
        .build();

    withTransaction(() -> {
      entityManager().persist(datosAPersistir);
    });
  }

  @Test
  void tieneQueTenerUnIdNoNulo() {
    assertNotNull(datosAPersistir.getId());
  }

}
