package NotificarDonantesInactivos;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import InactividadDeDonante.PublicadorDeInactividad;
import Repositories.Donantes.DonantesRepository;
import Repositories.UsuariosRepository;
import TareasProgramadas.NotificarDonantesInactivos.NotificarDonantesInactivos;
import UsuarioFinal.UsuarioDonante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.lang.reflect.Field;
import utils.ConLimpiezaDeBaseDeDatos;
import utils.FechasUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utils.FabricaDeDirecciones.calleCualquiera;

class NotificarDonantesInactivosTest implements ConLimpiezaDeBaseDeDatos {
  private DonantesRepository donantesRepository;
  private PublicadorDeInactividad publicadorDeInactividad;
  private NotificarDonantesInactivos notificarDonantesInactivos;
  private UsuariosRepository usuariosRepository;

  @BeforeEach
  void setUp() {
    usuariosRepository = UsuariosRepository.getInstance();
    donantesRepository = DonantesRepository.getInstance();
    publicadorDeInactividad    = mock(PublicadorDeInactividad.class);
    notificarDonantesInactivos = new NotificarDonantesInactivos(publicadorDeInactividad);
  }

  @Test
  void siNoHayDonantesInactivosNoSePublicaNingunaInactividad() {
    notificarDonantesInactivos.ejecutar();

    verify(publicadorDeInactividad, never()).publicarInactividad(any());
  }

  @Test
  void siHayUnDonanteInactivoSePublicaSuInactividad() {
    Donante donanteInactivo = new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("fabio@dot.com")
            .conTelefono("1120304050")
            .build()
        )
        .conDocumento("45459090")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("Fabio", "Alvarez"))
        .conGenero(Genero.MASCULINO)
        .conCalle(calleCualquiera())
        .build();
    UsuarioDonante usuarioDonanteInactivo = new UsuarioDonante("dante", "test", donanteInactivo, FechasUtils.haceNDias(20));
    withTransaction(() -> {
      donantesRepository.registrar(donanteInactivo);
      usuariosRepository.registrar(usuarioDonanteInactivo);
    });
    notificarDonantesInactivos.ejecutar();
    verify(publicadorDeInactividad).publicarInactividad(donanteInactivo);
  }
}
