package Repositorios;

import Contacto.DatosDeContactoBuilder;
import Controllers.DTOsSinController.Direccion.Request.CalleRequestDto;
import Controllers.Donantes.DonanteRequestBuilder;
import Controllers.Donantes.DonanteRequestDto;
import Direccion.Calle;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteHumana.PersonaHumana;
import Donantes.DonanteJuridico.DonanteJuridicoBuilder;
import Donantes.DonanteJuridico.PersonaJuridica;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import ExceptionHandler.DatosInvalidosException;
import ExceptionHandler.RecursoNoEncontradoException;
import MedioDeNotificacion.TipoDeMedio;
import Repositories.Donantes.DonantesRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static utils.FabricaDeDirecciones.calleCualquiera;


@ExtendWith(MockitoExtension.class)
public class DonantesRepositoryTest implements ConLimpiezaDeBaseDeDatos {
    private DonantesRepository donantesRepository;
    @BeforeEach
    void setUp() {
      donantesRepository = DonantesRepository.getInstance();
    }

    @Nested
    @DisplayName("Creacion de Donantes")
    class  RegistroDeDonantes {
      private Donante donante;

      @BeforeEach
      void setUp() {
        donante = unDonanteDePrueba();
      }

      @Test
      void siSeRegistraUnDonanteYNoHabiaDonantesRegistradosPasaAHaberUnDonanteRegistrado(){
        withTransaction(() -> {
          donantesRepository.registrar(donante);
        });
        assertEquals(1, donantesRepository.cantidadDeDonantes());
      }
    }

    @Nested
    class OperacionesDeFiltrado {
      private Donante unDonanteInactivo;
      private Donante unDonanteActivo;

      @BeforeEach
      void setUp() {
       unDonanteActivo = unDonanteDePrueba();
       unDonanteInactivo = unDonanteDePrueba();
       setFechaDeUltimoIngreso(unDonanteInactivo, LocalDate.now().minusDays(21));
       withTransaction(() -> {
         donantesRepository.registrar(unDonanteActivo);
         donantesRepository.registrar(unDonanteInactivo);
       });
      }

      @Test
      void getDonantesInactivosSoloRetornaLosDonantesInactivos() {
        assertEquals(1, donantesRepository.getInactivos().size());
      }

      @Test
      void findByIdEnCasoDeHaberUnDonanteConEseIdLoDevuelve() {
        Donante encontrado = donantesRepository.findById(unDonanteActivo.getIdDominio());
        assertTrue(encontrado.esLaMismaEntidad(unDonanteActivo.getIdDominio()));
      }

      @Test
      void findByIdEnCasoDeNoHaberUnDonanteConDichoIdArrojaUnaRecursoNoEncontradoException() {
        assertThrows(RecursoNoEncontradoException.class, () -> donantesRepository.findById("1"));
      }
    }

    @Nested
    class EliminarDonante {
      private Donante donanteASerEliminado;

      @BeforeEach
      void setUp() {
        donanteASerEliminado = unDonanteDePrueba();
        withTransaction(() -> {
          donantesRepository.registrar(donanteASerEliminado);
        });
      }

      @Test
      void seEliminaAlDonanteCuyoIdCoincidaConElElegidoParaEliminar() {
        assertEquals(1, donantesRepository.cantidadDeDonantes());
        withTransaction(() -> {
          donantesRepository.eliminarInstancia(donanteASerEliminado.getIdDominio());
        });
        assertEquals(0, donantesRepository.cantidadDeDonantes());
      }

      @Test
      void siNoExisteUnRecursoConElIdAEliminarSeLanzaUnaRecursoNoEncontradoException() {
        assertThrows(RecursoNoEncontradoException.class, () -> donantesRepository.eliminarInstancia("2"));
      }

    }

    @Nested
  class ActualizarDonante {
      private Donante donanteASerActualizado;
     private DonanteRequestDto datos;

