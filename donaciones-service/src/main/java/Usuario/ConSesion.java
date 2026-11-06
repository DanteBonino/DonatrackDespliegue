package Usuario;
import java.time.LocalDate;

public interface ConSesion {
  int DIAS_PARA_INACTIVIDAD = 20;
  LocalDate fechaDeUltimoIngreso();

  default Boolean esInactivo() {
    return this.getCantidadDeDiasInactivo() >= DIAS_PARA_INACTIVIDAD;
  }

  default int getCantidadDeDiasInactivo() {
    return this.fechaDeUltimoIngreso().datesUntil(LocalDate.now()).toArray().length;
  }

  void registrarIngreso();
}
