package Repositories.Generales.filtros;


import javax.persistence.Query;

public interface FiltroDeBusqueda {
  String construirCadenaDeComparacion(String alias);
  void aplicarParametros(Query query);
}