      @BeforeEach
      void setUp() {
        donanteASerActualizado = unDonanteDePrueba();
        datos = unosDatos(TipoPersona.HUMANA);
        withTransaction(() -> {
          donantesRepository.registrar(donanteASerActualizado);
        });
      }

      @Test
      void siNoExisteUnRecursoConElIdAActualizarSeLanzaUnaRecursoNoEncontradoException() {
        assertThrows(RecursoNoEncontradoException.class, () -> donantesRepository.actualizarInstancia("2", datos));
      }

      @Test
      void siExisteElRecursoAActualizarYNoSeRecibeElTipoDePersonaEntoncesNoSeLanzaUnaExcepcionDatosInvalidos() {
          assertDoesNotThrow(() -> donantesRepository.actualizarInstancia(donanteASerActualizado.getIdDominio(), datos));
        }

        @Test
        void siElDonanteEsHumanoYElTipoDePersonaEsJuridicoSeLanzaUnaDatosInvalidosException() {
          assertThrows(DatosInvalidosException.class, () -> donantesRepository.actualizarInstancia(donanteASerActualizado.getIdDominio(), unosDatos(TipoPersona.JURIDICA)));
        }

      }

    @Nested
    class ActualizarDonanteHumano {
       private PersonaHumana donanteHumanoAActualizar;
       private String idDonanteAActualizar;
       private DonanteRequestBuilder donanteRequestBuilder;

       @BeforeEach
       void setUp() {
         donanteHumanoAActualizar = unDonanteDePrueba();
         withTransaction(() -> {
           donantesRepository.registrar(donanteHumanoAActualizar);
         });


         idDonanteAActualizar  = donanteHumanoAActualizar.getIdDominio();
         donanteRequestBuilder = new DonanteRequestBuilder();
       }

       @Test
       void sePuedeActualizarDeFormaParcial() {
         DonanteRequestDto actualizadorDeGenero = donanteRequestBuilder
             .genero(Genero.MASCULINO)
             .build();
        withTransaction(() -> {
          donantesRepository.actualizarInstancia(idDonanteAActualizar, actualizadorDeGenero);
        });


         PersonaHumana esperado = new DonanteHumanoBuilder()
             .conContacto(new DatosDeContactoBuilder()
                 .conTelefono("1120304050")
                 .conMail("dante@gmail.com")
                 .build()
             )
             .conDocumento("45459090")
             .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
             .conNombreCompleto(new NombreCompleto("dante", "dante"))
             .conGenero(Genero.MASCULINO)
             .conCalle(donanteHumanoAActualizar.getCalle())
             .build();

         assertThat(donanteHumanoAActualizar)
             .usingRecursiveComparison()
             .ignoringFields("idDominio", "datosDeContacto", "id") // el builder genera un id nuevo, hay que ignorarlo
             .isEqualTo(esperado);
       }

       @Test
      void sePuedeActualizarDeFormaTotal() {
         DonanteRequestDto actualizadorTotal = donanteRequestBuilder
             .genero(Genero.FEMENINO)
             .telefono("1122334455")
             .email("dantenuevo@gmail.com")
             .fechaDeNacimiento(LocalDate.of(2001, 10, 20))
             .nombreRazonSocial("gustavo")
             .apellido("gustavo")
             .calle(new CalleRequestDto("Segurola", 4555))
             .documento("0000000")
             .build();
         withTransaction(() -> {
           donantesRepository.actualizarInstancia(idDonanteAActualizar, actualizadorTotal);
         });


         PersonaHumana esperado = new DonanteHumanoBuilder()
             .conGenero(Genero.FEMENINO)
             .conContacto(new DatosDeContactoBuilder()
                 .conTelefono("1122334455")
                 .conMail("dantenuevo@gmail.com")
                 .build()
             )
             .conFechaDeNacimiento(LocalDate.of(2001, 10, 20))
             .conNombreCompleto(new NombreCompleto("gustavo", "gustavo"))
             .conCalle(donanteHumanoAActualizar.getCalle())
             .conDocumento("0000000")
             .build();
         assertThat(donanteHumanoAActualizar)
             .usingRecursiveComparison()
             .ignoringFields("idDominio", "datosDeContacto", "id") // el builder genera un id nuevo, hay que ignorarlo
             .isEqualTo(esperado);
       }
    }

