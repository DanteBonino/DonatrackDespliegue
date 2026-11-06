package Controllers.Donanciones.Get;

import Controllers.DTOsSinController.Bien.BienDto;
import Controllers.DTOsSinController.ItemDto;
import Controllers.Donantes.DonanteResponseDto;
import Controllers.EntidadesBenefeciarias.EntidadBeneficiariaResponseDto;
import Donaciones.DonacionIndependiente;
import EstadoDonacion.EstadoDonacion;
import java.time.LocalDateTime;

public class DonacionResponseDto {
  private ItemDto item;
  private LocalDateTime fecha;
  private DonanteResponseDto donante;
  private String             id;
  private EstadoDonacion estadoActual;
  private EntidadBeneficiariaResponseDto asignado;

  public static DonacionResponseDto desde(DonacionIndependiente donacion) {
    DonacionResponseDto dto = new DonacionResponseDto();
    dto.id           = donacion.getIdDominio();
    dto.item         = ItemDto.desde(donacion.getItem());
    dto.fecha        = donacion.getFechaDeDonacion();
    dto.donante      = DonanteResponseDto.desde(donacion.getDonante());
    dto.estadoActual = donacion.getEstado();
    dto.asignado     = EntidadBeneficiariaResponseDto.desde(donacion.getAsignado());
    return dto;
  }

  public String getId() { return id; }
  public LocalDateTime getFecha() { return fecha; }
  public DonanteResponseDto getDonante() { return donante; }
  public ItemDto getItem() { return item; }
  public EstadoDonacion getEstadoActual() { return estadoActual; }
  public EntidadBeneficiariaResponseDto getAsignado() { return asignado; }

}
