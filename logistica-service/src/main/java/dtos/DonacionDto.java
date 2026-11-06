package dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record DonacionDto(
    String idDonacion,
    String idEntidadBeneficiaria,
    DireccionDto direccionDestino
) {
  public record DireccionDto(CalleDto calle, PosicionDto posicion) {}
  public record CalleDto(String nombre, int altura) {}
  public record PosicionDto(double longitud, double latitud) {}

  public PosicionDto posicion() {
    return direccionDestino.posicion;
  }

  public double longitud() {
    return posicion().longitud();
  }

  public double latitud() {
    return posicion().latitud;
  }

  public CalleDto calle() {
    return direccionDestino.calle;
  }

  public String nombreCalle() {
    return calle().nombre;
  }

  public int altura() {
    return calle().altura();
  }
}