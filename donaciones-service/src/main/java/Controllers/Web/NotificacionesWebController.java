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
    Pagina<Notificacion> pagina = notificacionesRepository()
        .getEntregadasDe(contacto, numeroDePagina(context), CANTIDAD_DE_ELEMENTOS_POR_PAGINA);

    List<Map<String, Object>> filas = pagina.items().stream()
        .map(n -> Map.<String, Object>of(
            "mensaje", n.getMensaje(),
            "nueva", !n.estaLeida(),
            "hrefLeer", "/notificaciones/" + n.getIdDominio() + "/leer?pagina=" + pagina.actual()))
        .toList();

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Notificaciones");
    model.put("notificaciones", filas);
    model.put("hayNotificaciones", !filas.isEmpty());
    model.put("paginas", pagina.aModelo());
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
    ctx.redirect("/notificaciones?pagina=" + numeroDePagina(ctx) + "&leida=" + notificacion.getIdDominio());
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
