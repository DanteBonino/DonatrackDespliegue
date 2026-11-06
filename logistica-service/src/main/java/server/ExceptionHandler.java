package server;

import dtos.MensajeResponse;
import exceptions.AlmaceniamientoDeComprobanteException;
import exceptions.CamionNoAsignadoIntentaAccionarSobreLaRutaException;
import exceptions.CamionNoDisponibleException;
import exceptions.CamionSinRutaActivaException;
import exceptions.EstadoEntregaInvalidoException;
import exceptions.EstadoRutaInvalidoException;
import exceptions.NoEstaEnCursoElCamionException;
import exceptions.ProcesarCallBackDePlanificacionException;
import exceptions.RecursoNoEncontradoException;
import exceptions.RutaSinEntregasException;
import exceptions.TransaccionException;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import java.util.Map;

public class ExceptionHandler {
  public static void registrar(JavalinConfig config) {
    config.routes.exception(RecursoNoEncontradoException.class, (e, ctx) -> {
      setearStatus(ctx, 404, e);
    });

    config.routes.exception(CamionNoDisponibleException.class, (e, ctx) -> {
      setearStatus(ctx, 409, e);
    });

    config.routes.exception(TransaccionException.class, (e, ctx) -> {
      setearStatus(ctx, 505, e);
    });

    config.routes.exception(NoEstaEnCursoElCamionException.class, (e, ctx) -> {
      setearStatus(ctx, 404, e);
    });

    config.routes.exception(CamionSinRutaActivaException.class, (e, ctx) -> {
      setearStatus(ctx, 409, e);
    });

    config.routes.exception(IllegalArgumentException.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

    config.routes.exception(RutaSinEntregasException.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

    config.routes.exception(EstadoRutaInvalidoException.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

    config.routes.exception(CamionNoAsignadoIntentaAccionarSobreLaRutaException.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

    config.routes.exception(EstadoEntregaInvalidoException.class, (e, ctx) -> {
      setearStatus(ctx, 409, e);
    });

    config.routes.exception(AlmaceniamientoDeComprobanteException.class, (e, ctx) -> {
      setearStatus(ctx, 500, e);
    });

    config.routes.exception(ProcesarCallBackDePlanificacionException.class, (e, ctx) -> {
      setearStatus(ctx, 400, e);
    });

  }

  private static void setearStatus(Context ctx, Integer status, Exception e) {
    ctx.status(status).json(new MensajeResponse(e.getMessage()));
  }
}
