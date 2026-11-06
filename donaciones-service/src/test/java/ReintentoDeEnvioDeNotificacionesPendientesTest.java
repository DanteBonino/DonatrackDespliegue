import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import BusNotificaciones.BusNotificaciones;
import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Notificaciones.Notificacion;
import Notificaciones.NotificacionPreferencial;
import Repositories.Notificaciones.NotificacionesRepository;
import TareasProgramadas.ReintentoDeEnvioDeNotificaciones.ReintentoDeEnvioDeNotificacionesPendientes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLimpiezaDeBaseDeDatos;

@ExtendWith(MockitoExtension.class)
class ReintentoDeEnvioDeNotificacionesPendientesTest implements ConLimpiezaDeBaseDeDatos {
  private Notificacion unaNotificacion;
  private DatosDeContacto unosDatosDeContacto;
  @Mock
  private BusNotificaciones busNotificaciones;

  @BeforeEach
  void setUp() {
    unosDatosDeContacto = new DatosDeContactoBuilder()
        .conTelefono("1122334455")
        .conMail("dante@gmail.com")
        .build();
    unaNotificacion  = new NotificacionPreferencial("hola", unosDatosDeContacto);
    withTransaction(() -> {
      entityManager().persist(unosDatosDeContacto);
      NotificacionesRepository.getInstance().registrar(unaNotificacion);
    });
  }

  @Test
  void ejecutarEnviaTodasLasNotificacionesEnviables() {
    new ReintentoDeEnvioDeNotificacionesPendientes(busNotificaciones).ejecutar();
    verify(busNotificaciones).procesar(unaNotificacion);
  }

  @Test
  void ejecutarNoFallaSiNoHayNotificacionesEnviables() {
    unaNotificacion.pasarAEntregada();
    new ReintentoDeEnvioDeNotificacionesPendientes(busNotificaciones).ejecutar();
    verify(busNotificaciones, times(0)).procesar(unaNotificacion);
  }

}