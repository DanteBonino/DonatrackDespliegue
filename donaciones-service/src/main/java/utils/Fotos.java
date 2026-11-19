package utils;

public final class Fotos {
  private static final FotoStorage INSTANCIA = FotoStorage.desdeEntorno();
  private Fotos() {}
  public static FotoStorage storage() { return INSTANCIA; }
}
