package Controllers.DTOsSinController.CondicionDeAceptacion;

import Bienes.SubCategoria.SubCategoria;
import Necesidades.CondicionesDeAceptacion.CondicionDeAceptacion;
import java.math.BigDecimal;
import utils.FabricaDeCondicionesDeAceptacion;

public enum TipoCondicion {
  BASICA {
    public CondicionDeAceptacion crearInstancia(SubCategoria subCategoria) {
      return FabricaDeCondicionesDeAceptacion.unaDeVigencia(subCategoria);
    }
  },
  NUEVO {
    public CondicionDeAceptacion crearInstancia(SubCategoria subCategoria) {
      return FabricaDeCondicionesDeAceptacion.Nuevo(subCategoria);
    }
  },
  USADO {
    public CondicionDeAceptacion crearInstancia(SubCategoria subCategoria) {
      return FabricaDeCondicionesDeAceptacion.Usado(subCategoria);
    }
  };

  public abstract CondicionDeAceptacion crearInstancia(SubCategoria subCategoria);
}
