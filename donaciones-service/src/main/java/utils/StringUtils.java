package utils;

public class StringUtils {
  public static String nombreAptoBaseDeDatos(String unNombre) {
    return unNombre.trim().toUpperCase();
  }

  public static String sacarGuionesPuntosYEspacios(String unString) {
    return unString.replaceAll("[\\s.-]", "");
  }
}
