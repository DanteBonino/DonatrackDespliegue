package Controllers.Lote;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Controllers.ConBusquedaDeSubCategoria;
import Controllers.DTOsSinController.Bien.TipoBien;
import Donaciones.Item;
import Repositories.SubCategorias.SubCategoriasRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import utils.FabricaDeItems;

public class ItemDtoParaCreacionDeLote implements ConBusquedaDeSubCategoria {
  private int cantidadDeUnidades;
  private String nombreSubCategoria;
  private LocalDate fechaDeVencimiento;
  private EstadoDeUso estadoDeUso;
  private List<String> fotos;
  private TipoBien tipoDeBien;
  private BigDecimal contenido;

  public Item crearItem() {
    return FabricaDeItems.nuevoItem(
        tipoDeBien.crearInstancia(buscarSubCategoria(nombreSubCategoria), contenido, fechaDeVencimiento, estadoDeUso),
        cantidadDeUnidades,
        fotos
    );
  }

  public int getCantidadDeUnidades() {
    return cantidadDeUnidades;
  }

  public String getNombreSubCategoria() {
    return nombreSubCategoria;
  }

  public LocalDate getFechaDeVencimiento() {
    return fechaDeVencimiento;
  }

  public EstadoDeUso getEstadoDeUso() {
    return estadoDeUso;
  }

  public List<String> getFotos() {
    return fotos;
  }

  public TipoBien getTipoDeBien() {
    return tipoDeBien;
  }

  public BigDecimal getContenido() {
    return contenido;
  }

  public ItemDtoParaCreacionDeLote setCantidadDeUnidades(int cantidadDeUnidades) {
    this.cantidadDeUnidades = cantidadDeUnidades;
    return this;
  }

  public ItemDtoParaCreacionDeLote setNombreSubCategoria(String nombreSubCategoria) {
    this.nombreSubCategoria = nombreSubCategoria;
    return this;
  }

  public ItemDtoParaCreacionDeLote setFechaDeVencimiento(LocalDate fechaDeVencimiento) {
    this.fechaDeVencimiento = fechaDeVencimiento;
    return this;
  }

  public ItemDtoParaCreacionDeLote setEstadoDeUso(EstadoDeUso estadoDeUso) {
    this.estadoDeUso = estadoDeUso;
    return this;
  }

  public ItemDtoParaCreacionDeLote setFotos(List<String> fotos) {
    this.fotos = fotos;
    return this;
  }

  public ItemDtoParaCreacionDeLote setTipoDeBien(TipoBien tipoDeBien) {
    this.tipoDeBien = tipoDeBien;
    return this;
  }

  public ItemDtoParaCreacionDeLote setContenido(BigDecimal contenido) {
    this.contenido = contenido;
    return this;
  }

}
