package UsuarioFinal;
import java.time.LocalDate;

public interface ConSesion {
  int DIAS_PARA_INACTIVIDAD = 20;
  LocalDate getUltimoAcceso();

  default Boolean esInactivo() {
    return this.getCantidadDeDiasInactivo() >= DIAS_PARA_INACTIVIDAD;
  }

  default int getCantidadDeDiasInactivo() {
    return this.getUltimoAcceso().datesUntil(LocalDate.now()).toArray().length;
  }

  void registrarIngreso();
}
