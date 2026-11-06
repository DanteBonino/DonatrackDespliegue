package ExceptionHandler;

public class EntidadNoEsCandidata extends RuntimeException {
  public EntidadNoEsCandidata() {
    super("La entidad seleccionada no es candidata para recibir la donación en cuestión");
  }
}
