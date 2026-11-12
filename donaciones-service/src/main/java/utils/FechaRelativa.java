package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class FechaRelativa {
  private static final DateTimeFormatter ABSOLUTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public static String texto(LocalDate fecha, LocalDate hoy) {
    long dias = ChronoUnit.DAYS.between(fecha, hoy);
    if (dias < 0) return fecha.format(ABSOLUTA);
    if (dias == 0) return "hoy";
    if (dias == 1) return "ayer";
    if (dias < 7) return "hace " + dias + " días";
    if (dias < 30) {
      long semanas = dias / 7;
      return "hace " + semanas + (semanas == 1 ? " semana" : " semanas");
    }
    return fecha.format(ABSOLUTA);
  }

  public static String absoluta(LocalDate fecha) {
    return fecha.format(ABSOLUTA);
  }
}