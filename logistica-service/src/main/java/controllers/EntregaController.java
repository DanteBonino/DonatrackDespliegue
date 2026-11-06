package controllers;

import controllers.utils.AlmacenamientoDeComprobantes;
import controllers.utils.GeneradorDeUrlDeTracking;
import domain.*;
import dtos.MensajeResponse;
import dtos.ReportarIncidenciaRequest;
import exceptions.EstadoEntregaInvalidoException;
import io.javalin.http.Context;
import repositories.EntregasRepository.EntregasRepository;
import repositories.EntregasRepository.EntregasRepositoryConPersistencia;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepository;
import io.javalin.http.UploadedFile;
import repositories.RutasRepository.RutasRepositoryConPersistencia;

public class EntregaController {
  private final GeneradorDeUrlDeTracking generadorDeUrlDeTracking;
  private final AlmacenamientoDeComprobantes almacenamientoDeComprobantes;

  public EntregaController(
      AlmacenamientoDeComprobantes almacenamientoDeComprobantes,
      GeneradorDeUrlDeTracking generadorDeUrlDeTracking
  ) {
    this.generadorDeUrlDeTracking = generadorDeUrlDeTracking;
    this.almacenamientoDeComprobantes = almacenamientoDeComprobantes;
  }

  /**
   * Endpoint: POST /entregas/{nombreEntrega}/confirmar
   */
  public void confirmarRecepcion(Context ctx) {
    Entrega entrega = getEntregaPorNombre(ctx);
    if (!entrega.estaEnCamino()) {
      throw new EstadoEntregaInvalidoException("confirmar", entrega.getEstado());
    }
    UploadedFile archivo = ctx.uploadedFile("foto");
    if (archivo == null) {
      throw new IllegalArgumentException("Debe adjuntar una foto");
    }
    Ruta ruta = rutasRepository().buscarRutaPorEntregaId(entrega.getId());
    Camion camionDeRuta = flotaRepositoryConPersistencia().buscarPorRuta(ruta);
    String patente = camionDeRuta.getPatente();

    String comprobantePath = almacenamientoDeComprobantes.almacenarComprobante(
        archivo,
        entrega.getId(),
        entrega.getIdDonacion(),
        patente
    );

    entrega.confirmarRecepcion(comprobantePath, patente);
    ctx.status(200).json(new MensajeResponse("Recepción confirmada con éxito, foto guardada"));
  }

  /**
   * Endpoint: POST /entregas/{idEntrega}/reportar-incidencia
   */
  public void reportarIncidencia(Context ctx) {
    Entrega entrega = getEntregaPorNombre(ctx);
    ReportarIncidenciaRequest req = ctx.bodyAsClass(ReportarIncidenciaRequest.class);
    entrega.reportarIncidencia(req.motivo());
    ctx.status(200).json(new MensajeResponse("Incidencia reportada con éxito"));
  }

  /**
   * Endpoint: POST /entregas/{idEntrega}/reprogramar
   */
  public void reprogramar(Context ctx) {
    Entrega entrega = getEntregaPorNombre(ctx);
    entrega.reprogramar();
    ctx.status(200).json(new MensajeResponse("Entrega reprogramada con éxito. Vuelve a estado PLANIFICADA"));
  }

  // Redirigir a Tracking
  public void redirigirATracking(Context ctx) {
    Entrega entrega = getEntregaPorNombre(ctx);
    Ruta ruta = rutasRepository().buscarRutaPorEntregaId(entrega.getId());
    Camion camionAsignadoA = flotaRepositoryConPersistencia().buscarPorRuta(ruta);
    ctx.redirect(generadorDeUrlDeTracking.urlTracking(camionAsignadoA.getPosicionActual()));
  }

  private Entrega getEntregaPorNombre(Context ctx) {
    String nombreEntrega = ctx.pathParamAsClass("nombreEntrega", String.class).get();
    return EntregasRepositoryConPersistencia.getInstance().obtenerPorNombre(nombreEntrega);
  }

  private FlotaRepositoryConPersistencia flotaRepositoryConPersistencia() {
    return FlotaRepositoryConPersistencia.getInstance();
  }

  private RutasRepositoryConPersistencia rutasRepository() {
    return RutasRepositoryConPersistencia.getInstance();
  }
}