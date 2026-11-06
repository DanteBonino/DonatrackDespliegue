package Controllers.Donanciones.HistorialDeCambiosDeEstado;

import Donaciones.CambioDeEstado;
import EstadoDonacion.EstadoDonacion;
import java.time.LocalDateTime;

public class CambioDeEstadoResponseDto {
  private LocalDateTime fecha;
  private EstadoDonacion tipo;
  private String idEntidadAsignada;

  public static CambioDeEstadoResponseDto desde (CambioDeEstado unCambio) {
    CambioDeEstadoResponseDto dto = new CambioDeEstadoResponseDto();
    dto.fecha = unCambio.getFecha();
    dto.tipo = unCambio.getEstadoDonacion();
    dto.idEntidadAsignada = unCambio.getAsignado() == null ? null : unCambio.getAsignado().getIdDominio();
    return dto;
  }

  public EstadoDonacion getTipo() { return tipo; }
  public LocalDateTime getFecha() { return fecha; }
  public String getIdEntidadAsignada() { return idEntidadAsignada; }
}
