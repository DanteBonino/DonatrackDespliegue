import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.FabricaDeDirecciones.calleCualquiera;
import static utils.FabricaDeDirecciones.direccionCualquiera;
import static utils.FechasUtils.hoy;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteHumana.PersonaHumana;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import MedioDeNotificacion.TipoDeMedio;
import Telefono.TelefonoArgentino;
import Usuario.Administrador;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DatosDeContactoTest {
  DatosDeContacto unosDatosDeContacto;
  @BeforeEach
  void setUp() {
    unosDatosDeContacto = new DatosDeContactoBuilder()
        .conMail("unMail@gmail")
        .conTelefono("1122334455")
        .conMedioPreferido(TipoDeMedio.MAIL)
        .build();
  }

  @Nested
  class Donante {
    private PersonaHumana gus;
    @BeforeEach
    void setUp() {
      gus = new DonanteHumanoBuilder()
          .conContacto(unosDatosDeContacto)
          .conCalle(calleCualquiera())
          .conFechaDeNacimiento(hoy().minusYears(40))
          .conDocumento("40404003")
          .conGenero(Genero.MASCULINO)
          .conNombreCompleto(new NombreCompleto("gustavo", "e"))
          .build();
    }

    @Test
    void unDonanteTieneComoEmailElMailDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getMailsParaNotificar().get(0), gus.getEmail());
    }

    @Test
    void unDonantePuedeCambiarSuMail() {
      gus.setEmail("gus@Gmail.com");
      assertEquals("gus@Gmail.com", gus.getEmail());
    }

    @Test
    void unDonanteTieneComoTelefonoElDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getTelefono(), gus.getTelefono());
    }

    void unDonantePuedeCambiarSuTelefono() {
      gus.setTelefono(TelefonoArgentino.nuevoTelefono("1122334455"));
      assertEquals("1122334455", gus.getNumeroTelefonicoBase());
    }

    @Test
    void unDonanteTieneUnMedioDeContactoPreferenteQueEsElDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getMedioDeNotificacionPreferencial(), gus.getMedioDeNotificacionPreferencial());
    }

    @Test
    void unDonantePuedeCambiarSuMedioDeContactoPreferencial() {
      gus.setMedioPreferido(TipoDeMedio.WPP);
      assertEquals(TipoDeMedio.WPP, gus.getMedioDeNotificacionPreferencial());
    }
  }

  @Nested
  class IAdministrador {
    private Administrador admin;
    @BeforeEach
    void setUp() {
      admin = new Administrador(unosDatosDeContacto);
    }

    @Test
    void unAdministradorTieneComoEmailElMailDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getMailsParaNotificar().get(0), admin.getEmail());
    }

    @Test
    void unAdministradorPuedeCambiarSuMail() {
      admin.setEmail("gus@Gmail.com");
      assertEquals("gus@Gmail.com", admin.getEmail());
    }

    @Test
    void unAdministradorTieneComoTelefonoElDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getTelefono(), admin.getTelefono());
    }

    void unAdministradorPuedeCambiarSuTelefono() {
      admin.setTelefono(TelefonoArgentino.nuevoTelefono("1122334455"));
      assertEquals("1122334455", admin.getNumeroBase());
    }

    @Test
    void unAdministradorTieneUnMedioDeContactoPreferenteQueEsElDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getMedioDeNotificacionPreferencial(), admin.getMedioDeNotificacionPreferencial());
    }

    @Test
    void unAdministradorPuedeCambiarSuMedioDeContactoPreferencial() {
      admin.setMedioPreferido(TipoDeMedio.WPP);
      assertEquals(TipoDeMedio.WPP, admin.getMedioDeNotificacionPreferencial());
    }
  }

  @Nested
  class IEntidadBeneficiaria {
    private EntidadBeneficiaria entidadBeneficiaria;
    @BeforeEach
    void setUp() {
      entidadBeneficiaria = new EntidadBeneficiariaBuilder()
          .conDireccion(direccionCualquiera())
          .conContacto(unosDatosDeContacto)
          .conRazonSocial("con razon")
          .build();
    }

    @Test
    void unaEntidadTieneUnConjuntoDeMails() {
      assertEquals(unosDatosDeContacto.getMailsParaNotificar(), entidadBeneficiaria.getMails());
    }

    @Test
    void unaEntidadBeneficiariaTieneComoTelefonoElDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getTelefono(), entidadBeneficiaria.getTelefono());
    }

    @Test
    void unaEntidadBeneficiariaPuedeCambiarSuTelefono() {
      entidadBeneficiaria.setTelefono(TelefonoArgentino.nuevoTelefono("1122334455"));
      assertEquals("1122334455", entidadBeneficiaria.getNumeroBase());
    }

    @Test
    void unaEntidadBeneficiariaTieneUnMedioDeContactoPreferenteQueEsElDeSusDatosDeContacto() {
      assertEquals(unosDatosDeContacto.getMedioDeNotificacionPreferencial(), entidadBeneficiaria.getMedioDeNotificacionPreferencial());
    }

    @Test
    void  unaEntidadBeneficiariaPuedeCambiarSuMedioDeContactoPreferencial() {
      entidadBeneficiaria.setMedioPreferido(TipoDeMedio.WPP);
      assertEquals(TipoDeMedio.WPP, entidadBeneficiaria.getMedioDeNotificacionPreferencial());
    }
  }
}
