package ExceptionHandler;

public class NoSeEncontroEnElRepositoryException extends RuntimeException {
  public NoSeEncontroEnElRepositoryException(String id) {
    super("No se encontro en el repo" + id);
  }
}
