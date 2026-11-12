package Servidor.templates;

import Servidor.ManejadorDeSesiones.DatosSesion;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.FileRenderer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import utils.FechaRelativa;

public class JavalinHandlebars implements FileRenderer {
  private static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");
  private Handlebars handlebars;

  private boolean esMismoPath(Options options, Object pathDestino) {
    Object pathActual = options.get("pathActual");
    return pathDestino != null && pathDestino.equals(pathActual);
  }

  public JavalinHandlebars() {
    TemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
    this.handlebars = new Handlebars(loader);

    this.handlebars.registerHelper("eq", (context, options) -> {
      Object a = context;
      Object b = options.param(0);
      return a != null && a.equals(b);
    });

    handlebars.registerHelper("esDonante", (context, options) -> {
      DatosSesion sesion = (DatosSesion) options.context.get("sesion");
      return sesion != null && sesion.esDonante();
    });

    handlebars.registerHelper("esBeneficiaria", (context, options) -> {
      DatosSesion sesion = (DatosSesion) options.context.get("sesion");
      return sesion != null && sesion.esBeneficiario();
    });

    handlebars.registerHelper("esAdmin", (context, options) -> {
      DatosSesion sesion = (DatosSesion) options.context.get("sesion");
      return sesion != null && sesion.esAdministrado();
    });

    handlebars.registerHelper("isActive", (destino, options) ->
        esMismoPath(options, destino) ? "active" : "");

    handlebars.registerHelper("estamosEnDestino", (destino, options) ->
        esMismoPath(options, destino));

    handlebars.registerHelper("fechaRelativa", (LocalDateTime fecha, Options options) -> {
      if (fecha == null) return "";
      String texto = FechaRelativa.texto(fecha.toLocalDate(), LocalDate.now(ZONA));
      String completa = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
      return new Handlebars.SafeString(
          "<time datetime=\"" + fecha + "\" title=\"" + completa + "\">" + texto + "</time>");
    });

  }

  @NotNull
  @Override
  public String render(@NotNull String path,
                       @NotNull Map<String, ?> model,
                       @NotNull Context context) {
    try {
      String cleanPath = path.replace("templates/", "").replace(".hbs", "");
      Template template = handlebars.compile(cleanPath);

      Map<String, Object> modeloCompleto = new HashMap<>(model);

      modeloCompleto.put("pathActual", context.path());
      ManejadorDeSesion.obtenerSesion(context).ifPresent(datosDeSesion -> modeloCompleto.put("sesion", datosDeSesion));

      return template.apply(modeloCompleto);
    } catch (IOException e) {
      context.status(HttpStatus.NOT_FOUND);
      return "No se encuentra la página indicada...";
    }
  }
}