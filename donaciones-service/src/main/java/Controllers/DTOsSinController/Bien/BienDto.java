package Controllers.DTOsSinController.Bien;

import Bienes.Bien;
import Bienes.EstadoDeUso;
import Bienes.NoPerecedero;
import Bienes.Perecedero;
import Controllers.ConBusquedaDeSubCategoria;
import Controllers.SubCategorias.SubCategoriaDto;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BienDto implements ConBusquedaDeSubCategoria {
  private BigDecimal contenido;
  private TipoBien tipo;
  private LocalDate fechaDeVencimiento;
  private EstadoDeUso estado;
  private SubCategoriaDto subCategoria;

  public Bien crearInstancia() {
    return tipo.crearInstancia(buscarSubCategoria(subCategoria.getNombre()), contenido, fechaDeVencimiento, estado);
  }

  public SubCategoriaDto getSubCategoria() {
    return this.subCategoria;
  }

  public static BienDto desde(Bien unBien) {
    BienDto response = new BienDto();
    response.subCategoria = SubCategoriaDto.desde(unBien.getSubCategoria());
    response.contenido = unBien.getContenido();
    if(unBien instanceof Perecedero) {
      response.fechaDeVencimiento = (LocalDate) unBien.getFactorDeAgrupamiento();
    }
    else {
      response.estado = (EstadoDeUso) unBien.getFactorDeAgrupamiento();
    }

    return response;
  }
}
