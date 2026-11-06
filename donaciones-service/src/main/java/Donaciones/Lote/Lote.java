package Donaciones.Lote;

import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Persistible.Persistible;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Lotes")
public class Lote extends Persistible {
  @ManyToOne
  @JoinColumn(name = "id_donante")
  private Donante donante;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_lote_")
  private Set<DonacionIndependiente> donaciones;

  protected Lote() {

  }

  public Lote(Donante donante, Set<DonacionIndependiente> donaciones) {
    this.donante = donante;
    this.donaciones = donaciones;
  }

  public Donante getDonante() {
    return this.donante;
  }

  public Set<DonacionIndependiente> getDonaciones() {
    return Set.copyOf(donaciones);
  }

}
