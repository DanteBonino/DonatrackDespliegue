package Injector;
import BusNotificaciones.*;
import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoDeAsignacion;
import AsignacionDeDonaciones.AlgoritmosDeAsignacion.AlgoritmoMixtoPorInterseccion;
import BusNotificaciones.BusNotificaciones;
import Config.Configuracion;
import Geolocalizador.GeolocalizadorUsig;
import Geolocalizador.IGeolocalizador;
import InactividadDeDonante.NotificadorDeInactividad;
import InactividadDeDonante.PublicadorDeInactividad;
import MedioDeNotificacion.Correo.Adapter.Implementaciones.ClienteCorreoSendGrid;
import MedioDeNotificacion.Correo.MedioCorreo;
import MedioDeNotificacion.Mensajeria.Adapter.Implementaciones.ClienteMensajeriaTwilio;
import MedioDeNotificacion.Mensajeria.SMS.MedioSMS;
import MedioDeNotificacion.Mensajeria.Wapp.MedioWhatsApp;
import ObserversCambioDeEstado.ConfirmarAsignacion.NotificadorDeDonanteAnteAsignacion;
import ObserversCambioDeEstado.ConfirmarAsignacion.NotificadorDeEntidadBeneficiariaAnteAsignacion;
import ObserversCambioDeEstado.EntregaConExito.NotificadorEntregaConExitoDonante;
import ObserversCambioDeEstado.EntregaConExito.NotificadorEntregaConExitoEntidad;
import ObserversCambioDeEstado.EntregaFallida.NotificadorEntregaFallidaAdministrador;
import ObserversCambioDeEstado.EntregaFallida.NotificadorEntregaFallidaDonante;
import ObserversCambioDeEstado.EntregaFallida.NotificadorEntregaFallidaEntidad;
import ObserversCambioDeEstado.InicioDeRuta.GeneradorLinkApi;
import ObserversCambioDeEstado.InicioDeRuta.NotificadorDonanteInicioDeRuta;
import ObserversCambioDeEstado.InicioDeRuta.NotificadorEntidadInicioDeRuta;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Donaciones.DonacionesRepository;
import ServiceLocator.ServiceLocator;
import TareasProgramadas.CrearSolicitudesDeAsignacion.CrearSolicitudesDeAsignacion;

public class Injector {
  private final Configuracion configuracion = new Configuracion();

  public PublicadorDeCambiosEstados getPublicadorCambiosDeEstado() {
    PublicadorDeCambiosEstados publicadorDeCambiosEstados = new PublicadorDeCambiosEstados();
    GeneradorLinkApi generadorLinkDeTrackeoEntrega = new GeneradorLinkApi(configuracion.getLogisticaServiceUrl());

    publicadorDeCambiosEstados.suscribir(new NotificadorEntidadInicioDeRuta(generadorLinkDeTrackeoEntrega));
    publicadorDeCambiosEstados.suscribir(new NotificadorDonanteInicioDeRuta(generadorLinkDeTrackeoEntrega));
    ;
    publicadorDeCambiosEstados.suscribir(new NotificadorEntregaConExitoEntidad());
    publicadorDeCambiosEstados.suscribir(new NotificadorEntregaConExitoDonante());

    publicadorDeCambiosEstados.suscribir(new NotificadorEntregaFallidaDonante());
    publicadorDeCambiosEstados.suscribir(new NotificadorEntregaFallidaEntidad());
    publicadorDeCambiosEstados.suscribir(new NotificadorEntregaFallidaAdministrador());

    publicadorDeCambiosEstados.suscribir(new NotificadorDeDonanteAnteAsignacion());
    publicadorDeCambiosEstados.suscribir(new NotificadorDeEntidadBeneficiariaAnteAsignacion());

    return publicadorDeCambiosEstados;
  }

  public PublicadorDeInactividad getPublicadorDeInactividad() {
    PublicadorDeInactividad publicador = new PublicadorDeInactividad();
    publicador.suscribir(new NotificadorDeInactividad());
    return publicador;
  }

  public AlgoritmoDeAsignacion getAlgoritmoDeAsignacion() { return AlgoritmoMixtoPorInterseccion.algoritmoEstandar(); }
  public CrearSolicitudesDeAsignacion getCrearSolicitudesDeAsignacion() { return new CrearSolicitudesDeAsignacion(this.getAlgoritmoDeAsignacion(), DonacionesRepository.getInstance()); }

  public BusNotificaciones getBusNotificaciones() {
    return new BusNotificaciones(getFactory());
  }

  private ClienteMensajeriaTwilio getClienteMensajeriaTwilio(String emisor) {
    return new ClienteMensajeriaTwilio(
        configuracion.getAuthTokenTwilio(),
        configuracion.getAccountSidTwilio(),
        emisor
    );
  }

  private ClienteMensajeriaTwilio getClienteMensajeriaWpp() {
    return getClienteMensajeriaTwilio(configuracion.getEmisorWhatsAppTwilio());
  }

  private ClienteMensajeriaTwilio getClienteMensajeriaSms() {
    return getClienteMensajeriaTwilio(configuracion.getEmisorSmsTwilio());
  }

  private MedioCorreo getMedioCorrero() {
    return new MedioCorreo(
        new ClienteCorreoSendGrid(configuracion.getApiKeySendGrid(), configuracion.getEmisorSendGrid())
    );
  }

  private MedioSMS getMedioSms() {
    return new MedioSMS(getClienteMensajeriaSms());
  }

  private MedioWhatsApp getMedioWpp() {
    return new MedioWhatsApp(getClienteMensajeriaWpp());
  }

  private MedioNotificacionFactory getFactory() {
    return  new MedioNotificacionFactory(getMedioCorrero(), getMedioWpp(), getMedioSms());
  }

  public void configurarServiceLocator() {
    ServiceLocator.configurar(getPublicadorCambiosDeEstado());
  }

  public void configurarGeolocalizador() {
    ServiceLocator.configurarGeolocalizadorCon(new GeolocalizadorUsig(GeolocalizadorUsig.URL_BASE));
  }
}
