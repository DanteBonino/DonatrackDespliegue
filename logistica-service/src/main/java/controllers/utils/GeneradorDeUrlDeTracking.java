package controllers.utils;

import domain.Posicion;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GeneradorDeUrlDeTracking {
  private final String direccionOrigen;
  private final Posicion posicionOrigen;

  public GeneradorDeUrlDeTracking(String direccionOrigen, Posicion posicionOrigen) {
    this.direccionOrigen = direccionOrigen;
    this.posicionOrigen = posicionOrigen;
  }

  public String urlTracking(Posicion posicion) {
    if (estaEnDeposito(posicion)) {
      return this.getUrlBase() + "/search/?api=1&query=" +
          URLEncoder.encode(direccionOrigen, StandardCharsets.UTF_8);
    }
    return String.format(Locale.US, this.getUrlBase() + "?q=%f,%f&z=15",
        posicion.getLatitud(), posicion.getLongitud());
  }

  private boolean estaEnDeposito(Posicion posicion) {
    return posicion.estaCerca(posicionOrigen);
  }

  private String getUrlBase() {
    return "https://www.google.com/maps";
  }
}
