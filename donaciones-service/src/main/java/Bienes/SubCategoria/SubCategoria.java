package Bienes.SubCategoria;

import Bienes.Categoria.Categoria;
import Bienes.ConNombreUnico;
import Persistible.Persistible;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import utils.StringUtils;

@Entity
@Table(name = "SubCategorias")
public class SubCategoria extends Persistible implements ConNombreUnico {
  @Column(name = "nombre", unique = true, nullable = false)
  private String    nombre;
  @ManyToOne
  @JoinColumn(name = "id_categoria")
  private Categoria categoria;
  @Column(name = "es_perecedera")
  private Boolean   esPerecedera;


  protected SubCategoria() {

  }
  public SubCategoria(String nombre, Categoria categoria, Boolean esPerecedera) {
    this.nombre       = StringUtils.nombreAptoBaseDeDatos(nombre);
    this.categoria    = categoria;
    this.esPerecedera = esPerecedera;
  }

  public Boolean esPerecedera() {
    return esPerecedera;
  }

  public Boolean perteneceA(Categoria unaCategoria) {
    return unaCategoria.esLaMisma(this.getCategoria());
  }

  public Categoria getCategoria() {
    return categoria;
  }

  @Override
  public String getNombre() {
    return this.nombre;
  }

  public Boolean esLaMisma(SubCategoria otraSubCategoria) {
    return ConNombreUnico.super.esLaMisma(otraSubCategoria) && this.getCategoria().esLaMisma(otraSubCategoria.getCategoria());
  }
}
