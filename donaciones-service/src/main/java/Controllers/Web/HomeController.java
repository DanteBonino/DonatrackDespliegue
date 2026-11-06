package Controllers.Web;

import Donaciones.DonacionIndependiente;
import Repositories.Donaciones.DonacionesRepository;
import EstadoDonacion.EstadoDonacion;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;

public class HomeController {

  public void index(Context ctx) {
    ctx.render("home.hbs", Map.of(
        "titulo", "DonaTrack",
        "subtitulo", "Sistema de Gestión y Trazabilidad de Donaciones",
        "descripcion", "Organizamos, registramos y monitoreamos donaciones "
            + "desde su recepción en el depósito hasta la entrega a entidades "
            + "beneficiarias, fortaleciendo la confianza de donantes y beneficiarios."
    ));
  }

  public void donacionesEntregadas(Context ctx) {
    ctx.render("donaciones-entregadas.hbs", Map.of(
        "titulo", "Donaciones Entregadas",
        "mapaData", getMapaData("mapa-entregadas", "600px")
    ));
  }

  public void legal(Context ctx) {
    ctx.render("legal.hbs", Map.of(
        "titulo", "Información Legal y Privacidad"
    ));
  }

  private Map<String, Object> getMapaData(String mapId, String height) {
    List<Object[]> filas = DonacionesRepository.getInstance()
        .getMarcadoresDeEntregadas(100);

    List<Map<String, Object>> marcadores = filas.stream()
        .filter(f -> f[1] != null && f[2] != null)   // lat/lng no nulos
        .map(this::aMarcadorDesdeFila)


        .toList();

    return Map.of(
        "mapId", mapId,
        "mapHeight", height,
        "centerLat", -34.5984,
        "centerLng", -58.4200,
        "zoom", 12,
        "marcadores", marcadores
    );
  }

  private Map<String, Object> aMarcadorDesdeFila(Object[] fila) {


    return Map.of(
        "lat", fila[1],
        "lng", fila[2],
        "titulo", fila[0],
        "descripcion", fila[3] + " x " + fila[4]
    );
  }

}