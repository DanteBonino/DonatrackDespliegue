package Controllers.Web;

import java.util.Map;

public final class EstilosEstado {
  public record Estilo(String etiqueta, String icono, String clases) {}

  private static final Map<String, Estilo> ESTILOS = Map.of(
      "PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION", new Estilo("Pendiente de confirmación", "hourglass-split", "text-bg-secondary"),
      "ASIGNACION_REALIZADA", new Estilo("Asignada", "clipboard-check", "text-bg-primary"),
      "LISTA_PARA_ENTREGAR", new Estilo("Lista para entregar", "box-seam", "text-bg-warning"),
      "EN_TRASLADO", new Estilo("En traslado", "truck", "text-bg-info"),
      "ENTREGADA", new Estilo("Entregada", "check-circle", "text-bg-success"),
      "EN_DEPOSITO", new Estilo("En depósito", "archive", "text-bg-dark"),
      "ENTREGA_FALLIDA", new Estilo("Entrega fallida", "x-circle", "text-bg-danger"),
      "VENCIDA", new Estilo("Vencida", "calendar-x", "text-bg-danger")
  );

  public static Estilo de(String clave) {
    return ESTILOS.getOrDefault(clave, new Estilo(clave, "question-circle", "text-bg-secondary"));
  }

  public static String etiquetaDe(String clave) {
    return de(clave).etiqueta();
  }

  private EstilosEstado() {}
}