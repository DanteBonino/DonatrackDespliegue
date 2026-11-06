package NotificarDonantesInactivos;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import InactividadDeDonante.PublicadorDeInactividad;
import Repositories.Donantes.DonantesRepository;
import TareasProgramadas.NotificarDonantesInactivos.NotificarDonantesInactivos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.lang.reflect.Field;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utils.FabricaDeDirecciones.calleCualquiera;

class NotificarDonantesInactivosTest implements ConLimpiezaDeBaseDeDatos {
  private DonantesRepository donantesRepository;
  private PublicadorDeInactividad publicadorDeInactividad;
  private NotificarDonantesInactivos notificarDonantesInactivos;

  @BeforeEach
  void setUp() {
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

    setFechaDeUltimoIngreso(donanteInactivo, LocalDate.now().minusDays(21));
    withTransaction(() -> {
      donantesRepository.registrar(donanteInactivo);

    });
    notificarDonantesInactivos.ejecutar();
    verify(publicadorDeInactividad).publicarInactividad(donanteInactivo);
  }

  @Test
  void siHayVariosDonantesInactivosSePublicaLaInactividadDeCadaUno() {

    Donante primerDonanteInactivo = new DonanteHumanoBuilder()
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

    Donante segundoDonanteInactivo = new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("dante@example.com")
            .conTelefono("1120304050")
            .build()
        )
        .conDocumento("46458080")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("Dante", "Perez"))
        .conGenero(Genero.MASCULINO)
        .conCalle(calleCualquiera())
        .build();

    setFechaDeUltimoIngreso(primerDonanteInactivo, LocalDate.now().minusDays(21));
    setFechaDeUltimoIngreso(segundoDonanteInactivo, LocalDate.now().minusDays(30));
    withTransaction(() -> {
      donantesRepository.registrar(primerDonanteInactivo);
      donantesRepository.registrar(segundoDonanteInactivo);
    });

    notificarDonantesInactivos.ejecutar();

    verify(publicadorDeInactividad).publicarInactividad(primerDonanteInactivo);
    verify(publicadorDeInactividad).publicarInactividad(segundoDonanteInactivo);
    verify(publicadorDeInactividad, times(2)).publicarInactividad(any());
  }


  
  private void setFechaDeUltimoIngreso(Object usuario, LocalDate fecha) {
    try {
      Field f = usuario.getClass().getSuperclass().getDeclaredField("fechaDeUltimoIngreso");
      f.setAccessible(true);
      f.set(usuario, fecha);
    } catch (NoSuchFieldException e) {
      try {
        // in case the field is declared higher in the hierarchy
        Field f = usuario.getClass().getSuperclass().getSuperclass().getDeclaredField("fechaDeUltimoIngreso");
        f.setAccessible(true);
        f.set(usuario, fecha);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
