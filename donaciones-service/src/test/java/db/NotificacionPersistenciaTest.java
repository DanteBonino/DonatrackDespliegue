package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.FabricaDeDirecciones.calleCualquiera;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionFija;
import Notificaciones.NotificacionPreferencial;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;

public class NotificacionPersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private Notificacion notificacionPreferencial;
  private Notificacion notificacionViaMail;
  private Donante dante;

  @BeforeEach
  void setUp() {
    dante = new DonanteHumanoBuilder()
        .conNombreCompleto(new NombreCompleto("dante", "bonino"))
        .conContacto(new DatosDeContactoBuilder()
            .conMail("dante@sendgrid.com")
            .conTelefono("1122334455")
            .build()
        )
        .conCalle(calleCualquiera())
        .conDocumento("1234567")
        .conFechaDeNacimiento(LocalDate.now())
        .conGenero(Genero.MASCULINO)
        .build();
    notificacionPreferencial = new NotificacionPreferencial("un mensaje cualqueira", dante.getDatosDeContacto());
    notificacionViaMail = new NotificacionFija("un mensaje random", dante.getDatosDeContacto(), TipoDeMedio.MAIL);
    withTransaction(() -> {
      entityManager().persist(dante.getDatosDeContacto());
      entityManager().persist(notificacionPreferencial);
      entityManager().persist(notificacionViaMail);
    });
  }

  @Test
  void laNotificacionFijaTieneQueTenerUnIdNoNulo() {
    assertNotNull(notificacionViaMail.getId());
  }

  @Test
  void laNotificacionPreferencialTieneQueTenerUnIdNoNulo() {
    assertNotNull(notificacionViaMail.getId());
  }
}
