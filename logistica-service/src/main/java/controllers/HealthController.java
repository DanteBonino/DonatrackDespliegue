package controllers;

import dtos.HealthResponse;
import io.javalin.http.Context;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class HealthController {

  private final Instant inicioServidor;
  private final String nombreServicio;

  public HealthController(String nombreServicio) {
    this.inicioServidor = Instant.now();
    this.nombreServicio = nombreServicio;
  }

  /**
   * Endpoint: GET /health
   * Verifica el estado actual del servidor y devuelve una respuesta
   * con información básica de salud del sistema.
   */
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

    ctx.json(new HealthResponse("UP",
        timestamp,
        uptimeFormateado,
        this.nombreServicio)
    );
  }
}
