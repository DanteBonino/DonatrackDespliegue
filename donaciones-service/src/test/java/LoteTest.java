import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FechasUtils.elDiaSiguiente;
import static utils.FechasUtils.enUnaSemana;

import Donaciones.Item;
import Donaciones.Lote.Lote;
import Donaciones.Lote.LoteBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FabricaDeDatos;
import utils.FabricaDeItems;
import utils.FechasUtils;

public class LoteTest {
  private Lote lote;
  private List<Item> items;

  @Test
  void siSeTieneItemsAgrupablesCuandoSeCreaElLoteSeAgrupanEnUnaMismaDonacionIndependiente() {
    items = List.of(
        FabricaDeItems.itemDePaqueteDeArroz500GramosConNUnidadesYFechaDeVencimiento(
            2, enUnaSemana()
        ),
        FabricaDeItems.itemDePaqueteDeArroz500GramosConNUnidadesYFechaDeVencimiento(
            4, enUnaSemana()
        ),
        FabricaDeItems.itemDePaqueteDeArroz500GramosConNUnidadesYFechaDeVencimiento(
            5, elDiaSiguiente()
        ),
        FabricaDeItems.itemDeRemeraNuevaConNUnidades(50)
    );
    lote = new LoteBuilder()
        .conDonante(FabricaDeDatos.unDonanteDePrueba())
        .conItems(items)
        .build();
    assertEquals(3, lote.getDonaciones().size());
    assertTrue(lote.getDonaciones().stream().allMatch(unaDonacion -> unaDonacion.getDonante().equals(lote.getDonante())));
  }

}
