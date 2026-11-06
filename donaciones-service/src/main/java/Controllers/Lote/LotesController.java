package Controllers.Lote;

import Controllers.ConOperacionesBasicasGet;
import Controllers.Donantes.DonanteResponseDto;
import Donaciones.Lote.Lote;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Generales.RepositorioBasico;
import Repositories.Lotes.LotesRepository;
import io.javalin.http.Context;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class LotesController implements ConOperacionesBasicasGet<Lote, LoteDto> {
  private PublicadorDeCambiosEstados publicadorBase;

  public LotesController(PublicadorDeCambiosEstados publicadorDeCambiosEstados) {
    this.publicadorBase = publicadorDeCambiosEstados;
  }

  @Override
  public Function<Lote, LoteDto> conversorARespuesta() {
    return LoteDto::desde;
  }

  @Override
  public RepositorioBasico<Lote> repositorioPrincipal() {
    return LotesRepository.getInstance();
  }

  public void create(@NotNull Context context) {
    CreacionLoteDto dto = context.bodyValidator(CreacionLoteDto.class)
        .check(d -> d.getItems() != null, "La lista de items es obligatoria")
        .check(d -> d.getDonanteId() != null, "El id del donantes es obligatorio")
        .get();
    Lote nuevoLote = dto.crearLote(publicadorBase);
    repositorioPrincipal().registrar(nuevoLote);
    context.status(201).json(LoteDto.desde(nuevoLote));
  }
}
