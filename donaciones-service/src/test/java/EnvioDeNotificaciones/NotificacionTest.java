package EnvioDeNotificaciones;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionFija;
import Notificaciones.NotificacionPreferencial;
import MedioDeNotificacion.TipoDeMedio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificacionTest {
  private Notificacion notificacion;
  private DatosDeContacto algoNotificable;
  private TipoDeMedio medioMail;
  private TipoDeMedio medioSms;
  private TipoDeMedio medioWpp;

  @BeforeEach
  public void setUp() {
    algoNotificable = new DatosDeContactoBuilder()
        .conMail("dante@gmail.com")
        .conTelefono("1122334455")
        .build();
    notificacion = new NotificacionPreferencial("Cualquier cosa", algoNotificable);
    medioSms = TipoDeMedio.SMS;
    medioMail = TipoDeMedio.MAIL;
    medioWpp = TipoDeMedio.WPP;
  }

  @Test
  void unaNotificacionRecienCreadaDebeSerEnviable() {
    assertTrue(notificacion.esEnviable(), "Es enviable una notificación recién creada");
  }

  @Test
  void unaNotificacionQueFueEntregadaNoDebeSerEnviable() {
    notificacion.pasarAEntregada();
    assertFalse(notificacion.esEnviable(), "Una notificación entregada no debería ser enviable");
  }

  @Test
  void unaNotificacionRebotada1VezDebeSerEnviable() {
    notificacion.serRebotada();
    assertTrue(notificacion.esEnviable(), "Con 1 rebote todavía debería ser enviable");
  }

  @Test
  void unaNotificacionRebotada2VecesDebeSerEnviable() {
    notificacion.serRebotada();
    notificacion.serRebotada();
    assertTrue(notificacion.esEnviable(), "Con 2 rebotes todavía debería ser enviable");
  }

  @Test
  void unaNotificacionRebotada3VecesNoDebeSerEnviable() {
    notificacion.serRebotada();
    notificacion.serRebotada();
    notificacion.serRebotada();
    assertFalse(notificacion.esEnviable(), "Con 3 rebotes ya no debería ser enviable");
  }

  @Test
  void unaNotificacionEntregadaNoVuelveASerEnviableAunSiEsRebotada() {
    notificacion.pasarAEntregada();
    notificacion.serRebotada();
    assertFalse(notificacion.esEnviable(), "Una notificación entregada no debería volverse enviable por un rebote");
  }

  @Test
  void unaNotificacionPreferencialPuedeSerEnviadaPorCualquierMedio() {
    assertTrue(notificacion.esEnviablePorElMedio(medioMail));
    assertTrue(notificacion.esEnviablePorElMedio(medioSms));
    assertTrue(notificacion.esEnviablePorElMedio(medioWpp));
  }

  @Test
  void unaNotificacionFijaSóloPuedeSerEnviadaPorUnMedioDeSuMismoTipo() {
    NotificacionFija notificacionFija = new NotificacionFija(
        "algo",
        algoNotificable,
        medioMail
    );
    assertTrue(notificacionFija.esEnviablePorElMedio(medioMail));
    assertFalse(notificacionFija.esEnviablePorElMedio(medioSms));
    assertFalse(notificacionFija.esEnviablePorElMedio(medioWpp));
  }

}