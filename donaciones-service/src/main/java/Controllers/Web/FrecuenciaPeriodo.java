package Controllers.Web;

import java.time.Period;

public enum FrecuenciaPeriodo {
  SEMANAL(Period.ofWeeks(1)),
  MENSUAL(Period.ofMonths(1)),
  BIMESTRAL(Period.ofMonths(2)),
  TRIMESTRAL(Period.ofMonths(3));

  private final Period periodJava;

  FrecuenciaPeriodo(Period periodJava) {
    this.periodJava = periodJava;
  }

  public Period getPeriodJava() {
    return periodJava;
  }
}
