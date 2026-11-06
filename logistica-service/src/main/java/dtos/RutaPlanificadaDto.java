package dtos;
import java.util.List;

public class RutaPlanificadaDto {
  private String patenteCamion;
  private List<DonacionDto> entregas;

  public RutaPlanificadaDto() {}

  public RutaPlanificadaDto(String patenteCamion, List<DonacionDto> entregas) {
    this.patenteCamion = patenteCamion;
    this.entregas = entregas;
  }

  public String getPatenteCamion() { return patenteCamion; }
  public List<DonacionDto> getEntregas() { return entregas; }
}
