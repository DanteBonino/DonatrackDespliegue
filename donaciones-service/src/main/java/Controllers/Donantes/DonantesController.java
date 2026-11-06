package Controllers.Donantes;

import Direccion.Calle;
import Donantes.Donante;
import Injector.Injector;
import Repositories.Donantes.DonantesRepository;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DonantesController implements CrudHandler {


  @Override
  public void getAll(Context contexto) {
    List<DonanteResponseDto> respuesta = donantesRepository().getInstancias().stream()
        .map(DonanteResponseDto::desde)
        .toList();
    contexto.json(respuesta);
  }

  @Override
  public void getOne(Context contexto, @NotNull String idDonante) {
    contexto.json(DonanteResponseDto.desde(donantesRepository().findById(idDonante)));
  }

  @Override
  public void create(Context context) {
    DonanteRequestDto potencialNuevoDonante = context.bodyValidator(DonanteRequestDto.class)
        .check(body -> body.getCalleDto() != null, "La calle es necesaria")
        .get();
    Donante nuevoDonante = potencialNuevoDonante.crearInstancia();
    donantesRepository().registrar(nuevoDonante);
    context.header("Location", "/donantes/" + nuevoDonante.getIdDominio());
    context.status(201).json(DonanteResponseDto.desde(nuevoDonante));
  }

  @Override
  public void update(Context context, @NotNull String resourceId) {
    DonanteRequestDto datosActualizados = context.bodyAsClass(DonanteRequestDto.class);
    donantesRepository().actualizarInstancia(resourceId, datosActualizados);
    context.status(204);
  }

  @Override
  public void delete(Context context, @NotNull String idDonante) {
    donantesRepository().eliminarInstancia(idDonante);
    context.status(204);
  }

  private DonantesRepository donantesRepository() {
    return DonantesRepository.getInstance();
  }
}
