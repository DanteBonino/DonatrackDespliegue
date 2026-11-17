package Controllers.Web;

import io.javalin.http.Context;
import io.javalin.validation.ValidationError;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ConValidacionDeFormulario {

  default Object getValue(Context ctx, String inputLabel) {
    return ctx.formParam(inputLabel) != null ? ctx.formParam(inputLabel) : "";
  }

  default void volver(Context ctx, String pagina, Map<String,String> errores, Map<String, Object> valores) {
    ctx.sessionAttribute("errores" + getFinalDeClave(), errores);
    ctx.sessionAttribute("valores" + getFinalDeClave(), valores);
    ctx.status(400).redirect(pagina);
  }

  default Map<String, String> obtenerErroresConDetalle( Map<String, List<ValidationError<Object>>> errors){
    return errors.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> {
              var validationError = entry.getValue().get(0);
              return validationError.getMessage();
            })
        );
  }

  default Map<String, String> getErrores(Context ctx) {
    return getMapaDeSesionYLimpiar(ctx, "errores"+getFinalDeClave());
  }

  default Map<String, String> getValores(Context ctx) {
    return getMapaDeSesionYLimpiar(ctx, "valores"+getFinalDeClave());
  }

  default Map<String, String> getMapaDeSesionYLimpiar(Context ctx, String propiedad) {
    Map<String, String> valores = ctx.sessionAttribute(propiedad);
    ctx.sessionAttribute(propiedad, null);
    if (valores == null) valores = Map.of();
    return valores;
  }

  String getFinalDeClave();
}
