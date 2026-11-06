package Controllers.Lote;

import Controllers.Donanciones.Get.DonacionResponseDto;
import Controllers.Donantes.DonanteResponseDto;
import Donaciones.Item;
import Donaciones.Lote.Lote;
import Donantes.Donante;
import java.util.List;

public class LoteDto {
  private DonanteResponseDto donante;
  private List<DonacionResponseDto> donaciones;

  public static LoteDto desde(Lote lote) {
    LoteDto loteDto = new LoteDto();
    loteDto.setDonante(DonanteResponseDto.desde(lote.getDonante()));
    loteDto.setDonaciones(lote.getDonaciones().stream().map(DonacionResponseDto::desde).toList());
    return loteDto;
  }

  public void setDonante(DonanteResponseDto donante) {
    this.donante = donante;
  }

  public void setDonaciones(List<DonacionResponseDto> donaciones) {
    this.donaciones = donaciones;
  }

  public DonanteResponseDto getDonante() {
    return donante;
  }

  public List<DonacionResponseDto> getDonaciones() {
    return donaciones;
  }

}
