package Controllers.Necesidades;

import Controllers.DTOsSinController.CondicionDeAceptacion.CondicionDeAceptacionDto;
import Necesidades.Necesidad;
import Necesidades.NecesidadRecurrente;
import Necesidades.TipoNecesidad;
import java.math.BigDecimal;
import java.time.Period;

public class NecesidadResponseDto {
  private String id;
  private TipoNecesidad tipo;
  private BigDecimal cantidadRecibida;
  private Boolean estaSatisfecha;
  private Period periodo; // null salvo RECURRENTE
  private CondicionDeAceptacionDto condicionDeAceptacionDto;
  private BigDecimal cantidadRequerida;

  public static NecesidadResponseDto desde(Necesidad unaNecesidad) {
    NecesidadResponseDto response = new NecesidadResponseDto();
    response.id = unaNecesidad.getIdDominio();
    response.tipo = unaNecesidad.getTipo();
    response.cantidadRecibida = unaNecesidad.cantidadRecibida();
    response.estaSatisfecha = unaNecesidad.estaSatisfecha();
    response.condicionDeAceptacionDto = CondicionDeAceptacionDto.desde(unaNecesidad.getCondicionDeAceptacion());
    response.cantidadRequerida = unaNecesidad.cantidadRequerida();
    if (unaNecesidad instanceof NecesidadRecurrente necesidadRecurrente) {
      response.periodo = necesidadRecurrente.getPeriodo();
    }
    return response;
  }

  public Period getPeriodo() {
    return periodo;
  }

  public BigDecimal getCantidadRecibida() {
    return cantidadRecibida;
  }

  public Boolean getEstaSatisfecha() {
    return estaSatisfecha;
  }

  public String getId() {
    return id;
  }

  public TipoNecesidad getTipo() {
    return tipo;
  }

}
