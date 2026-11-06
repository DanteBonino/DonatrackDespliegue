package dtos;

import java.util.List;

public class SolicitudPlanificacionRequest {
  private List<DonacionDto> donaciones;
  private List<CamionDto> camiones;

  public SolicitudPlanificacionRequest(List<DonacionDto> donaciones, List<CamionDto> camiones) {
    this.donaciones = donaciones;
    this.camiones = camiones;
  }

  public List<DonacionDto> getDonaciones() {return this.donaciones;}
  public List<CamionDto> getCamiones() {return this.camiones;}
}
