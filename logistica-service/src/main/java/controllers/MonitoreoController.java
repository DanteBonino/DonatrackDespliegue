package controllers;

import domain.Camion;
import domain.Posicion;
import dtos.MensajeResponse;
import dtos.MonitoreoRutaDto;
import exceptions.NoEstaEnCursoElCamionException;
import io.javalin.http.Context;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepositoryEnMemoria;
import java.util.List;

public class MonitoreoController {
  /**
   * Endpoint: GET /rutas/activas
   *
   * Obtiene todas las rutas que actualmente estan en curso y construye
   * una lista de objetos {@link MonitoreoRutaDto} con la informacion
   * relevante para el monitoreo (p.ej. auditoria o un dashboard).
   *
   * <p>Para cada ruta en curso se incluyen:
   * <ul>
   *   <li>ID de la ruta</li>
   *   <li>Patente del camion asignado</li>
   *   <li>Posicion actual del camion (latitud, longitud, velocidad)</li>
   *   <li>Estado de la ruta</li>
   *   <li>Total de entregas planificadas</li>
   *   <liCantidad de entregas completadas</li>
   * </ul>
   *
   * <p>La respuesta se serializa en formato JSON y se envia al cliente.
   *
   * @param ctx el contexto de Javalin utilizado para construir la respuesta HTTP
   * @throws IllegalStateException si alguna ruta no tiene camion asignado
   *                               o presenta inconsistencias en sus entregas
   */
  public void obtenerRutasActivas(Context ctx) {
    List<Camion> camionesEnCurso = flotaRepositoryConPersistencia().obtenerEnCurso();

    List<MonitoreoRutaDto> respuesta = camionesEnCurso.stream()
        .map(camion -> new MonitoreoRutaDto(
            camion.getNombreRutaAsignada(),
            camion.getPatente(),
            camion.getPosicionActual().getLatitud(),
            camion.getPosicionActual().getLongitud(),
            camion.getEstadoRutaAsignada().name(),
            camion.totalDeEntregasDeRutaAsignada(),
            camion.totalDeEntregasCompletadasDeLaRutaAsignada()
        ))
        .toList();

    ctx.json(respuesta);
  }

  private FlotaRepositoryConPersistencia flotaRepositoryConPersistencia() {
    return FlotaRepositoryConPersistencia.getInstance();
  }

}