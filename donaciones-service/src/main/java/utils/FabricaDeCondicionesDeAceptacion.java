package utils;

import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Controllers.DTOsSinController.CondicionDeAceptacion.TipoCondicion;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import Necesidades.CondicionesDeAceptacion.EnEstado;
import Necesidades.CondicionesDeAceptacion.Basica;

public class FabricaDeCondicionesDeAceptacion {
  public static CondicionDeAceptacion unaDeVigencia(SubCategoria subCategoria) {
    return new Basica(subCategoria);
  }
  public static EnEstado Nuevo(SubCategoria subCategoria) {
    return new EnEstado(subCategoria, EstadoDeUso.NUEVO);
  }

  public static EnEstado Usado(SubCategoria subCategoria) {
    return new EnEstado(subCategoria, EstadoDeUso.USADO);
  }

  public static CondicionDeAceptacion nuevaCondicionSegunTipo(TipoCondicion tipoCondicion, SubCategoria subCategoria) {
    return switch (tipoCondicion) {
      case BASICA -> unaDeVigencia(subCategoria);
      case NUEVO  -> Nuevo(subCategoria);
      case USADO  -> Usado(subCategoria);
    };
  }

}
