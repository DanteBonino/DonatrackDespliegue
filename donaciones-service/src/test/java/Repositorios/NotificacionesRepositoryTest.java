package Repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;


class NotificacionesRepositoryTest implements ConLimpiezaDeBaseDeDatos {
  private NotificacionesRepository notificacionesRepository;
  private Notificacion notificacionEnviable;
  private Notificacion notificacionNoEnviablePorRebotes;
  private Notificacion notificacionNoEnviablePorHaberSidoEntregada;

  @BeforeEach
  void setUp() {
    notificacionesRepository = NotificacionesRepository.getInstance();
  }
  @Nested
  class SinNotificacionesPersistidas {
    @Test
    void siNoSePersistieronNotificacionesEntoncesEstaVacio() {
      assertTrue(notificacionesRepository.notificacionesEnviables().isEmpty());
    }

    @Test
    void notificacionesEnviablesTraeALaListaVacia() {
      List<Notificacion> notificaciones = notificacionesRepository.notificacionesEnviables();
      assertTrue(notificaciones.isEmpty());
    }
  }

  @Nested
  class ConNotificacionEnviablePersistida {
    @BeforeEach
    void setUp() {
      notificacionEnviable = unaNotificacion("hola", "hola@gmail.com");
      withTransaction(() -> {
        registrarNotificacion(notificacionEnviable);
      });
    }

    @Test
    void notificacionesEnviablesTraeALaUnicaNotificacionPersistidaPorqueEsEnviable() {
      List<Notificacion> notificaciones = notificacionesRepository.notificacionesEnviables();
      assertTrue(notificaciones.stream().anyMatch(notificacion -> notificacion.esLaMismaEntidad(notificacionEnviable.getIdDominio())));
    }

    @Test
    void getInstanciasTraeTodasLasPersistidas() {
      List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
      assertEquals(1, notificaciones.size());
      assertTrue(notificaciones.stream().anyMatch(notificacion -> notificacion.esLaMismaEntidad(notificacionEnviable.getIdDominio())));
    }

  }

  @Nested
  class ConNotificacionesEnviablesYNoEnviablesPersistidas {
    @BeforeEach
    void setUp() {
      notificacionEnviable = unaNotificacion("soy enviable", "soyEnviable@gmail.com");
      notificacionNoEnviablePorRebotes = unaNotificacion("no soy enviable", "nse@gmail.com");
      notificacionNoEnviablePorHaberSidoEntregada = unaNotificacion("ya fui entregada", "yfe@gmail.com");
      notificacionNoEnviablePorHaberSidoEntregada.pasarAEntregada();
      notificacionNoEnviablePorRebotes.serRebotada();
      notificacionNoEnviablePorRebotes.serRebotada();
      notificacionNoEnviablePorRebotes.serRebotada();
      withTransaction(() -> {
        registrarNotificacion(notificacionEnviable);
        registrarNotificacion(notificacionNoEnviablePorHaberSidoEntregada);
        registrarNotificacion(notificacionNoEnviablePorRebotes);
      });
    }

    @Test
    void notificacionesEnviablesTraeALaUnicaNotificacionPersistidaPorqueEsEnviable() {
      List<Notificacion> notificaciones = notificacionesRepository.notificacionesEnviables();
      assertEquals(1, notificaciones.size());
      assertTrue(notificaciones.stream().anyMatch(notificacion -> notificacion.esLaMismaEntidad(notificacionEnviable.getIdDominio())));
    }

    @Test
    void getInstanciasTraeTodasLasPersistidas() {
      List<Notificacion> notificaciones = notificacionesRepository.getInstancias();
      assertEquals(3, notificaciones.size());
    }

  }
  //Helpers
  void registrarNotificacion(Notificacion unaNotificacion) {
    entityManager().persist(unaNotificacion.getReceptor());
    notificacionesRepository.registrar(unaNotificacion);
  }
  DatosDeContacto unosDatos(String unMail) {
    return new DatosDeContactoBuilder()
        .conMail(unMail)
        .conTelefono("1122334455")
        .conMedioPreferido(TipoDeMedio.MAIL)
        .build();
  }

  NotificacionPreferencial unaNotificacion(String mensaje, String unMail) {
    return new NotificacionPreferencial(mensaje, unosDatos(unMail));
  }
}