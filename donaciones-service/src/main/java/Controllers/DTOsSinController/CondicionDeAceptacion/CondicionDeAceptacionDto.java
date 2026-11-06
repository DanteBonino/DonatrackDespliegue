package Controllers.DTOsSinController.CondicionDeAceptacion;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Controllers.SubCategorias.SubCategoriaDto;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.CondicionesDeAceptacion.Basica;
import utils.FabricaDeCondicionesDeAceptacion;

public class CondicionDeAceptacionDto {
  private SubCategoriaDto subCategoriaDto;
  private EstadoDeUso estado;
  private TipoCondicion tipo;

  public CondicionDeAceptacion crearInstancia() {
    SubCategoria subCategoria = subCategoriaDto.crearInstancia();
    CondicionDeAceptacion condicionDeAceptacion;
    switch (tipo) {
      case NUEVO -> condicionDeAceptacion  = FabricaDeCondicionesDeAceptacion.Nuevo(subCategoria);
      case USADO ->  condicionDeAceptacion = FabricaDeCondicionesDeAceptacion.Usado(subCategoria);
      default -> condicionDeAceptacion = new Basica(subCategoria);
    }
    return condicionDeAceptacion;
  }

  public static CondicionDeAceptacionDto desde(CondicionDeAceptacion unaCondicion) {
    CondicionDeAceptacionDto condicionDeAceptacionDto = new CondicionDeAceptacionDto();
    condicionDeAceptacionDto.setSubCategoriaDto(SubCategoriaDto.desde(unaCondicion.getSubCategoria()));
    condicionDeAceptacionDto.setTipo(unaCondicion.getTipo());
    switch (unaCondicion.getTipo()) {
      case NUEVO -> condicionDeAceptacionDto.setEstado(EstadoDeUso.NUEVO);
      case USADO -> condicionDeAceptacionDto.setEstado(EstadoDeUso.USADO);
    }
    return condicionDeAceptacionDto;
  }

  public void setSubCategoriaDto(SubCategoriaDto subCategoriaDto) {
    this.subCategoriaDto = subCategoriaDto;
  }

  public void setTipo(TipoCondicion tipo) {
    this.tipo = tipo;
  }

  public void setEstado(EstadoDeUso estado) {
    this.estado = estado;
  }

}