  @Nested
  class ActualizarDonanteJuridico {
    private PersonaJuridica donanteJuridico;
    private String idDonanteAActualizar;
    private DonanteRequestBuilder donanteRequestBuilder;

    @BeforeEach
    void setUp() {
      donanteJuridico = new DonanteJuridicoBuilder()
          .conContacto(new DatosDeContactoBuilder()
              .conTelefono("1120304050")
              .conMail("dante@gmail.com")
              .build()
          )
          .conCuit("45459090")
          .conRazonSocial("UnaRazon")
          .conTipoJuridico(TipoJuridica.EMPRESA)
          .conRubro("unRubro")
          .build();
      withTransaction(() -> {
        donantesRepository.registrar(donanteJuridico);
      });
      idDonanteAActualizar  = donanteJuridico.getIdDominio();
      donanteRequestBuilder = new DonanteRequestBuilder();
    }

    @Test
    void sePuedeActualizarDeFormaParcial() {
      DonanteRequestDto actualizadorDeRubro = donanteRequestBuilder
              .rubro("nuevoRubro")
              .build();

      donantesRepository.actualizarInstancia(idDonanteAActualizar, actualizadorDeRubro);

      PersonaJuridica esperado = new DonanteJuridicoBuilder()
          .conContacto(new DatosDeContactoBuilder()
              .conTelefono("1120304050")
              .conMail("dante@gmail.com")
              .build()
          )
          .conRubro("nuevoRubro")
          .conTipoJuridico(TipoJuridica.EMPRESA)
          .conCuit("45459090")
          .conRazonSocial("UnaRazon")
          .build();

      assertThat(donanteJuridico)
          .usingRecursiveComparison()
          .ignoringFields("idDominio", "id", "datosDeContacto")
          .isEqualTo(esperado);
    }

    @Test
    void sePuedeActualizarDeFormaTotal() {
      DonanteRequestDto actualizadorTotal = donanteRequestBuilder
          .rubro("sarasa")
          .telefono("1122334455")
          .email("dantenuevo@gmail.com")
          .nombreRazonSocial("gustavo")
          .documento("0000000")
          .tipoJuridico(TipoJuridica.ONG)
          .build();
      donantesRepository.actualizarInstancia(idDonanteAActualizar, actualizadorTotal);

      PersonaJuridica esperado = new DonanteJuridicoBuilder()
          .conContacto(new DatosDeContactoBuilder()
              .conTelefono("1122334455")
              .conMail("dantenuevo@gmail.com")
              .build()
          )
          .conRazonSocial("gustavo")
          .conCuit("0000000")
          .conTipoJuridico(TipoJuridica.ONG)
          .conRubro("sarasa")
          .build();

      assertThat(donanteJuridico)
          .usingRecursiveComparison()
          .ignoringFields("idDominio", "id", "datosDeContacto")
          .isEqualTo(esperado);
    }
  }

  private PersonaHumana unDonanteDePrueba() {
    return  new DonanteHumanoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1120304050")
            .conMail("dante@gmail.com")
            .build()
        )
        .conDocumento("45459090")
        .conFechaDeNacimiento(LocalDate.of(2004, 10, 26))
        .conNombreCompleto(new NombreCompleto("dante", "dante"))
        .conGenero(Genero.X)
        .conCalle(new Calle("Cordoba", 4500))
        .build();
  }

  private DonanteRequestDto unosDatos(TipoPersona tipoPersona) {
      return new DonanteRequestDto(
          tipoPersona,
          "44",
          "dante",
          "apellido",
          "algo@gmial.com",
          "1122334455",
          LocalDate.now().minusYears(10),
          new CalleRequestDto("calle", 111), Genero.MASCULINO, null, null, TipoDeMedio.WPP);
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
