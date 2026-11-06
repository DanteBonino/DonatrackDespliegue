package ServiceLocator;

import Geolocalizador.IGeolocalizador;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;

public class ServiceLocator {
  private static PublicadorDeCambiosEstados publicador;
  private static IGeolocalizador geolocalizador;

  public static void configurar(PublicadorDeCambiosEstados publicadorConfigurado) {
    publicador = publicadorConfigurado;
  }

  public static PublicadorDeCambiosEstados obtenerPublicadorCambioDeEstado() {
    return publicador;
  }

  public static void configurarGeolocalizadorCon(IGeolocalizador geolocalizadorConfigurado) {
    geolocalizador = geolocalizadorConfigurado;
  }

  public static IGeolocalizador obtenerGeolocalizador() {
    return geolocalizador;
  }

}
