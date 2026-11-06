package Controllers.EntidadesBenefeciarias;

import Controllers.ConOperacionesBasicasGet;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Generales.RepositorioBasico;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class EntidadesBeneficiariasController implements CrudHandler, ConOperacionesBasicasGet<EntidadBeneficiaria, EntidadBeneficiariaResponseDto> {

  public EntidadesBeneficiariasController() {
  }

  @Override
  public RepositorioBasico<EntidadBeneficiaria> repositorioPrincipal() {
    return repository();
  }

  @Override
  public Function<EntidadBeneficiaria, EntidadBeneficiariaResponseDto> conversorARespuesta() {
    return EntidadBeneficiariaResponseDto::desde;
  }

  @Override
  public void getAll(@NotNull Context contexto) {
    ConOperacionesBasicasGet.super.getAll(contexto);
  }

  @Override
  public void getOne(@NotNull Context contexto, @NotNull String idBeneficiario) {
   ConOperacionesBasicasGet.super.getOne(contexto, idBeneficiario);
  }

  @Override
  public void create(Context context) {
    EntidadBeneficiariaRequestDto potenciaNuevoBeneficiario = context.bodyValidator(EntidadBeneficiariaRequestDto.class)
        .check(obj -> obj.getDireccion() != null, "la direccion es necesaria")
        .get();
    EntidadBeneficiaria nuevoBeneficiario = potenciaNuevoBeneficiario.crearInstancia();
    repository().registrar(nuevoBeneficiario);
    context.header("Location", "/entidades-beneficiarias/" + nuevoBeneficiario.getIdDominio());
    context.status(201).json(EntidadBeneficiariaResponseDto.desde(nuevoBeneficiario));
  }

  @Override
  public void update(Context context, @NotNull String resourceId) {
    EntidadBeneficiariaRequestDto datosActualizados = context.bodyAsClass(EntidadBeneficiariaRequestDto.class);
    repository().actualizarInstancia(resourceId, datosActualizados);
    context.status(204);
  }

  @Override
  public void delete(Context context, @NotNull String idEntidadBeneficiaria) {
    repository().eliminarInstancia(idEntidadBeneficiaria);
    context.status(204);
  }

  private BeneficiariosRepository repository() {
    return BeneficiariosRepository.getInstance();
  }
}
