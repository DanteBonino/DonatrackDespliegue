package dtos;

import java.util.List;

public class PlanificacionResponse {
  private List<RutaPlanificadaDto> rutas;
  private List<String> donacionesSinAsignar; //devuelve los IDs de las no asignadas

  public PlanificacionResponse() {}

  public PlanificacionResponse(List<RutaPlanificadaDto> rutas, List<String> donacionesSinAsignar) {
    this.rutas = rutas;
    this.donacionesSinAsignar = donacionesSinAsignar;
  }

  public List<RutaPlanificadaDto> getRutas() { return rutas; }
  public List<String> getDonacionesSinAsignar() { return donacionesSinAsignar; }
}
