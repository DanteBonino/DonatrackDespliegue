package Observers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.FabricaDeDatos.unDonanteDePrueba;
import static utils.FabricaDeDatos.unaEntidadDePrueba;

import Bienes.Bien;
import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import MedioDeNotificacion.TipoDeMedio;
import ObserversCambioDeEstado.ConfirmarAsignacion.NotificadorDeDonanteAnteAsignacion;
import ObserversCambioDeEstado.ConfirmarAsignacion.NotificadorDeEntidadBeneficiariaAnteAsignacion;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import ObserversCambioDeEstado.EntregaConExito.NotificadorEntregaConExitoDonante;
import ObserversCambioDeEstado.EntregaConExito.NotificadorEntregaConExitoEntidad;
import ObserversCambioDeEstado.EntregaFallida.NotificadorEntregaFallidaAdministrador;
import ObserversCambioDeEstado.EntregaFallida.NotificadorEntregaFallidaDonante;
import ObserversCambioDeEstado.EntregaFallida.NotificadorEntregaFallidaEntidad;
import ObserversCambioDeEstado.InicioDeRuta.GeneradorLinkApi;
import ObserversCambioDeEstado.InicioDeRuta.GeneradorLinkDeTrackeoEntrega;
import ObserversCambioDeEstado.InicioDeRuta.NotificadorDonanteInicioDeRuta;
import ObserversCambioDeEstado.InicioDeRuta.NotificadorEntidadInicioDeRuta;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Usuario.Administrador;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FabricaDeBienes;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;
import utils.FabricaDeNecesidades;

public class CambiosDeEstadoObserversTest implements ConLimpiezaDeBaseDeDatos {
  private NotificacionesRepository notificacionesRepository;
  private DonacionIndependiente donacion;
  private EntidadBeneficiaria receptor;
  private Donante donante;
  private PublicadorDeCambiosEstados publicadorDeCambiosEstados;
  private Bien remeraNueva;

  @BeforeEach
  void setUp() {
    publicadorDeCambiosEstados = new PublicadorDeCambiosEstados();
    donante = unDonanteDePrueba();
    remeraNueva = FabricaDeBienes.remeraNueva();
    donacion = FabricaDeDonacionesIndependientes.unaDonacionEnDeposito(remeraNueva,  1, donante, publicadorDeCambiosEstados);
    receptor = FabricaDeEntidadesBeneficiarias.crearCandidataPara(donacion);
    notificacionesRepository = NotificacionesRepository.getInstance();
    withTransaction(() -> {
      entityManager().persist(donante);
      entityManager().persist(donacion.getSubCategoria().getCategoria());
      entityManager().persist(donacion.getSubCategoria());
      entityManager().persist(donacion);
      entityManager().persist(receptor);
    });
  }

  @Nested
  class EntregaFallida {
    @BeforeEach
    void setUp() {
      donacion.enSolicitudDeAsignacion();
      donacion.asignarA(receptor);
      donacion.alistarParaEntregar();
      donacion.iniciarTraslado("ENT-404");
    }

    @Nested
    class NotificarFallidaAAdmins {
      private Administrador receptorDeNotificacion;
      private Administrador administradorNoPersistido;

      @BeforeEach
      void setUp() {
        publicadorDeCambiosEstados.suscribir(new NotificadorEntregaFallidaAdministrador());
        receptorDeNotificacion = new Administrador(unosDatosDeContacto("dante@gmail.com"));
        administradorNoPersistido = new Administrador(unosDatosDeContacto("nopersistido@gmail.com"));
        withTransaction(() -> {
          AdministradoresRepository.getInstance().registrar(receptorDeNotificacion);
          donacion.marcarComoFallida("No había nadie para recibir la entrega");
        });
      }

      @Test
      void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelUnicoAdminPersistido() {
        assertEquals(1, notificacionesRepository.cantidadInstancias());
        assertEquals(receptorDeNotificacion.getEmail(), getMailDeNotificacion());
      }
    }

    @Nested
    class NotificarFallidaADonante {
      @BeforeEach
      void setUp() {
        publicadorDeCambiosEstados.suscribir(new NotificadorEntregaFallidaDonante());
        withTransaction(() -> {
          donacion.marcarComoFallida("No había nadie para recibir la entrega");
        });
      }

      @Test
      void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
        assertEquals(1, notificacionesRepository.cantidadInstancias());
        assertEquals(donante.getEmail(), getMailDeNotificacion());
      }
    }

    @Nested
    class NotificarFallidaAEntidadBeneficiaria {
      @BeforeEach
      void setUp() {
        publicadorDeCambiosEstados.suscribir(new NotificadorEntregaFallidaEntidad());
        withTransaction(() -> {
          donacion.marcarComoFallida("No había nadie para recibir la entrega");
        });
      }

