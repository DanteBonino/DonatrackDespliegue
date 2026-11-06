package Controllers.Necesidades;
import Bienes.SubCategoria.SubCategoria;
import Controllers.ConBusquedaDeSubCategoria;
import Controllers.DTOsSinController.CondicionDeAceptacion.TipoCondicion;
import Necesidades.Necesidad;
import Necesidades.TipoNecesidad;
import java.math.BigDecimal;
import java.time.Period;
import utils.FabricaDeCondicionesDeAceptacion;

public class NecesidadRequestDto implements ConBusquedaDeSubCategoria {
  private TipoNecesidad tipo;
  private Period periodo;
  private String nombreSubCategoria;
  private TipoCondicion tipoCondicion;
  private BigDecimal cantidadRequerida;


  public Necesidad crearInstancia() {
    return Necesidad.crear(tipo, tipoCondicion.crearInstancia(buscarSubCategoria(nombreSubCategoria)), cantidadRequerida,  periodo);
  }

  public TipoNecesidad getTipo() { return tipo; }

  public Period getPeriodo() { return periodo; }

  public String getNombreSubCategoria() {
    return nombreSubCategoria;
  }

  public TipoCondicion getTipoCondicion() { return tipoCondicion; }
  public BigDecimal getCantidadRequerida() { return cantidadRequerida; }
}
