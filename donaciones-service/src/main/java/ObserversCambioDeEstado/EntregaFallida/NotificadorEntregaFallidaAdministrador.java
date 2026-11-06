package ObserversCambioDeEstado.EntregaFallida;
import Donaciones.DonacionIndependiente;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import ObserversCambioDeEstado.InteresadoEnCambiosDeEstado;
import Notificaciones.NotificacionPreferencial;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Usuario.Administrador;
import java.util.List;

public class  NotificadorEntregaFallidaAdministrador implements InteresadoEnCambiosDeEstado {

  @Override
  public void anteEntregaFallida(String justificacion, EntidadBeneficiaria entidadBeneficiaria, DonacionIndependiente donacionIndependiente) {
    List<Administrador> administradores = AdministradoresRepository.getInstance().getInstancias();
    administradores.forEach(admin ->
        NotificacionesRepository.getInstance().registrar(
            new NotificacionPreferencial("La donacion id:" + donacionIndependiente.getIdDominio() +
        "no se pudo entregar, motivo: " + justificacion, admin.getDatosDeContacto())
        )
    );
  }
}
