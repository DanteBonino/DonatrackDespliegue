package ServiceLocator;


import domain.eventos.EntregaPublicador;

public class ServiceLocator {
  private static EntregaPublicador publicador;

  public static void configurar(EntregaPublicador publicadorConfigurado) {
    publicador = publicadorConfigurado;
  }

  public static EntregaPublicador obtenerPublicadorCambioDeEstado() {
    return publicador;
  }
}
