package datosBaseDeDatos.CargaDeDatos;

import static utils.FabricaDeDatos.unDonanteDePrueba;
import static utils.FabricaDeDatos.unDonanteDePruebaConDatosDeContactoTrackeables;

import Donantes.Donante;
import MedioDeNotificacion.TipoDeMedio;
import Notificaciones.NotificacionFija;
import Repositories.Donantes.DonantesRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utils.FabricaDeNotificaciones;

public class GetAllNotificaciones extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new GetAllNotificaciones().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    cargarEscenario();
  }

  public void cargarEscenario() {
    DonantesRepository donantesRepository = DonantesRepository.getInstance();
    NotificacionesRepository notificacionesRepository = NotificacionesRepository.getInstance();
    Donante donante = unDonanteDePruebaConDatosDeContactoTrackeables();
    withTransaction(() -> {
      donantesRepository.registrar(donante);
    });

    withTransaction(() -> {
      notificacionesRepository.registrar(FabricaDeNotificaciones.notificacionViaWpp("mensaje wpp", donante.getDatosDeContacto()));
      notificacionesRepository.registrar(FabricaDeNotificaciones.notificacionViaSms("mensaje sms", donante.getDatosDeContacto()));
      notificacionesRepository.registrar(FabricaDeNotificaciones.holaMundoViaMail(donante.getDatosDeContacto()));
    });

  }

}