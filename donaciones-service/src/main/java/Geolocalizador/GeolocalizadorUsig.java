package Geolocalizador;

import Direccion.*;
import Direccion.builders.PosicionBuilder;
import ExceptionHandler.NoSePudoLocalizarLaCalle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class GeolocalizadorUsig implements IGeolocalizador {
  public static final String URL_BASE = "https://servicios.usig.buenosaires.gob.ar/normalizar/";

  private final HttpClient client = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(3))
      .build();
  private final ObjectMapper mapper = new ObjectMapper();

  private String urlBase;

  public GeolocalizadorUsig(String url) {
    this.urlBase = url;
  }

  @Override
  public Posicion getPosicionDe(Calle calle) {
    try {
      HttpResponse<String> respuesta = client.send(pedidoPara(calle), HttpResponse.BodyHandlers.ofString());
      if (respuesta.statusCode() != 200) {
        throw new IOException("USIG respondió " + respuesta.statusCode());
      }
      return extraerPosicion(respuesta.body())
          .orElseThrow(() -> new NoSePudoLocalizarLaCalle(calle));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new NoSePudoLocalizarLaCalle(calle);
    } catch (IOException e) {
      throw new NoSePudoLocalizarLaCalle(calle);
    }
  }

  private HttpRequest pedidoPara(Calle calle) {
    String direccion = calle.getFormatoLegible() + ", caba";
    String url = this.urlBase + "?direccion=" + URLEncoder.encode(direccion, StandardCharsets.UTF_8)
        + "&geocodificar=true";
    return HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(5))
        .GET()
        .build();
  }

  private Optional<Posicion> extraerPosicion(String json) throws IOException {
    JsonNode coordenadas = mapper.readTree(json)
        .path("direccionesNormalizadas").path(0).path("coordenadas");
    JsonNode x = coordenadas.path("x");
    JsonNode y = coordenadas.path("y");
    if (x.isMissingNode() || y.isMissingNode()) return Optional.empty();
    return Optional.of(new PosicionBuilder()
        .conLatitud(y.asDouble())
        .conLongitud(x.asDouble())
        .build());
  }
}
