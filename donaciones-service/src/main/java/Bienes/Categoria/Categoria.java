package Bienes.Categoria;

import Bienes.ConNombreUnico;
import Persistible.Persistible;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import utils.StringUtils;

@Entity
@Table(name = "Categorias")
public class Categoria extends Persistible implements ConNombreUnico {
  @Column(name = "nombre", unique = true, nullable = false)
  private String nombre;
  @Column(name = "unidad")
  private Unidad unidad;

  protected Categoria() {

  }

  public Categoria(String nombre, Unidad unidad) {
    this.nombre = StringUtils.nombreAptoBaseDeDatos(nombre);
    this.unidad = unidad;
  }

  public String getNombre() {
    return nombre;
  }

  public Boolean esLaMisma(Categoria otraCategoria) {
    return ConNombreUnico.super.esLaMisma(otraCategoria);
  }

  public Unidad getUnidad() {
    return this.unidad;
  }
}
