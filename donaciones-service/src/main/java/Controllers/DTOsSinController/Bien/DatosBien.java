package Controllers.DTOsSinController.Bien;

public interface DatosBien <T> {
  String getDescripcion();

  String getFoto();

  Integer getCantidad();

  T getSubCategoria();
}
