package Necesidades;

import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import Eliminable.Eliminable;
import EntidadesBeneficiarias.ReciboDeDonacion;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Persistible.Persistible;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_necesidad")
@Table(name = "Necesidades")
abstract public class Necesidad extends Persistible implements Eliminable {
  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_condicion_de_aceptacion")
  private CondicionDeAceptacion condicionDeAceptacion;

  @Column(name = "cantidad_requerida")
  private BigDecimal cantidadRequerida;

  @ElementCollection
  @CollectionTable(name = "Recibos_De_Donacion", joinColumns = @JoinColumn(name = "id_necesidad"))
  protected Set<ReciboDeDonacion> bienesRecibidos = new HashSet<>();

  @Column(name = "esta_eliminado")
  private boolean estaEliminado = false;

  protected Necesidad(){}
  public Necesidad(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida){
    this.condicionDeAceptacion = condicionDeAceptacion;
    this.cantidadRequerida = cantidadRequerida;
  }

  public Boolean estaSatisfecha () {
    return this.cantidadRequerida().compareTo(this.cantidadRecibida()) <= 0;
  }

  public BigDecimal cantidadRecibida(){
    return this.bienesRecibidosActuales().stream()
        .map(ReciboDeDonacion::getCantidad)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal cantidadRequerida(){
    return this.cantidadRequerida;
  }

  public Boolean estaActiva() {
    return !this.estaSatisfecha();
  }

  public SubCategoria getSubCategoriaRequerida(){
    return condicionDeAceptacion.getSubCategoria();
  }

  public Set<ReciboDeDonacion> bienesRecibidosActuales() {
    return this.bienesRecibidos.stream()
        .filter(this::condicionDeActualidad)
        .collect(Collectors.toSet());
  }

  protected abstract boolean condicionDeActualidad(ReciboDeDonacion unBienRecibido);

  public void recibirBien(DonacionIndependiente unaDonacion){
    bienesRecibidos.add(new ReciboDeDonacion(unaDonacion));
  }

  public int cantidadDeDonacionesRecibidasDespuesDe(LocalDate unaFecha) {
    return Math.toIntExact(this.bienesRecibidos
        .stream()
        .filter(unBienRecibido -> unBienRecibido.esPosteriorA(unaFecha))
        .count());
  }

  public abstract TipoNecesidad getTipo();

  public static Necesidad crear(TipoNecesidad tipo, CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida,  Period periodo) {
    return switch (tipo) {
      case RECURRENTE -> new NecesidadRecurrenteBuilder()
          .conCondicionDeAceptacion(condicionDeAceptacion)
          .conPeriodo(periodo)
          .conCantidadRequerida(cantidadRequerida)
          .build();
      case EXTRAORDINARIA -> new NecesidadExtraordinariaBuilder()
          .conCondicionDeAceptacion(condicionDeAceptacion)
          .conCantidadRequerida(cantidadRequerida)
          .build();
    };
  }

  public boolean estaEliminado() {
    return this.estaEliminado;
  }

  public void marcarComoEliminado() {
    this.estaEliminado = true;
  }

  public CondicionDeAceptacion getCondicionDeAceptacion() {
    return this.condicionDeAceptacion;
  }

  public Boolean esCompatibleCon(DonacionIndependiente donacionIndependiente) {
    return condicionDeAceptacion.esAceptable(donacionIndependiente);
  }

  public Boolean puedeAceptarA(DonacionIndependiente donacionIndependiente) {
    return this.estaActiva() && esCompatibleCon(donacionIndependiente);
  }

}
