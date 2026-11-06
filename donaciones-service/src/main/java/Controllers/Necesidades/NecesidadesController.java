package Controllers.Necesidades;

import EntidadesBeneficiarias.EntidadBeneficiaria;
import Necesidades.Necesidad;
import Repositories.Beneficiarios.BeneficiariosRepository;
import io.javalin.http.Context;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class NecesidadesController {

  public void getAll(@NotNull Context contexto) {
    List<NecesidadResponseDto> respuesta = getEntidadBeneficiaria(contexto)
        .getNecesidades()
        .stream()
        .map(NecesidadResponseDto::desde)
        .toList();
    contexto.json(respuesta);
  }

  public void getOne(@NotNull Context contexto) {
    Necesidad necesidad = getEntidadBeneficiaria(contexto).buscarNecesidadPorId(contexto.pathParam("idNecesidad"));
    contexto.json(NecesidadResponseDto.desde(necesidad));
  }

  public void create(@NotNull Context contexto) {
    EntidadBeneficiaria entidad = getEntidadBeneficiaria(contexto);
    NecesidadRequestDto datos = contexto.bodyAsClass(NecesidadRequestDto.class);
    Necesidad nuevaNecesidad = datos.crearInstancia();
    entidad.agregarNecesidad(nuevaNecesidad);
    contexto.header("Location",
        "/entidades-beneficiarias/" + entidad.getIdDominio() + "/necesidades/" + nuevaNecesidad.getIdDominio());
    contexto.status(201).json(NecesidadResponseDto.desde(nuevaNecesidad));
  }

  public void delete(@NotNull Context contexto) {
    getEntidadBeneficiaria(contexto).eliminarNecesidad(contexto.pathParam("idNecesidad"));
    contexto.status(204);
  }

  private EntidadBeneficiaria getEntidadBeneficiaria(Context contexto) {
    return BeneficiariosRepository.getInstance().findById(contexto.pathParam("idBeneficiario"));
  }
}
