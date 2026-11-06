package Controllers.DTOsSinController;

import Controllers.DTOsSinController.Bien.BienDto;
import Donaciones.Item;
import java.util.List;

public record ItemDto (BienDto bien, int cantidadUnidades, List<String> fotos) {
  public static ItemDto desde(Item item) {
    return new ItemDto(BienDto.desde(item.getBien()), item.getCantidadDeUnidades(), item.getFotos());
  }
}
