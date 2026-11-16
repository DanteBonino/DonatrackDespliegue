package Controllers.Web;

import Contacto.DatosDeContacto;
import Notificaciones.Notificacion;
import Repositories.Generales.paginacion.Pagina;
import Repositories.Notificaciones.NotificacionesRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NotificacionesWebController implements ConPaginacion {

  public void showNotificaciones(Context context, DatosDeContacto contacto) {
    int paginaActual = numeroDePagina(context);
    int tamanioPagina = 5;

    List<Map<String, Object>> noLeidas = notificacionesRepository()
        .getNoLeidasEntregadasDe(contacto).stream()
        .map(n -> {
          Map<String, Object> map = new HashMap<>();
          map.put("mensaje", n.getMensaje());
          map.put("hrefLeer", "/notificaciones/" + n.getIdDominio() + "/leer?pagina=" + paginaActual);
          return map;
        })
        .toList();

    Pagina<Notificacion> paginaLeidas = notificacionesRepository()
        .getLeidasEntregadasPaginadasDe(contacto, paginaActual, tamanioPagina);

    List<Map<String, Object>> leidas = paginaLeidas.items().stream()
        .map(n -> {
          Map<String, Object> map = new HashMap<>();
          map.put("mensaje", n.getMensaje());
          return map;
        })
        .toList();

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Notificaciones");
    model.put("noLeidas", noLeidas);
    model.put("leidas", leidas);
    model.put("hayNoLeidas", !noLeidas.isEmpty());
    model.put("hayLeidas", !leidas.isEmpty());
    model.put("hayNotificaciones", !noLeidas.isEmpty() || !leidas.isEmpty());
    model.put("paginas", paginaLeidas.aModelo());

    mensajeDeLaNotificacionLeida(context, contacto).ifPresent(m -> model.put("mensajeLeida", m));

    context.render("notificaciones.hbs", model);
  }

  private Optional<String> mensajeDeLaNotificacionLeida(Context context, DatosDeContacto contacto) {
    return Optional.ofNullable(context.queryParam("leida"))
        .flatMap(idDominio -> notificacionesRepository().obtenerUnoSiExisteSegun("idDominio", idDominio))
        .filter(n -> n.getReceptor().esLaMismaEntidad(contacto.getIdDominio()))
        .map(Notificacion::getMensaje);
  }

  public void marcarComoLeida(Context ctx, DatosDeContacto contacto) {
    Notificacion notificacion = notificacionesRepository()
        .obtenerUnoSiExisteSegun("idDominio", ctx.pathParam("id"))
        .filter(n -> n.getReceptor().esLaMismaEntidad(contacto.getIdDominio()))
        .orElseThrow(NotFoundResponse::new);

    notificacion.marcarComoLeida();
    ctx.redirect("/mis-notificaciones?pagina=" + numeroDePagina(ctx) + "&leida=" + notificacion.getIdDominio());
  }

  public void campana(Context ctx, DatosDeContacto contacto) {
    List<Map<String, Object>> recientes = notificacionesRepository()
        .getNoLeidasRecientesDe(contacto, 3).stream()
        .map(n -> Map.<String, Object>of("mensaje", n.getMensaje()))
        .toList();

    long noLeidas = notificacionesRepository().contarNoLeidas(contacto);

    ctx.render("partials/_campana.hbs", Map.of(
        "recientes", recientes,
        "noLeidas", noLeidas,
        "restantes", Math.max(0, noLeidas - recientes.size())));
  }

  private NotificacionesRepository notificacionesRepository() {
    return NotificacionesRepository.getInstance();
  }
}
