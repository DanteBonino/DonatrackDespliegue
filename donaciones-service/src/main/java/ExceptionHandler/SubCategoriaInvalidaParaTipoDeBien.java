package ExceptionHandler;

import Controllers.DTOsSinController.Bien.TipoBien;

public class SubCategoriaInvalidaParaTipoDeBien extends RuntimeException {
  public SubCategoriaInvalidaParaTipoDeBien(String nombreSubCategoria, TipoBien tipoBien) {
    super("La SubCategoria: " + nombreSubCategoria + " no es " + tipoBien);
  }
}
