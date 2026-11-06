package ExceptionHandler;

public class NoHayNecesidadCorrespondiente extends RuntimeException {
  public NoHayNecesidadCorrespondiente(String message) {
    super(message);
  }
  public NoHayNecesidadCorrespondiente() {
    this("La entidad beneficiaria no cuenta con una necesidad que se corresponda con la donacion en cuestión");
  }
}
