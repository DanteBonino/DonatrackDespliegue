package utils;

public final class Entorno {
  private Entorno() {}

  public static String requerida(String nombre) {
    String valor = System.getenv(nombre);
    if (valor == null || valor.isBlank()) {
      throw new IllegalStateException("Falta la variable de entorno obligatoria: " + nombre);
    }
    return valor;
  }
}
