package Controllers.DTOsSinController.Bien;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Bienes.SubCategoria.SubCategoria;
import java.math.BigDecimal;
import java.time.LocalDate;

public enum TipoBien {
  PERECEDERO {
    @Override
    public Bien crearInstancia(SubCategoria subCategoria, BigDecimal contenido, LocalDate fechaDeVencimiento, EstadoDeUso estadoDeUso) {
      return new Perecedero(subCategoria, contenido, fechaDeVencimiento);
    }
  },
  NOPERECEDERO {
    @Override
    public Bien crearInstancia(SubCategoria subCategoria, BigDecimal contenido, LocalDate fechaDeVencimiento, EstadoDeUso estadoDeUso) {
      return new NoPerecedero(subCategoria, contenido, estadoDeUso);
    }
  };

  public abstract Bien crearInstancia(SubCategoria subCategoria, BigDecimal contenido, LocalDate fechaDeVencimiento, EstadoDeUso estadoDeUso);
}
