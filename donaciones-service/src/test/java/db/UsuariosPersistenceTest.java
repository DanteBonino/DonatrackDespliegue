package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Usuario.Administrador;
import UsuarioFinal.UsuarioAdministrador;
import UsuarioFinal.UsuarioBeneficiario;
import UsuarioFinal.UsuarioDonante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeDatos;
import utils.FabricaDeEntidadesBeneficiarias;

public class UsuariosPersistenceTest implements ConLimpiezaDeBaseDeDatos {
  private Administrador administradorAPersistir;
  private DatosDeContacto contacto;
  private Donante unDonante;
  private EntidadBeneficiaria unaEntidad;
  private UsuarioDonante usuarioDonante;
  private UsuarioBeneficiario usuarioBeneficiario;
  private UsuarioAdministrador usuarioAdministrador;

  @BeforeEach
  void setUp() {
    contacto = new DatosDeContactoBuilder()
        .conMail("admin@gmail.com")
        .conTelefono("1122334455")
        .build();
    administradorAPersistir = new Administrador(
        contacto
    );
    usuarioAdministrador = new UsuarioAdministrador("admin1", "sarasa", administradorAPersistir);
    unDonante = FabricaDeDatos.unDonanteDePrueba();
    usuarioDonante = new UsuarioDonante("dante", "sarasa", unDonante);
    unaEntidad = FabricaDeEntidadesBeneficiarias.entidadSinNecesidades();
    usuarioBeneficiario = new UsuarioBeneficiario("sancor seguros", "sarasa", unaEntidad);

    withTransaction(() -> {
      entityManager().persist(unDonante);
      entityManager().persist(usuarioDonante);
      entityManager().persist(unaEntidad);
      entityManager().persist(usuarioBeneficiario);
      entityManager().persist(administradorAPersistir);
      entityManager().persist(usuarioAdministrador);
    });
  }

  @Test
  void deberianTenerUnIdNoNulo() {
    assertNotNull(usuarioAdministrador.getId());
    assertNotNull(usuarioBeneficiario.getId());
    assertNotNull(usuarioDonante.getId());
  }
}
