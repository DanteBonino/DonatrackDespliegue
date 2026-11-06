package Repositories.Generales.filtros;

import java.util.UUID;
import javax.persistence.Query;

public abstract class Filtro implements FiltroDeBusqueda {
  private final String nombrePropiedad;
  private final Object valor;
  private final String nombreParametro;

  protected Filtro(String nombrePropiedad, Object valor) {
    this.nombrePropiedad = nombrePropiedad;
    this.valor = valor;
    this.nombreParametro = nombrePropiedad + "_" + UUID.randomUUID().toString().replace("-", "");
  }
  @Override
  public String construirCadenaDeComparacion(String alias) {
    return alias + "." + nombrePropiedad + " " + operador() + " :" + nombreParametro;
  }

  @Override
  public void aplicarParametros(Query query) {
    query.setParameter(nombreParametro, valor);
  }

  protected abstract String operador();

  public static Filtro igual(String propiedad, Object valor) { return new Igual(propiedad, valor); }
  public static Filtro mayorQue(String propiedad, Object valor) { return new MayorQue(propiedad, valor); }
  public static Filtro mayorOIgual(String propiedad, Object valor) { return new MayorOIgual(propiedad, valor); }
  public static Filtro menorQue(String propiedad, Object valor) { return new MenorQue(propiedad, valor); }
  public static Filtro menorOIgual(String propiedad, Object valor) { return new MenorOIgual(propiedad, valor); }

  private static class Igual extends Filtro {
    Igual(String p, Object v) { super(p, v); }
    protected String operador() { return "="; }
  }
  private static class MayorQue extends Filtro {
    MayorQue(String p, Object v) { super(p, v); }
    protected String operador() { return ">"; }
  }
  private static class MayorOIgual extends Filtro {
    MayorOIgual(String p, Object v) { super(p, v); }
    protected String operador() { return ">="; }
  }
  private static class MenorQue extends Filtro {
    MenorQue(String p, Object v) { super(p, v); }
    protected String operador() { return "<"; }
  }
  private static class MenorOIgual extends Filtro {
    MenorOIgual(String p, Object v) { super(p, v); }
    protected String operador() { return "<="; }
  }
}