      @Test
      void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
        assertEquals(1, notificacionesRepository.cantidadInstancias());
        assertEquals(receptor.getMails().get(0), getMailDeNotificacion());
      }
    }
  }

    @Nested
    class EntregaConExito {
      @BeforeEach
      void setUp() {
        donacion.enSolicitudDeAsignacion();
        donacion.asignarA(receptor);
        donacion.alistarParaEntregar();
        donacion.iniciarTraslado("ENT-404");
      }

      @Nested
      class NotificarExitoADonante {
        @BeforeEach
        void setUp() {
          publicadorDeCambiosEstados.suscribir(new NotificadorEntregaConExitoDonante());
          withTransaction(() -> {
            donacion.marcarComoEntregada(new ComprobanteDeEntrega(LocalDateTime.now(), "CCC-333"));
          });
        }
        @Test
        void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
          assertEquals(1, notificacionesRepository.cantidadInstancias());
          assertEquals(donante.getEmail(), getMailDeNotificacion());
        }
      }

      @Nested
      class NotificarExitoAEntidadBeneficiaria {
        @BeforeEach
        void setUp() {
          publicadorDeCambiosEstados.suscribir(new NotificadorEntregaConExitoEntidad());
            withTransaction(() -> {
            donacion.marcarComoEntregada(new ComprobanteDeEntrega(LocalDateTime.now(), "CCC-333"));
          });
        }
        @Test
        void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
          assertEquals(1, notificacionesRepository.cantidadInstancias());
          assertEquals(receptor.getMails().get(0), getMailDeNotificacion());
        }
      }
  }

      @Nested
      class ConfirmarAsignacion {
        @BeforeEach
        void setUp() {
          donacion.enSolicitudDeAsignacion();
        }

        @Nested
        class NotificarAsignacionADonante {
          @BeforeEach
          void setUp() {
            publicadorDeCambiosEstados.suscribir(new NotificadorDeDonanteAnteAsignacion());
            withTransaction(() -> {
              donacion.asignarA(receptor);
            });
          }
          @Test
          void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
            assertEquals(1, notificacionesRepository.cantidadInstancias());
            assertEquals(donante.getEmail(), getMailDeNotificacion());
          }
        }

        @Nested
        class NotificarAsignacionAEntidadBeneficiaria {
          @BeforeEach
          void setUp() {
            publicadorDeCambiosEstados.suscribir(new NotificadorDeEntidadBeneficiariaAnteAsignacion());
            withTransaction(() -> {
              donacion.asignarA(receptor);
            });
          }
          @Test
          void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
            assertEquals(1, notificacionesRepository.cantidadInstancias());
            assertEquals(receptor.getMails().get(0), getMailDeNotificacion());
          }
        }

      }

  @Nested
  class InicioDeRuta {
    private GeneradorLinkDeTrackeoEntrega generadorLink = new GeneradorLinkApi("CUALQUIERCOSA");
    @BeforeEach
    void setUp() {
      donacion.enSolicitudDeAsignacion();
      donacion.asignarA(receptor);
      donacion.alistarParaEntregar();
    }

    @Nested
    class NotificarInicioDeRutaADonante {
      @BeforeEach
      void setUp() {
        publicadorDeCambiosEstados.suscribir(new NotificadorDonanteInicioDeRuta(generadorLink));
        withTransaction(() -> {
          donacion.iniciarTraslado("ENT-404");
        });
      }
      @Test
      void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
        assertEquals(1, notificacionesRepository.cantidadInstancias());
        assertEquals(donante.getEmail(), getMailDeNotificacion());
      }
    }

    @Nested
    class NotificarInicioDeRutaAEntidadBeneficiaria {
      @BeforeEach
      void setUp() {
        publicadorDeCambiosEstados.suscribir(new NotificadorEntidadInicioDeRuta(generadorLink));
        withTransaction(() -> {
          donacion.iniciarTraslado("ENT-404");
        });
      }
      @Test
      void seCreaUnaNotificacionCuyosDatosDeContactoSonLosDelDonante() {
        assertEquals(1, notificacionesRepository.cantidadInstancias());
        assertEquals(receptor.getMails().get(0), getMailDeNotificacion());
      }
    }

  }


  //Helpers
  private DatosDeContacto unosDatosDeContacto(String mail) {
    return new DatosDeContactoBuilder()
        .conMail(mail)
        .conTelefono("1122334455")
        .conMedioPreferido(TipoDeMedio.MAIL)
        .build();
  }

  private String getMailDeNotificacion() {
    return notificacionesRepository.getInstancias().get(0).getReceptor().getMailsParaNotificar().get(0);
  }

}
