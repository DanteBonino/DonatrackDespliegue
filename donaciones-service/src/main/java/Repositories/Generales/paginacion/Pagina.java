package Repositories.Generales.paginacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public record Pagina<T>(List<T> items, int actual, int totalPaginas, long totalElementos) {

  public boolean tieneAnterior() { return actual > 1; }
  public boolean tieneSiguiente() { return actual < totalPaginas; }
  public int anterior() { return actual - 1; }
  public int siguiente() { return actual + 1; }

  public List<Map<String, Object>> numeros() {
    int desde = Math.max(1, actual - 2);
    int hasta = Math.min(totalPaginas, actual + 2);
    return IntStream.rangeClosed(desde, hasta)
        .mapToObj(n -> Map.<String, Object>of("numero", n, "activa", n == actual))
        .toList();
  }

  public Map<String, Object> aModelo() {
    Map<String, Object> m = new HashMap<>();
    m.put("hayVarias", totalPaginas > 1);
    m.put("tieneAnterior", tieneAnterior());
    m.put("tieneSiguiente", tieneSiguiente());
    m.put("anterior", anterior());
    m.put("siguiente", siguiente());
    m.put("numeros", numeros());
    m.put("totalElementos", totalElementos);
    return m;
  }

  public static <T> Pagina<T> de(List<T> todos, int numero, int tamanio, long totalElementos) {
    int totalPaginas = Math.max(1, (int) Math.ceil(todos.size() / (double) tamanio));
    int actual = Math.min(Math.max(numero, 1), totalPaginas);
    int desde = (actual - 1) * tamanio;
    int hasta = Math.min(desde + tamanio, todos.size());
    return new Pagina<>(todos.subList(desde, hasta), actual, totalPaginas, totalElementos);
  }
}