package Donaciones;

import Bienes.Bien;
import Bienes.Categoria.Unidad;
import Bienes.SubCategoria.SubCategoria;
import Persistible.Persistible;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name = "ItemsDonacion")
public class Item extends Persistible {
  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_bien")
  private Bien bien;
  @Column(name = "cantidad_de_unidades")
  private int cantidadDeUnidades;
  @ElementCollection
  @Column(name = "foto")
  @CollectionTable(name = "FotosDeItems", joinColumns = @JoinColumn(name = "id_item"))
  @OrderColumn(name = "orden")
  private List<String> fotos;

  protected Item() {}
  public Item(Bien bien, int cantidad, List<String> fotos) {
    this.bien = bien;
    this.cantidadDeUnidades = cantidad;
    this.fotos = fotos;
  }

  public Bien getBien() {
    return this.bien;
  }

  public int getCantidadDeUnidades() {
    return this.cantidadDeUnidades;
  }

  public BigDecimal getCantidadDeSatisfaccion() {
    return this.getBien().cantidadSatisfechaSegunCantidadDeBien(this.getCantidadDeUnidades());
  }

  public SubCategoria getSubCategoria() {
    return getBien().getSubCategoria();
  }

  public Boolean esAgrupableCon(Item otroItem) {
    return this.getBien().esAgrupableCon(otroItem.getBien());
  }

  public Item combinacionCon(Item otroItem) {
    return this.esAgrupableCon(otroItem) ? new Item(this.getBien(), this.getCantidadDeUnidades() + otroItem.getCantidadDeUnidades(), Stream.concat(this.getFotos().stream(), otroItem.getFotos().stream()).toList()) : this;
  }

  public List<String> getFotos() {
    return List.copyOf(fotos);
  }

  public Unidad getUnidad() {
    return getSubCategoria().getUnidad();
  }
}
