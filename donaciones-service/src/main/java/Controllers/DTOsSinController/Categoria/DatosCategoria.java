package Controllers.DTOsSinController.Categoria;

import Bienes.EstadoDeUso;
import java.time.LocalDate;

public interface DatosCategoria {
  TipoCategoria getTipo();
  EstadoDeUso getEstadoDeUso();
  LocalDate getFechaDeVencimiento();
}
