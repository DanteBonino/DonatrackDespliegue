package controllers.utils;

import builders.CalleBuilder;
import builders.DireccionBuilder;
import builders.PosicionBuilder;
import domain.Camion;
import domain.Entrega;
import domain.Posicion;
import domain.Ruta;
import domain.eventos.EntregaPublicador;
import dtos.EntregaDto;
import dtos.PlanificacionResponse;
import dtos.RutaPlanificadaDto;
import exceptions.CamionNoDisponibleException;
import externo.donaciones.DonacionesHttpAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import repositories.FlotaRepository.FlotaRepository;
import repositories.FlotaRepository.FlotaRepositoryConPersistencia;
import repositories.RutasRepository.RutasRepository;
import repositories.RutasRepository.RutasRepositoryConPersistencia;

public class AsignadorDePlanificacion {
  private EntregaPublicador publicador;

  public AsignadorDePlanificacion(EntregaPublicador publicadorBase) {
    this.publicador = publicadorBase;
  }

  public List<Ruta> asignarRutas(PlanificacionResponse planificacion, LocalDate fechaDeReparto) {
    List<Ruta> rutas = planificacion.getRutas().stream()
        .map(rutaDto -> crearRutaAsignada(rutaDto, fechaDeReparto))
        .toList();

    rutas.forEach(RutasRepositoryConPersistencia.getInstance()::registrar);
    return rutas;
  }

  private Ruta crearRutaAsignada(RutaPlanificadaDto rutaDto, LocalDate fechaDeReparto) {
    Camion camion = FlotaRepositoryConPersistencia.getInstance().buscarPorPatente(rutaDto.getPatenteCamion());
    Ruta ruta = new Ruta(fechaDeReparto);

    rutaDto.getEntregas().forEach(entregaDto ->
        ruta.agregarEntrega(
            new Entrega(
                entregaDto.idDonacion(),
                entregaDto.idEntidadBeneficiaria(),
                new DireccionBuilder()
                    .conCalle(
                        new CalleBuilder()
                            .conNombre(entregaDto.nombreCalle())
                            .conAltura(entregaDto.altura())
                            .build()
                    )
                    .conPosicion(
                        new PosicionBuilder()
                            .conLongitud(entregaDto.longitud())
                            .conLatitud(entregaDto.latitud())
                            .build()
                    )
                    .build()
                ,publicador)));

    camion.asignarRuta(ruta);
    return ruta;
  }

}
