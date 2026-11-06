package ActualizarDeInstanciasSegunArchivo;

import Contacto.DatosDeContactoBuilder;
import DTOs.DonanteDto;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteHumana.PersonaHumana;
import ImportadorDesdeArchivo.ActualizadorDeInstanciasSegunArchivo;
import ImportadorDesdeArchivo.Archivos.Archivo;
import Repositories.Donantes.DonantesRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import utils.ConLimpiezaDeBaseDeDatos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utils.FabricaDeDatos.unDonanteDePrueba;
import static utils.FabricaDeDirecciones.calleCualquiera;

public class ActualizadorDeInstanciasSegunArchivoTest implements ConLimpiezaDeBaseDeDatos {
  private DonantesRepository donantesRepositoryConPersistencia;
  private Archivo archivo;
  private ActualizadorDeInstanciasSegunArchivo actualizador;

  @BeforeEach
  void setUp() {
    donantesRepositoryConPersistencia = DonantesRepository.getInstance();
    archivo = mock(Archivo.class);
    actualizador = new ActualizadorDeInstanciasSegunArchivo(archivo);
  }

  @Test
  void siElArchivoNoTieneInstanciasNoSeRegistraNiSeRefrescaNada() {
    when(archivo.importarInstancias()).thenReturn(List.of());
    actualizador.crearOActualizarInstanciasSegunArchivo();
    assertEquals(0, donantesRepositoryConPersistencia.cantidadInstancias());
  }
  @Nested
  class CuandoHayInstanciasParaImportar {

    private DonanteDto donanteImportadoDesdeElArchivo;
    private Donante donanteInexistente;
    private Donante donanteExistente;
    private PersonaHumana donanteExistenteActualizado;

    @BeforeEach
    void setUpCuandoHayInstanciasParaImportar(){
      donanteImportadoDesdeElArchivo = mock(DonanteDto.class);
      donanteInexistente = (Donante) new DonanteHumanoBuilder()
          .conDocumento("4433443344")
          .conCalle(calleCualquiera())
          .conFechaDeNacimiento(LocalDate.now())
          .conContacto(
              new DatosDeContactoBuilder()
                  .conTelefono("1111111111")
                  .conMail("dante@gmail.com")
                  .build()
          )
          .conGenero(Genero.MASCULINO)
          .conNombreCompleto(new NombreCompleto("dante", "asd"))
          .build();
      donanteExistente = unDonanteDePrueba();
      donanteExistenteActualizado = (PersonaHumana) unDonanteDePrueba();
      donanteExistenteActualizado.setEmail("nuevoMail@gmail.com");
      withTransaction(() -> {
        donantesRepositoryConPersistencia.registrar(donanteExistente);
      });
      when(archivo.importarInstancias()).thenReturn(List.of(donanteImportadoDesdeElArchivo));

    }

    @Test
    void siElDonanteImportadoNoExisteEnElRepositorioSeRegistraComoNuevo() {
      when(donanteImportadoDesdeElArchivo.crearInstancia()).thenReturn(donanteInexistente);
      actualizador.crearOActualizarInstanciasSegunArchivo();
      assertEquals(2, donantesRepositoryConPersistencia.cantidadDeDonantes());
    }

//    @Test
//    void siElDonanteImportadoYaExisteEnElRepositorioSeRefrescaElExistenteYNoSeRegistraUnoNuevo() {
//      when(donanteImportadoDesdeElArchivo.crearInstancia()).thenReturn(donanteExistenteActualizado);
//      when(donanteImportadoDesdeElArchivo.getEmail()).thenReturn(donanteExistenteActualizado.getEmail());
//      when(donanteImportadoDesdeElArchivo.getTelefono()).thenReturn(donanteExistenteActualizado.getNumeroBase());
//      when(donanteImportadoDesdeElArchivo.getTipoDeMedio()).thenReturn(donanteExistenteActualizado.getMedioDeNotificacionPreferencial());
//      when(donanteImportadoDesdeElArchivo.getNombreRazonSocial()).thenReturn(donanteExistenteActualizado.getNombre());
//      when(donanteImportadoDesdeElArchivo.getApellido()).thenReturn(donanteExistenteActualizado.getApellido());
//      when(donanteImportadoDesdeElArchivo.getDocumento()).thenReturn(donanteExistenteActualizado.getDocumento());
//      when(donanteImportadoDesdeElArchivo.getGenero()).thenReturn(donanteExistenteActualizado.getGenero());
//      when(donanteImportadoDesdeElArchivo.getCalle()).thenReturn(donanteExistenteActualizado.getCalle());
//      when(donanteImportadoDesdeElArchivo.getFechaDeNacimiento()).thenReturn(donanteExistenteActualizado.getFechaDeNacimiento());
//      actualizador.crearOActualizarInstanciasSegunArchivo();
//      assertEquals(1, donantesRepositoryConPersistencia.cantidadDeDonantes());
//      Donante donanteActualizado = donantesRepositoryConPersistencia.getInstancias().get(0);
//      assertEquals(donanteExistenteActualizado.getEmail(), donanteActualizado.getEmail());
//    }

  }
}