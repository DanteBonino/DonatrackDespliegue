package utils;

import Bienes.Bien;
import Donaciones.Item;
import java.time.LocalDate;
import java.util.List;

public class FabricaDeItems {
  public static Item nuevoItem(Bien nuevoBien, int cantidadDeUnidades, List<String> fotos) {
    return new Item(nuevoBien, cantidadDeUnidades, fotos);
  }

  public static Item itemDePaqueteDeArroz500GramosConNUnidadesYFechaDeVencimiento(int cantidadDeUnidades, LocalDate fechaDeVencimiento) {
    return nuevoItemSinFotos(FabricaDeBienes.unPaqueteDeArrozDe500GramosConVencimientoEn(fechaDeVencimiento), cantidadDeUnidades);
  }

  public static Item itemDeRemeraNuevaConNUnidades(int cantidadDeUnidades) {
    return nuevoItemSinFotos(FabricaDeBienes.remeraNueva(), cantidadDeUnidades);
  }

  public static Item nuevoItemSinFotos(Bien nuevoBien, int cantidadDeUnidades) {
    return nuevoItem(nuevoBien, cantidadDeUnidades, List.of());
  }
}
