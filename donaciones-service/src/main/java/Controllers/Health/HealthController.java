package Controllers.Health;

import java.time.*;

import io.javalin.http.Context;

import java.time.format.DateTimeFormatter;

public class HealthController {
  private final Instant inicioServidor;
  private final String nombreServicio;

  public HealthController(String nombreServicio) {
    this.inicioServidor = Instant.now();
    this.nombreServicio = nombreServicio;
  }

  public void check(Context ctx) {
  Duration uptime = Duration.between(inicioServidor, Instant.now());
  String timestamp = Instant.now()
      .atZone(ZoneOffset.UTC)
      .format(DateTimeFormatter.ISO_INSTANT);

  String uptimeFormateado = String.format("%02d:%02d:%02d",
      uptime.toHours(),
      uptime.toMinutesPart(),
      uptime.toSecondsPart()
  );

  ctx.json(new HealthResponseDto("UP", timestamp, uptimeFormateado, this.nombreServicio));
  }
}
