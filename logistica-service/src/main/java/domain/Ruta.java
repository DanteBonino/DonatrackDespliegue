package domain;

import domain.persistencia.EntidadBase;
import exceptions.CamionNoAsignadoIntentaAccionarSobreLaRutaException;
import exceptions.EstadoRutaInvalidoException;
import exceptions.RutaNoPuedeFinalizarseException;
import exceptions.RutaSinEntregasException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Representa una ruta de reparto dentro del sistema logistico.
 * Una ruta agrupa entregas, se asigna a un camion y mantiene su estado.
 *
 * <p>Convenciones:
 * - La fecha de reparto se expresa como {@link LocalDate}.
 * - El camion asignado debe estar disponible antes de iniciar la ruta.
 * - Las entregas deben estar en estado PLANIFICADA antes de iniciar la ruta.
 */
@Entity
@Table(name = "Rutas")
public class Ruta extends EntidadBase {

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "fecha_reparto")
  private LocalDate fechaDeReparto;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "ruta_asignada_id")
  private List<Entrega> entregas;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private EstadoRuta estado;

  protected Ruta() {
  }
  /**
   * Crea una nueva ruta en estado PLANIFICADA.
   *
   * @param id Identificador unico de la ruta.
   * @param fechaDeReparto Fecha programada para el reparto.
   */
  public Ruta(LocalDate fechaDeReparto) {
    this.nombre = "RUT-" + UUID.randomUUID() + "-" + fechaDeReparto;
    this.fechaDeReparto = fechaDeReparto;
    this.entregas = new ArrayList<>();
    this.estado = EstadoRuta.PLANIFICADA;
  }

  /**
   * Agrega una entrega a la ruta.
   *
   * @param entrega Entrega a agregar.
   * @throws IllegalStateException si la ruta no esta en estado PLANIFICADA.
   */
  public void agregarEntrega(Entrega entrega) {
    if (!estaPlanificada()) {
      throw new EstadoRutaInvalidoException("agregarEntrega", this.estado);
    }
    this.entregas.add(entrega);
  }

  /**
   * Inicia la ruta, cambiando su estado a EN_CURSO.
   * Todas las entregas pasan a estado EN_CAMINO.
   *
   * @throws IllegalStateException si la ruta no esta en estado PLANIFICADA.
   * @throws IllegalStateException si no hay camion asignado.
   * @throws IllegalStateException si no hay entregas asociadas.
   */
  public void darInicio(Camion camion) {
    validarQueSeaSuCamion(camion);
    if (!estaPlanificada()) {
      throw new EstadoRutaInvalidoException("darInicio", this.estado);
    }
    if (this.entregas.isEmpty()) {
      throw new RutaSinEntregasException(this.getNombre());
    }

    this.estado = EstadoRuta.EN_CURSO;
    this.entregas.forEach(Entrega::iniciarViaje);
  }

  public void finalizar(Camion camion) {
    validarQueSeaSuCamion(camion);
    validarQueEsteParaFinalizar();
    this.estado = EstadoRuta.FINALIZADA;
  }

  private void validarQueSeaSuCamion(Camion camion) {
    if(!camion.tieneAsignadaALaRuta(this)) throw new CamionNoAsignadoIntentaAccionarSobreLaRutaException("Solo el Camion que tiene asignada a la Ruta puede iniciarla o finalizara");
  }

  private void validarQueEsteParaFinalizar() {
    if(!estaParaFinalizar()) throw new RutaNoPuedeFinalizarseException("La Ruta no se puede finalizar porque sus entregas no están finalizadas en su totalidad o porque no está en curso");
  }

  public Boolean estaParaFinalizar() {
    return todasSusEntregasEstanFinalizadas() && estaEnCurso();
  }

  private Boolean todasSusEntregasEstanFinalizadas() {
    return this.entregas
        .stream()
        .allMatch(Entrega::estaFinalizada);
  }

  /**
   * Obtiene la siguiente entrega del listado de entregas.
   *
   * @return La primera entrega en estado EN_CAMINO, o null si no existe.
   */
  public Entrega getSiguienteEntrega() {
    return this.entregas.stream()
        .filter(e -> e.getEstado() == EstadoEntrega.EN_CAMINO)
        .findFirst()
        .orElse(null);
  }

  /// --- Getters ---
  public  LocalDate getFechaDeReparto() {
    return this.fechaDeReparto;
  }

  public List<Entrega> getEntregas() {
    return this.entregas;
  }

  public EstadoRuta getEstado() {
    return this.estado;
  }

  public String getNombre() {
    return this.nombre;
  }

  /**
   * Indica si la ruta esta finalizada.
   *
   * @return true si la ruta esta finalizada.
   */
  public Boolean estaFinalizada() {
    return estaEnEstado(EstadoRuta.FINALIZADA);
  }

  public Boolean estaEnCurso() {
    return estaEnEstado(EstadoRuta.EN_CURSO);
  }

  private Boolean estaEnEstado(EstadoRuta unEstado) {
    return this.estado == unEstado;
  }

  public Boolean estaAsignadaParaHoy() {
    return vaASerRecorridaHoy() || estaEnCurso();
  }

  private Boolean vaASerRecorridaHoy() {
    return estaPlanificada() && ocurreHoy();
  }

  public Boolean estaPlanificada() {
    return estaEnEstado(EstadoRuta.PLANIFICADA);
  }

  public Boolean ocurreHoy() {
    return fechaDeReparto.equals(LocalDate.now());
  }

  public int getTotalDeEntregas() {
    return entregas.size();
  }

  public int getTotalDeEntregasCompletadas() {
    return (int) entregas.stream()
        .filter(Entrega::estaCompletada)
        .count();
  }

  public Boolean seVencio() {
    return this.fechaDeReparto.isBefore(LocalDate.now());
  }

  public Boolean debeRecorrerseEn(LocalDate fechaDeComparacion) {
    return this.fechaDeReparto.equals(fechaDeComparacion);
  }

  public Boolean esLaMisma(Ruta otraRuta) {
    return this.nombre.equals(otraRuta.getNombre());
  }
}
