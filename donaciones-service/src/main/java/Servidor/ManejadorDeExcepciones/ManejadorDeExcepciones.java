package Servidor.ManejadorDeExcepciones;

import ExceptionHandler.CantidadDeItemsInvalida;
import ExceptionHandler.DatosInvalidosException;
import ExceptionHandler.EntidadYaExisteException;
import ExceptionHandler.JsonInvalidoException;
import ExceptionHandler.RecursoNoEncontradoException;
import ExceptionHandler.SubCategoriaInvalidaParaTipoDeBien;
import ExceptionHandler.TransaccionException;
import ExceptionHandler.TransicionInvalidaDeEstado;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;                 // NUEVO
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;                               // NUEVO
import java.util.stream.Collectors;
import org.slf4j.Logger;

public class ManejadorDeExcepciones {
  public static void registrarExcepciones(JavalinConfig config, Logger logger) {

    // NUEVO: lo lanzan los controllers web (por ejemplo, donación o foto inexistente)
    config.routes.exception(NotFoundResponse.class, (e, ctx) ->
        responderError(ctx, 404, "No encontramos lo que buscabas", "/error_not_found.hbs"));

    // CAMBIADO: antes era setearStatus(ctx, 404, e); el JSON sale igual, pero el navegador recibe una página
    config.routes.exception(RecursoNoEncontradoException.class, (e, ctx) ->
        responderError(ctx, 404, e.getMessage(), "/error_not_found.hbs"));

    // Este handler atrapa los casos donde el body se evalúa como nulo (ej: mandar la palabra "null")
    config.routes.exception(NullPointerException.class, (e, ctx) -> {
      if (e.getMessage() != null && e.getMessage().contains("readValue(...) must not be null")) {
        logger.warn("Se envió un body nulo o vacío a {}: {}", ctx.path(), e.getMessage());
        ctx.status(400).json(Map.of("error", "El cuerpo de la petición (JSON) no puede estar vacío o ser nulo"));
      } else {
        // Si es un NullPointerException de tu propia lógica de negocio, lo tratamos como Error 500
        logger.error("NullPointerException real en {}: {}", ctx.path(), e.getMessage(), e);
        ctx.status(500).json(Map.of("error", "Error interno del servidor"));
      }
    });

    config.routes.exception(JsonInvalidoException.class, (e, ctx) -> {
      Throwable causa = e.getCause();
      if (causa instanceof MismatchedInputException
          && causa.getMessage() != null
          && causa.getMessage().contains("No content to map due to end-of-input")) {
        logger.warn("Se envió un request con body vacío a {}", ctx.path());
        ctx.status(400).json(Map.of("codigo", "BODY_VACIO", "error", "El cuerpo de la petición no puede estar vacío."));
      } else {
        logger.warn("JSON inválido en request a {}: {}", ctx.path(), e.getMessage());
        ctx.status(400).json(Map.of("error", "JSON inválido: chequear formato"));
      }
    });

    config.routes.exception(EntidadYaExisteException.class, (e, ctx) -> {
      setearStatus(ctx, 409, e);
    });

    config.routes.exception(DatosInvalidosException.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

    config.routes.exception(TransicionInvalidaDeEstado.class, (e, ctx) -> {
      ctx.status(422).json(Map.of(
          "codigo", "TRANSICION_ESTADO_INVALIDA",
          "error", e.getMessage()
      ));
    });

    // CAMBIADO: el navegador recibe una página; el JSON mantiene el formato de MensajeResponse
    config.routes.exception(Exception.class, (e, ctx) -> {
      logger.error("Error no manejado en {}: {}", ctx.path(), e.getClass().getSimpleName(), e);
      responderError(ctx, 500, "Ocurrió un error inesperado. Probá de nuevo en unos minutos.", "/error_server.hbs");
    });

    config.routes.exception(TransaccionException.class, (e, ctx) -> {
      //e.getCause().printStackTrace();
      setearStatus(ctx, 505, e);
    });

    config.routes.exception(CantidadDeItemsInvalida.class, (e, context) -> {
      setearStatus(context, 400, e);
    });

    config.routes.exception(SubCategoriaInvalidaParaTipoDeBien.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

    config.routes.exception(ValidationException.class, (e, ctx) -> {
      String mensaje = e.getErrors().values().stream()
          .flatMap(List::stream)
          .map(ValidationError::getMessage)
          .collect(Collectors.joining("; "));
      ctx.status(400).json(new MensajeResponse(mensaje));
    });
  }

  private static void setearStatus(Context ctx, Integer status, Exception e) {
    ctx.status(status).json(new MensajeResponse(e.getMessage()));
  }

  // NUEVO: ¿el que pide es un navegador?
  private static boolean pideHtml(Context ctx) {
    return Optional.ofNullable(ctx.header("Accept")).orElse("").contains("text/html");
  }

  // NUEVO: navegador -> página con el layout del sitio; resto (tests, otros servicios) -> JSON como siempre
  private static void responderError(Context ctx, int status, String mensaje, String vista) {
    if (pideHtml(ctx)) {
      ctx.status(status).render(vista, Map.of("mensaje", mensaje));
    } else {
      ctx.status(status).json(new MensajeResponse(mensaje));
    }
  }
}