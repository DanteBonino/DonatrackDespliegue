package Donaciones.Lote;

import Donaciones.DonacionIndependiente;
import Donaciones.Item;
import Donantes.Donante;
import EstadoDonacion.EstadoDonacion;
import ExceptionHandler.CantidadDeItemsInvalida;
import ObserversCambioDeEstado.PublicadorDeCambiosEstados;
import Validaciones.Validaciones;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoteBuilder {
  private List<Item> items;
  private Donante duenio;
  private PublicadorDeCambiosEstados publicadorDeCambiosEstados;

  public LoteBuilder conDonante(Donante donante) {
    this.duenio = donante;
    return this;
  }

  public LoteBuilder conItems(List<Item> items) {
    this.items = items;
    return this;
  }

  public LoteBuilder conPublicadorParaLasDonaciones(PublicadorDeCambiosEstados publicadorDeCambiosEstados) {
    this.publicadorDeCambiosEstados = publicadorDeCambiosEstados;
    return this;
  }

  public Lote build() {
    Validaciones.requerido(duenio, "donante");
    Validaciones.requerido(items, "items");
    if(items.isEmpty()) throw new CantidadDeItemsInvalida();
    if(publicadorDeCambiosEstados == null) publicadorDeCambiosEstados = new PublicadorDeCambiosEstados();
    return new Lote(duenio, donacionesDeItems());
  }

  private Set<DonacionIndependiente> donacionesDeItems() {
    return this.itemsAgrupados()
        .stream()
        .map(item -> new DonacionIndependiente(item, EstadoDonacion.EN_DEPOSITO, duenio, publicadorDeCambiosEstados))
        .collect(Collectors.toSet());
  }

  //Esto probablemente se podría delegar en otra clase
  private Set<Item> itemsAgrupados() {
    Set<Item> itemsAgrupados = new HashSet<Item>();
    for (Item item : items) {
      itemsAgrupados = agregarAgrupando(itemsAgrupados, item);
    }
    return itemsAgrupados;
  }

  private Set<Item> agregarAgrupando(Set<Item> agrupados, Item item) {
    if(agrupados.stream().anyMatch(item::esAgrupableCon)) {
      return agrupados.stream()
          .map(existente -> existente.combinacionCon(item))
          .collect(Collectors.toSet());
    }
    return Stream.concat(agrupados.stream(), Stream.of(item)).collect(Collectors.toSet());
  }

}
