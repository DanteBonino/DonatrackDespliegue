package Controllers.EntidadesBenefeciarias;

import Controllers.DTOsSinController.NotificableDatos;
import MedioDeNotificacion.TipoDeMedio;
import Telefono.*;
import java.util.List;
import java.util.Optional;

public interface DatosEntidadesBeneficiarias extends NotificableDatos {

  String getRazonSocial();

  List<String> getMails();

  String getTelefono();

  TipoDeMedio getTipoDeMedio();

  default Telefono getTelefonoSeteable() {
    return Optional.ofNullable(getTelefono())
        .map(TelefonoArgentino::nuevoTelefono)
        .orElse(null);
  }
}
