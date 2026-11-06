package NotificarDonantesInactivos;


import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import InactividadDeDonante.InteresadoEnInactividad;
import InactividadDeDonante.PublicadorDeInactividad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utils.FabricaDeDirecciones.calleCualquiera;

import java.time.LocalDate;

class
PublicadorDeInactividadTest {

  private PublicadorDeInactividad publicadorDeInactividad;
  private Donante unDonante;

  @BeforeEach
  void setUp() {
    publicadorDeInactividad = new PublicadorDeInactividad();
    unDonante = new DonanteHumanoBuilder()
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
  }

  @Test
  void siNoHayInteresadosSuscriptosPublicarInactividadNoFalla() {
    publicadorDeInactividad.publicarInactividad(unDonante);
  }

  @Test
  void siHayUnInteresadoSuscriptoSeLoNotificaAnteLaInactividad() {
    var interesado = mock(InteresadoEnInactividad.class);
    publicadorDeInactividad.suscribir(interesado);

    publicadorDeInactividad.publicarInactividad(unDonante);

    verify(interesado).anteInactividad(unDonante);
  }

  @Test
  void siHayVariosInteresadosSuscriptosSeNotificaACadaUno() {
    var primerInteresado = mock(InteresadoEnInactividad.class);
    var segundoInteresado = mock(InteresadoEnInactividad.class);
    publicadorDeInactividad.suscribir(primerInteresado);
    publicadorDeInactividad.suscribir(segundoInteresado);

    publicadorDeInactividad.publicarInactividad(unDonante);

    verify(primerInteresado).anteInactividad(unDonante);
    verify(segundoInteresado).anteInactividad(unDonante);
  }

  @Test
  void unInteresadoDesuscriptoNoEsNotificado() {
    var interesado = mock(InteresadoEnInactividad.class);
    publicadorDeInactividad.suscribir(interesado);
    publicadorDeInactividad.desuscribir(interesado);

    publicadorDeInactividad.publicarInactividad(unDonante);

    verify(interesado, never()).anteInactividad(any());
  }

  @Test
  void desuscribirUnInteresadoNoAfectaALosDemasSuscriptos() {
    var interesadoQueSeVa = mock(InteresadoEnInactividad.class);
    var interesadoQueQueda = mock(InteresadoEnInactividad.class);
    publicadorDeInactividad.suscribir(interesadoQueSeVa);
    publicadorDeInactividad.suscribir(interesadoQueQueda);
    publicadorDeInactividad.desuscribir(interesadoQueSeVa);

    publicadorDeInactividad.publicarInactividad(unDonante);

    verify(interesadoQueSeVa, never()).anteInactividad(any());
    verify(interesadoQueQueda).anteInactividad(unDonante);
  }

  @Test
  void publicarInactividadVariasVecesNotificaAlInteresadoEnCadaPublicacion() {
    var interesado = mock(InteresadoEnInactividad.class);
    var otroDonante = new DonanteHumanoBuilder()
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

    publicadorDeInactividad.suscribir(interesado);

    publicadorDeInactividad.publicarInactividad(unDonante);
    publicadorDeInactividad.publicarInactividad(otroDonante);

    verify(interesado).anteInactividad(unDonante);
    verify(interesado).anteInactividad(otroDonante);
  }
}