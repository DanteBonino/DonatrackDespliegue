import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Contacto.DatosDeContactoBuilder;
import Usuario.Administrador;
import UsuarioFinal.Rol;
import UsuarioFinal.UsuarioAdministrador;
import UsuarioFinal.UsuarioBeneficiario;
import UsuarioFinal.UsuarioDonante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.FabricaDeDatos;
import utils.FabricaDeEntidadesBeneficiarias;
import utils.FechasUtils;

public class UsuarioTests {

  @Nested
  class UsuarioDonanteTests {
    private UsuarioDonante usuarioDonante;

    @BeforeEach
    void setUp() {
      usuarioDonante = getUsuarioConInactividadDeNDias(1);
    }

    @Nested
    class Inactividad {
      @Test
      void unUsuarioCon20DiasSinLogearseEstaInactivo() {
        assertTrue(getUsuarioConInactividadDeNDias(20).esInactivo());
      }

      @Test
      void unUsuarioCon19DiasSinLogearseNoEstaInactivo() {
        assertFalse(getUsuarioConInactividadDeNDias(19).esInactivo());
      }

    }

    @Test
    void elRolDeUnUsuarioDonanteEsDonante() {
      assertEquals(Rol.DONANTE, usuarioDonante.getRol());
    }

    @Test
    void elNombreMostrableDeUsuarioDonanteHumanoEsSuNombreCompleto() {
      assertEquals("Dante Perez", usuarioDonante.getNombreAMostrar());
    }

    @Test
    void cuandoSeLogeaSeActualizaLaFechaDeUltimoAccesoAlDiaActual() {
      usuarioDonante.registrarIngreso();
      assertEquals(FechasUtils.hoy(), usuarioDonante.getUltimoAcceso());
    }

    @Test
    void unStringQueEsIgualALaContraseñaEsVerificable() {
      assertTrue(usuarioDonante.verificarContrasenia("sarasa"));
    }

    @Test
    void unStringQueNoEsIgualALaContraseñaNoSeVerifica() {
      assertFalse(usuarioDonante.verificarContrasenia("saras"));
    }

  }

  @Nested
  class UsuarioBeneficiarioTest {
    private UsuarioBeneficiario usuarioBeneficiario;

    @BeforeEach
    void setUp() {
      usuarioBeneficiario = getUsuarioBeneficiario();
    }

    @Test
    void elRolDeUnUsuarioBeneficiarioEsEntidadBeneficiaria() {
      assertEquals(Rol.ENTIDADBENEFICIARIA, usuarioBeneficiario.getRol());
    }

    @Test
    void elNombreMostrableDeUnUsuarioBeneficiarioEsSuRazonSocial() {
      assertEquals("Entidad 1", usuarioBeneficiario.getNombreAMostrar());
    }

    @Test
    void cuandoSeLogeaSeActualizaLaFechaDeUltimoAccesoAlDiaActual() {
      usuarioBeneficiario.registrarIngreso();
      assertEquals(FechasUtils.hoy(), usuarioBeneficiario.getUltimoAcceso());
    }

    @Test
    void unStringQueEsIgualALaContraseñaEsVerificable() {
      assertTrue(usuarioBeneficiario.verificarContrasenia("sarasa"));
    }

    @Test
    void unStringQueNoEsIgualALaContraseñaNoSeVerifica() {
      assertFalse(usuarioBeneficiario.verificarContrasenia("saras"));
    }

  }

  @Nested
  class UsuarioAdministradorTest {
    private UsuarioAdministrador usuarioAdministrador;

    @BeforeEach
    void setUp() {
      usuarioAdministrador = getUsuarioAdministrador();
    }

    @Test
    void elTipoDeUnUsuarioAdministradorEsAdministrador() {
      assertEquals(Rol.ADMINISTRADOR, usuarioAdministrador.getRol());
    }

    @Test
    void elNombreMostrableDeUsuarioAdministradorAdministradorNombreDeUsuario() {
      assertEquals( usuarioAdministrador.getUsername(), usuarioAdministrador.getNombreAMostrar());
    }

    @Test
    void cuandoSeLogeaSeActualizaLaFechaDeUltimoAccesoAlDiaActual() {
      usuarioAdministrador.registrarIngreso();
      assertEquals(FechasUtils.hoy(), usuarioAdministrador.getUltimoAcceso());
    }

    @Test
    void unStringQueEsIgualALaContraseñaEsVerificable() {
      assertTrue(usuarioAdministrador.verificarContrasenia("sarasa"));
    }

    @Test
    void unStringQueNoEsIgualALaContraseñaNoSeVerifica() {
      assertFalse(usuarioAdministrador.verificarContrasenia("saras"));
    }

  }


  private UsuarioDonante getUsuarioConInactividadDeNDias(int cantidadDeDias) {
    return new UsuarioDonante("donante1", "sarasa", FabricaDeDatos.unDonanteDePrueba(), FechasUtils.haceNDias(cantidadDeDias));
  }

  private UsuarioBeneficiario getUsuarioBeneficiario() {
    return new UsuarioBeneficiario("beneficiario1", "sarasa", FabricaDeEntidadesBeneficiarias.entidadSinNecesidades());
  }

  private UsuarioAdministrador getUsuarioAdministrador() {
    return new UsuarioAdministrador("admin1", "sarasa", new Administrador(new DatosDeContactoBuilder()
        .conMail("admin@gmail.com")
        .conTelefono("1122334455")
        .build()));
  }

}
