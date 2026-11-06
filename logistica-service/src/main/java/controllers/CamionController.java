package controllers;

import domain.Camion;
import domain.Posicion;
import dtos.CamionDashboardDto;
import dtos.CamionResponseDto;
import dtos.MensajeResponse;
import dtos.MonitoreoRutaDto;
import dtos.PosicionRequest;
import exceptions.CamionSinRutaActivaException;
import exceptions.EstadoRutaInvalidoException;
import exceptions.NoEstaEnCursoElCamionException;
import exceptions.RutaSinCamionAsignadoException;
import exceptions.RutaSinEntregasException;
import io.javalin.http.Context;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepositoryEnMemoria;
import java.util.List;


public class CamionController {

  // ========== HANDLERS (publicos para ser referenciados desde el enrutamiento) ==========

  /**
   * Endpoint para recibir las actualizaciones de geolocalización que envía
   * periódicamente la app del chofer.
   * <p>
   * Endpoint: POST /camiones/{patente}/posicion
   * </p>
   *
   * <p>Respuestas HTTP:</p>
   * <ul>
   *   <li>200: Posición actualizada correctamente.</li>
   *   <li>404: El camión no existe.</li>
   *   <li>409: El camión no tiene ruta activa.</li>
   *   <li>400: Datos inválidos en la posición.</li>
   * </ul>
   *
   * @param ctx contexto de Javalin que contiene:
   *            - {@code patente} (la patente del camión),
   *            - y el body {@link PosicionRequest} con latitud, longitud y velocidad.
   * @throws NotFoundResponse si no existe un camión con la patente indicada.
   */
  public void actualizarPosicion(Context ctx) {
    Camion camion = getCamionPorPatente(ctx);
    PosicionRequest req = ctx.bodyAsClass(PosicionRequest.class);
    Posicion nuevaPos = new Posicion(req.latitud(), req.longitud());
    camion.actualizarPosicion(nuevaPos);
    ctx.status(200).json(new MensajeResponse("Posición actualizada correctamente"));
  }

  /**
   * Endpoint para obtener todos los datos de un camión.
   * <p>
   * Endpoint: GET /camiones/{patente}/estado
   * </p>
   *
   * <p>Respuestas HTTP:</p>
   * <ul>
   *   <li>200: Devuelve un {@link CamionResponseDto} con la información del camión.</li>
   *   <li>404: El camión no existe.</li>
   * </ul>
   *
   * @param ctx Contexto de Javalin con la patente en el path.
   * @throws NotFoundResponse si no existe el camión con la patente indicada.
   */
  public void obtenerEstadoCamion(Context ctx) {
    Camion camion = getCamionPorPatente(ctx);

    CamionResponseDto dto = new CamionResponseDto(
        camion.getPatente(),
        camion.getPosicionActual().getLatitud(),
        camion.getPosicionActual().getLongitud(),
        camion.getEstadoRutaAsignada().name(),
        camion.getNombreRutaAsignada()
    );

    ctx.json(dto);
  }

  /**
   * Inicia la ruta activa de un camión identificado por su patente.
   * <p>
   * Endpoint: POST /camiones/{patente}/iniciar-ruta
   * </p>
   *
   * <p>Respuestas HTTP:</p>
   * <ul>
   *   <li>200: Ruta iniciada correctamente.</li>
   *   <li>404: El camión no existe.</li>
   *   <li>409: No tiene ruta activa o no está asignado.</li>
   *   <li>400: La ruta es inválida o no tiene entregas.</li>
   * </ul>
   *
   * @param ctx contexto de Javalin con la solicitud y la respuesta.
   */
  public void iniciarRuta(Context ctx) {
    Camion camion = getCamionPorPatente(ctx);
    camion.iniciarRuta();
    ctx.status(200).json(new MensajeResponse("Ruta iniciada correctamente. Entregas en camino"));
  }

  /**
   * Lista de todos los camiones de la flota.
   * Incluye posicion, estado y progreso
   *
   * <p>
   * Endpoint: GET /camiones
   * </p>
   *
   * @param ctx Contexto de Javalin.
   */
  public void obtenerTodosLosCamiones(Context ctx) {
    List<Camion> camiones = flotaRepositoryConPersistencia().obtenerTodos();

    List<CamionDashboardDto> respuesta = camiones.stream()
        .map(this::mapearADashboardDto)
        .toList();

    ctx.json(respuesta);
  }

  private CamionDashboardDto mapearADashboardDto(Camion camion) {
    return new CamionDashboardDto(
        camion.getPatente(),
        camion.getPosicionActual().getLatitud(),
        camion.getPosicionActual().getLongitud(),
        camion.getEstadoRutaAsignada().name(),
        camion.getNombreRutaAsignada(),
        camion.totalDeEntregasDeRutaAsignada(),
        camion.totalDeEntregasCompletadasDeLaRutaAsignada()
    );
  }

  /**
   * Endpoint: GET /camiones/{patente}/posicion
   *
   * Obtiene la ubicación actual (latitud, longitud y velocidad) de un camión específico
   * siempre y cuando se encuentre realizando una ruta activa.
   *
   * @param ctx el contexto de Javalin
   */
  public void obtenerPosicionCamion(Context ctx) {

    Camion camion = getCamionPorPatente(ctx);

    if(!camion.estaEnViaje()) {
      throw new NoEstaEnCursoElCamionException(camion.getPatente());
    }

    Posicion posicion = camion.getPosicionActual();

    MonitoreoRutaDto respuesta = new MonitoreoRutaDto(
        camion.getNombreRutaAsignada(),
        camion.getPatente(),
        posicion.getLatitud(),
        posicion.getLongitud(),
        camion.getEstadoRutaAsignada().name(),
        camion.totalDeEntregasDeRutaAsignada(),
        camion.totalDeEntregasCompletadasDeLaRutaAsignada()
    );

    ctx.json(respuesta);
  }

  private Camion getCamionPorPatente(Context ctx) {
    String patenteBuscada = ctx.pathParam("patente");
    return flotaRepositoryConPersistencia().buscarPorPatente(patenteBuscada);
  }

  private FlotaRepositoryConPersistencia flotaRepositoryConPersistencia() {
    return FlotaRepositoryConPersistencia.getInstance();
  }
}