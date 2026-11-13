package Repositories.Generales.paginacion;

import Controllers.Web.ConPaginacion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public record Pagina<T>(List<T> items, int actual, int totalPaginas, long totalElementos, int tamanio) {

  public boolean tieneAnterior() { return actual > 1; }
  public boolean tieneSiguiente() { return actual < totalPaginas; }
  public int anterior() { return actual - 1; }
  public int siguiente() { return actual + 1; }

  public int desde() { return items.isEmpty() ? 0 : (actual - 1) * tamanio + 1; }
  public int hasta() { return items.isEmpty() ? 0 : desde() + items.size() - 1; }

  public Map<String, Object> aModelo() { return aModelo(""); }

  public Map<String, Object> aModelo(String filtrosQuery) {
    Map<String, Object> m = new HashMap<>();
    m.put("hayVarias", totalPaginas > 1);
    m.put("tieneAnterior", tieneAnterior());
    m.put("tieneSiguiente", tieneSiguiente());
    m.put("hrefAnterior", href(anterior(), filtrosQuery));
    m.put("hrefSiguiente", href(siguiente(), filtrosQuery));
    m.put("numeros", numeros(filtrosQuery));
    m.put("desde", desde());
    m.put("hasta", hasta());
    m.put("total", totalElementos);
    return m;
  }

  private static String href(int pagina, String filtrosQuery) {
    return "?pagina=" + pagina + filtrosQuery;
  }

  public List<Map<String, Object>> numeros(String filtrosQuery) {
    int desde = Math.max(1, actual - 2);
    int hasta = Math.min(totalPaginas, actual + 2);
    return IntStream.rangeClosed(desde, hasta)
        .mapToObj(n -> Map.<String, Object>of("numero", n, "activa", n == actual, "href", href(n, filtrosQuery)))
        .toList();
  }

  public static <T> Pagina<T> de(List<T> todos, int numero, int tamanio) {
    int totalPaginas = Math.max(1, (int) Math.ceil(todos.size() / (double) tamanio));
    int actual = Math.min(Math.max(numero, 1), totalPaginas);
    int desde = (actual - 1) * tamanio;
    int hasta = Math.min(desde + tamanio, todos.size());
    return new Pagina<>(todos.subList(desde, hasta), actual, totalPaginas, todos.size(), tamanio);
  }
}