package ObserversCambioDeEstado.InicioDeRuta;

public class GeneradorLinkApi implements GeneradorLinkDeTrackeoEntrega {
  private String urlLogisitica;

  public GeneradorLinkApi(String urlLogistica) {
    this.urlLogisitica = urlLogistica;
  }

  @Override
  public String generarLink(String idEntrega) {
    return urlLogisitica + "/entregas/" + idEntrega + "/tracking";
  }
}
