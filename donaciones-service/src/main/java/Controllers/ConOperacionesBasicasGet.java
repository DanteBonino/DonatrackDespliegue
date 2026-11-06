package Controllers;

import AsignacionDeDonaciones.SolicitudDeAsignacion.SolicitudDeAsignacion;
import Controllers.Donanciones.Get.DonacionResponseDto;
import Controllers.SolicitudesDeAsignacion.EjecutarAlgoritmo.SolicitudDeAsignacionResponseDto;
import Persistible.Persistible;
import Repositories.Generales.RepositorioBasico;
import io.javalin.http.Context;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public interface ConOperacionesBasicasGet<T extends Persistible, R> extends ConRepositorioPrincipal<T> {

  Function<T, R> conversorARespuesta();

  default void getAll(Context context) {
    context.json(obtenerEnFormatoEnviable(repositorioPrincipal().getInstancias()));
  }

  default void getOne(Context context) {
    this.getOne(context, context.pathParam("id"));
  }

  default void getOne(Context context, @NotNull String id) {
    context.json(conversorARespuesta().apply(repositorioPrincipal().findById(id)));
  }

  default List<R> obtenerEnFormatoEnviable(List<T> entidades) {
    return entidades.stream().map(conversorARespuesta()).toList();
  }
}
