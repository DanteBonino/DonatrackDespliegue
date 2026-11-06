package domain;

import domain.persistencia.EntidadBase;
import exceptions.CamionNoDisponibleException;
import exceptions.CamionSinRutaActivaException;
import exceptions.RutaInvalidaParaAsignarException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Representa un camion dentro del sistema logistico.
 * Incluye informacion de identificacion, dimensiones fisicas,
 * capacidad de carga y estado de ruta/posicion.
 *
 * @param patente Identificador único del vehículo (ej. "ABC123").
 * @param volumen Volumen útil de carga en metros cúbicos (m³).
 * @param altura Altura máxima del camión en metros (m).
 * @param capacidad Capacidad máxima de carga en kilogramos (kg).
 */

@Entity
@Table(name = "Camiones")
public class Camion extends EntidadBase {
  @Column(name = "patente")
  private String patente;
  @Column(name = "volumen")
  private double volumen;
  @Column(name = "altura")
  private double altura;
  @Column(name = "capacidad")
  private double capacidad;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "latitud", column = @Column(name = "posicion_latitud")),
      @AttributeOverride(name = "longitud", column = @Column(name = "posicion_longitud"))
  })
  private Posicion posicionActual;

  @OneToMany
  @JoinColumn(name = "camion_asignado_id")
  private Set<Ruta> rutasAsignadas = new HashSet<>();

  // private Chofer chofer; // fuera de alcance por ahora, quizas

  /**
   * Crea un nuevo camion con sus caracteristicas fisicas.
   *
   * @param patente Identificador unico del vehiculo.
   * @param volumen Volumen util de carga en m3.
   * @param altura Altura en metros.
   * @param capacidad Capacidad maxima en kg.
   */
  protected  Camion() {
  }

  public Camion(String patente, double volumen, double altura, double capacidad, Posicion posicionInicial) {
    this.patente = patente;
    this.volumen = volumen;
    this.altura = altura;
    this.capacidad = capacidad;

    this.posicionActual = posicionInicial;
  }

  /**
   * Indica si el camion esta disponible en la fecha actual.
   *
   * @return true si de las rutas que le fueron asignadas, no hay ninguna en curso o que vaya a ser
   * recorrida hoy.
   */
  public Boolean estaDisponible() {
    return !tieneRutaAsignada();
  }

  public Boolean tieneRutaAsignada() {
    return rutaAsignada().isPresent();
  }

  public Optional<Ruta> rutaAsignada() {
    return findRutaSegun(Ruta::estaAsignadaParaHoy);
  }

  private Optional<Ruta> findRutaSegun(Predicate<Ruta> condicion) {
    return this.rutasAsignadas
        .stream()
        .filter(condicion)
        .findAny();
  }

  public EstadoRuta getEstadoRutaAsignada() {
    return rutaAsignada().map(Ruta::getEstado).orElse(EstadoRuta.DISPONIBLE);
  }

  public String getNombreRutaAsignada() {
    return rutaAsignada().map(Ruta::getNombre).orElse(null);
  }

  public void iniciarRuta() {
    rutaAsignada().orElseThrow(() -> new CamionSinRutaActivaException(patente)).darInicio(this);
  }

  public int totalDeEntregasDeRutaAsignada() {
    return rutaAsignada().map(Ruta::getTotalDeEntregas).orElse(0);
  }

  public int totalDeEntregasCompletadasDeLaRutaAsignada() {
    return rutaAsignada().map(Ruta::getTotalDeEntregasCompletadas).orElse(0);
  }

  public void asignarRuta(Ruta nuevaRuta) {
    validarQueNoTengaRutaAsignadaParaLaFecha(nuevaRuta.getFechaDeReparto());
    validarQueLaRutaEstePlanificada(nuevaRuta);
    validarQueLaRutaTengaUnaFechaAcorde(nuevaRuta);
    rutasAsignadas.add(nuevaRuta);
  }

  private Boolean tieneRutaAsignadaParaLaFecha(LocalDate unaFecha) {
    return rutasAsignadas.stream().anyMatch(ruta -> ruta.debeRecorrerseEn(unaFecha));
  }

  private void validarQueNoTengaRutaAsignadaParaLaFecha(LocalDate unaFecha) {
    if(tieneRutaAsignadaParaLaFecha(unaFecha)) throw new CamionNoDisponibleException(getPatente(), unaFecha);
  }

  private void validarQueLaRutaEstePlanificada(Ruta unaRuta) {
    if(!unaRuta.estaPlanificada()) throw new RutaInvalidaParaAsignarException("Las Rutas sólo se pueden asignar en estado PLANIFICADA");
  }

  private void validarQueLaRutaTengaUnaFechaAcorde(Ruta unaRuta) {
    if(unaRuta.seVencio()) throw new RutaInvalidaParaAsignarException("Las Rutas sólo pueden asignarse si todavía no ocurrió su fecha de reparto");
  }

  public void finalizarRuta() {
    rutaAsignada().orElseThrow(() -> new CamionSinRutaActivaException(patente)).finalizar(this);
  }

  public Boolean tieneAsignadaALaRuta(Ruta unaRuta) {
    return rutaAsignada()
        .map(ruta -> ruta.esLaMisma(unaRuta))
        .orElse(false);
  }

  public Boolean estaEnViaje() {
    return !estaDisponible() && rutaAsignada().get().estaEnCurso();
  }

  /**
   * Actualiza la posicion del camion.
   *
   * @param posicion Nueva posicion geografica.
   * @throws IllegalStateException si el camion no tiene una ruta activa.
   */
  public void actualizarPosicion(Posicion posicion) {
    if (this.estaDisponible()) {
      throw new CamionSinRutaActivaException(this.patente);
    }
    this.posicionActual = posicion;
  }

  // --- Getters ---
  public String getPatente() {
    return patente;
  }

  public double getVolumen() {
    return volumen;
  }

  public double getAltura() {
    return altura;
  }

  public double getCapacidad() {
    return capacidad;
  }

  public Posicion getPosicionActual() {
    return posicionActual;
  }

}
