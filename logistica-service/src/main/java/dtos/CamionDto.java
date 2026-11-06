package dtos;

public class CamionDto {
  private String patente;
  private double volumen;
  private double altura;
  private double capacidad;

  public CamionDto(String patente, double volumen, double altura, double capacidad) {
    this.patente = patente;
    this.volumen = volumen;
    this.altura = altura;
    this.capacidad = capacidad;
  }

  public String getPatente() {
    return patente;
  }

  public double getVolumen() {
    return volumen;
  }

  public double getAltura() {
    return altura;
  }

  public double getCapacidad() {
    return capacidad;
  }
}
