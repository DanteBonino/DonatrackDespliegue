package Controllers.Lote;

import Donaciones.Lote.Lote;
import Donaciones.Lote.LoteBuilder;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Repositories.Donantes.DonantesRepository;
import java.util.List;

public class CreacionLoteDto {
  private String donanteId;
  private List<ItemDtoParaCreacionDeLote> items;

  public Lote crearLote(PublicadorDeCambiosEstados publicadorDeCambiosEstados) {
    LoteBuilder builder = new LoteBuilder();
    builder.conDonante(DonantesRepository.getInstance().findById(donanteId));
    builder.conItems(getItems().stream().map(ItemDtoParaCreacionDeLote::crearItem).toList());
    builder.conPublicadorParaLasDonaciones(publicadorDeCambiosEstados);
    return    builder.build();

  }


  public String getDonanteId() {
    return donanteId;
  }

  public List<ItemDtoParaCreacionDeLote> getItems() {
    return items;
  }

}
