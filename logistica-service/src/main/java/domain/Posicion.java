package domain;

/**
 * Representa una posición geografica en grados decimales (DD).
 * Sistema de referencia: WGS84.
 * Latitud: -90 a +90 (N positivo, S negativo).
 * Longitud: -180 a +180 (E positivo, O negativo).
 * Velocidad: kilometros por hora (km/h).
 */

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class Posicion {
  private static final double RADIO_TIERRA_KM = 6371.0;

  @Column(name = "latitud")
  private Double latitud;
  @Column(name = "longitud")
  private Double longitud;

  protected Posicion() {}
  /**
   * Crea una nueva posicion geografica.
   *
   * @param latitud Latitud en grados decimales (-90 a +90).
   * @param longitud Longitud en grados decimales (-180 a +180).
   * @throws IllegalArgumentException si latitud o longitud estan fuera de rango.
   */
  public Posicion(double latitud, double longitud) {
    if (latitud < -90 || latitud > 90) {
      throw new IllegalArgumentException("Latitud inválida");
    }
    if (longitud < -180 || longitud > 180) {
      throw new IllegalArgumentException("Longitud invalida");
    }
    this.latitud = latitud;
    this.longitud = longitud;
  }

  public Double getLatitud() {
    return latitud;
  }

  public Double getLongitud() {
    return longitud;
  }


  /**
   * Calcula la distancia aproximada a otra posicion.
   *
   * <p>Actualmente es un placeholder que retorna 0.0.
   *
   * @param otraPosicion Otra posicion geografica.
   * @return Distancia aproximada en unidades arbitrarias.
   */
  private double calcularDistancia(Posicion otraPosicion) {
    double lat1 = Math.toRadians(this.latitud);
    double lat2 = Math.toRadians(otraPosicion.latitud);
    double deltaLat = Math.toRadians(otraPosicion.latitud - this.latitud);
    double deltaLon = Math.toRadians(otraPosicion.longitud - this.longitud);

    double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
          + Math.cos(lat1) * Math.cos(lat2)
          * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

      return RADIO_TIERRA_KM * c;
    }

  public boolean esIgual(Posicion otraPosicion) {
    return getLatitud().equals(otraPosicion.getLatitud()) && getLongitud().equals(otraPosicion.getLongitud());
  }

  public boolean estaCerca(Posicion otraPosicion) {
    return calcularDistancia(otraPosicion) * 1000 < 30;//Arbitrariamente setee 30 metros como tope
  }

}
