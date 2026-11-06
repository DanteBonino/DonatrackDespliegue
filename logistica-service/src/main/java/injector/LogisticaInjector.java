package injector;

import config.Configuracion;
import controllers.utils.AlmacenamientoDeComprobantes;
import domain.Entrega;
import controllers.utils.GeneradorDeUrlDeTracking;
import domain.Posicion;
import domain.eventos.EntregaPublicador;
import externo.donaciones.DonacionesHttpAdapter;
import externo.donaciones.DonacionesServiceAdapter;
import externo.donaciones.SincronizadorDeEstadosObserver;
import java.nio.file.Path;
import java.nio.file.Paths;
import repositories.EntregasRepository.EntregasRepository;
import repositories.EntregasRepository.EntregasRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryConPersistencia;
import externo.callback.CallbackPlanificadorReceiver;
import externo.planificador.PlanificadorRutasAdapter;

public class LogisticaInjector {
  private DonacionesServiceAdapter donacionesServiceAdapter;
  private SincronizadorDeEstadosObserver sincronizadorDeEstadosObserver;

  private CallbackPlanificadorReceiver callbackReceiver;
  private PlanificadorRutasAdapter planificadorAdapter;
  private EntregaPublicador entregaPublicador;
  private GeneradorDeUrlDeTracking generadorDeUrlDeTracking;
  private Posicion posicionDeposito;
  private AlmacenamientoDeComprobantes almacenamientoDeComprobantes;

  public LogisticaInjector() {
    Configuracion configuracion = new Configuracion();
    this.posicionDeposito = new Posicion(configuracion.getDepositoLatitud(), configuracion.getDepositoLongitud());
    this.generadorDeUrlDeTracking = new GeneradorDeUrlDeTracking(new Configuracion().getDireccionDeposito(), posicionDeposito);
    String urlDonaciones = configuracion.getUrlDonacionesService();
    this.donacionesServiceAdapter = new DonacionesHttpAdapter(urlDonaciones);
    this.sincronizadorDeEstadosObserver = new SincronizadorDeEstadosObserver(this.donacionesServiceAdapter);

    this.callbackReceiver = new CallbackPlanificadorReceiver();

    String urlPlanificador = configuracion.getUrlPlanificadorExterno();
    this.planificadorAdapter = new PlanificadorRutasAdapter(urlPlanificador);

    this.entregaPublicador = new EntregaPublicador();
    this.entregaPublicador.suscribir(this.sincronizadorDeEstadosObserver);
    this.almacenamientoDeComprobantes = new AlmacenamientoDeComprobantes(Paths.get("uploads"));
  }

  public CallbackPlanificadorReceiver getCallbackReceiver() {
    return this.callbackReceiver;
  }

  public PlanificadorRutasAdapter getPlanificadorAdapter() {
    return this.planificadorAdapter;
  }

  public EntregaPublicador getEntregaPublicador() {
    return entregaPublicador;
  }

  public DonacionesServiceAdapter getDonacionesServiceAdapter() {
    return this.donacionesServiceAdapter;
  }

  public PlanificadorRutasAdapter getPlanificadorRutasAdapter() {
    return this.planificadorAdapter;
  }

  public GeneradorDeUrlDeTracking getGeneradorDeUrlDeTracking() {
    return this.generadorDeUrlDeTracking;
  }

  public Posicion getPosicionDeposito() {
    return this.posicionDeposito;
  }
  public AlmacenamientoDeComprobantes getAlmacenamientoDeComprobantes() {
    return this.almacenamientoDeComprobantes;
  }
}