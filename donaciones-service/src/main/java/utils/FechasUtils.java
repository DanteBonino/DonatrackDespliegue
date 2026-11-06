package utils;

import java.time.LocalDate;

public class FechasUtils {
  public static LocalDate hoy() {
    return LocalDate.now();
  }

  public static LocalDate enUnaSemana() {
    return enNDias(7);
  }

  public static LocalDate enUnMes() {
    return enNDias(31);
  }

  public static LocalDate ayer() {
    return haceNDias(1);
  }

  public static LocalDate elDiaSiguiente() {
    return enNDias(1);
  }

  public static LocalDate enNDias(int dias) {
    return hoy().plusDays(dias);
  }

  public static LocalDate haceNDias(int dias) {
    return hoy().minusDays(dias);
  }

  public static Boolean esAnteriorA(LocalDate fechaDeInteres, LocalDate fechaLimite) {
    return fechaDeInteres.isBefore(fechaLimite);
  }

  public static LocalDate haceQuinceAnios() {
    return haceNDias(5475);
  }

}
