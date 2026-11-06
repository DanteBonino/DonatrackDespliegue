package ExceptionHandler;

public class NoSePudoLeerElArchivo extends RuntimeException {
  public NoSePudoLeerElArchivo(String unPath) {
    super("No se pudo leer el archivo de la ruta: " + unPath);
  }
}
