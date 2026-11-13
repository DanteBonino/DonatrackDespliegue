package Controllers.Web;

import EstadoDonacion.EstadoDonacion;

public record FiltroDonaciones(EstadoDonacion estado, Long categoriaId, Long subCategoriaId) {

  public boolean hayFiltros() {
    return estado != null || categoriaId != null || subCategoriaId != null;
  }
  public String comoQuery() {
    StringBuilder q = new StringBuilder();
    if (estado != null) q.append("&estado=").append(estado.name());
    if (categoriaId != null) q.append("&categoria=").append(categoriaId);
    if (subCategoriaId != null) q.append("&subcategoria=").append(subCategoriaId);
    return q.toString();
  }
}