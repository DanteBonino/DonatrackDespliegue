package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Usuario.Administrador;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;

public class AdministradorPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private Administrador administradorAPersistir;
  private DatosDeContacto contacto;

  @BeforeEach
  void setUp() {
    contacto = new DatosDeContactoBuilder()
        .conMail("admin@gmail.com")
        .conTelefono("1122334455")
        .build();
    administradorAPersistir = new Administrador(
        contacto
    );
    withTransaction(() -> {
      entityManager().persist(administradorAPersistir);
    });
  }

  @Test
  void deberiaTenerUnIdNoNulo() {
    assertNotNull(administradorAPersistir.getId());
  }
}
