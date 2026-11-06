package dtos;

public class EntregaDto {
  private String idDonacion;
  private String idEntidadBeneficiaria;
  private String direccion;

  public EntregaDto() {}

  public EntregaDto(String idDonacion, String idEntidadBeneficiaria, String direccion) {
    this.idDonacion = idDonacion;
    this.idEntidadBeneficiaria = idEntidadBeneficiaria;
    this.direccion = direccion;
  }

  public String getIdDonacion() {
    return idDonacion;
  }

  public String getIdEntidadBeneficiaria() {
    return  idEntidadBeneficiaria;
  }

  public String getDireccion() {
    return direccion;
  }
}
