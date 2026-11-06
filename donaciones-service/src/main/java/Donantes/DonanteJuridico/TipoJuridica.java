package Donantes.DonanteJuridico;

public enum TipoJuridica {
  GUBERNAMENTAL("Gubernamental"),
  ONG("ONG"),
  EMPRESA("Empresa"),
  INSTITUCION("Institución");

  private final String descripcion;

  TipoJuridica(String descripcion) { this.descripcion = descripcion; }

  public String getDescripcion() { return descripcion; }
  public String getCodigo() { return name(); }
}