package DTOs;

import Controllers.DTOsSinController.NotificableDatos;
import Direccion.Calle;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import Telefono.*;
import com.twilio.rest.insights.v1.Call;
import java.time.LocalDate;
import java.util.Optional;
import org.hibernate.Cache;

public interface DatosDonante extends NotificableDatos {
  String getNombreRazonSocial();
  String getApellido();
  String getDocumento();
  String getEmail();
  String getTelefono();
  LocalDate getFechaDeNacimiento();
  Genero getGenero();
  Calle getCalle();
  String getRubro();
  TipoJuridica getTipoJuridico();
  TipoPersona getTipoPersona();

  default Telefono getTelefonoSeteable() {
    return Optional.ofNullable(getTelefono())
        .map(TelefonoArgentino::nuevoTelefono)
        .orElse(null);
  }
}