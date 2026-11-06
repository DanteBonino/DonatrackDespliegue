package Necesidades;


import EntidadesBeneficiarias.ReciboDeDonacion;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("RECURRENTE")
public class NecesidadRecurrente extends Necesidad {
  @Column(name = "periodo")
  private Period periodo;
  @Column(name = "inicio_periodo_actual")
  private LocalDate inicioPeriodoActual = LocalDate.now();

  protected NecesidadRecurrente() {}
  public NecesidadRecurrente(CondicionDeAceptacion condicionDeAceptacion, BigDecimal cantidadRequerida, Period periodo){
    super(condicionDeAceptacion, cantidadRequerida);
    this.periodo = periodo;
  }

  private LocalDate fechaDeInicioDelPeriodo() {
    this.avanzarFechaDeInicioDelPeriodoHastaLaActual();
    return inicioPeriodoActual;
  }

  private void avanzarFechaDeInicioDelPeriodoHastaLaActual(){
    var diaActual = LocalDate.now();
    while (inicioPeriodoActual.plus(periodo).isBefore(diaActual) ||
        inicioPeriodoActual.plus(periodo).isEqual(diaActual)) {
      inicioPeriodoActual = inicioPeriodoActual.plus(periodo);
    }
  }

  public Period getPeriodo() {
    return this.periodo;
  }
  public void setPeriodo(Period nuevoPeriodo) {
    this.periodo = nuevoPeriodo;
  }

  @Override
  public TipoNecesidad getTipo() {
    return TipoNecesidad.RECURRENTE;
  }

  @Override
  protected boolean condicionDeActualidad(ReciboDeDonacion unRecibo) {
    return unRecibo.esPosteriorA(this.fechaDeInicioDelPeriodo());
  }
}
