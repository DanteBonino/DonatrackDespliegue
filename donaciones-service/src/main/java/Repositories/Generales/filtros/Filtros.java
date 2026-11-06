package Repositories.Generales.filtros;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Query;

public class Filtros implements FiltroDeBusqueda {
  private final List<FiltroDeBusqueda> filtros = new ArrayList<>();

  public void agregarFiltro(FiltroDeBusqueda filtro) {
    filtros.add(filtro);
  }

  @Override
  public String construirCadenaDeComparacion(String alias) {
    return filtros.stream()
        .map(f -> f.construirCadenaDeComparacion(alias))
        .collect(Collectors.joining(" AND "));
  }

  @Override
  public void aplicarParametros(Query query) {
    filtros.forEach(f -> f.aplicarParametros(query));
  }
}
