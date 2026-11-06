package Controllers.Donanciones.Update;

import Bienes.Bien;
import Controllers.DTOsSinController.Bien.BienDto;

public class DatosDonacionUpdateRequest implements DatosDonacion {
  private BienDto bien;

  public DatosDonacionUpdateRequest (BienDto bien) {
    this.bien = bien;
  }
  public DatosDonacionUpdateRequest(){}

  public Bien getBien() {
   return this.bien == null? null : bien.crearInstancia();
  }
}
