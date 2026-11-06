package ActualizarDeInstanciasSegunArchivo;


import DTOs.DonanteDto;
import Direccion.Calle;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import ExceptionHandler.NoSePudoLeerElArchivo;
import ImportadorCSV.ArchivoCsv;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ArchivoCsvTest {
  private String ruta;
  private ArchivoCsv archivoCsv;

  @BeforeEach
  public void setUp() {
    ruta = System.getProperty("user.dir") + "/src/test/resources/donantesnuevos.csv";
    archivoCsv = new ArchivoCsv(ruta);
  }

  @Test
  void importarDonantesDesdeCSV() {

    List<DonanteDto> donantes = archivoCsv.importarInstancias();

    assertEquals(2,donantes.size());
  }
  @Test
  void importarDonantesVerificaLaInformacion() {

    List<DonanteDto> donantes = archivoCsv.importarInstancias();

    DonanteDto ana = donantes.get(0);
    assertEquals(TipoPersona.HUMANA, ana.getTipoPersona());
    assertEquals("12345678", ana.getDocumento());
    assertEquals("Ana", ana.getNombreRazonSocial());
    assertEquals("Perez", ana.getApellido());
    assertEquals(Genero.FEMENINO, ana.getGenero());
    assertEquals(LocalDate.of(1990, 5, 15), ana.getFechaDeNacimiento());
    assertTrue(ana.getCalle().esLaMismaQue(new Calle("Tinogasta", 3000)));
    assertEquals("ana@mail.com", ana.getEmail());
    assertEquals("+54 11 5555-5555", ana.getTelefono());
    assertEquals("1", ana.getId());

    DonanteDto arcos = donantes.get(1);
    assertEquals(TipoPersona.JURIDICA, arcos.getTipoPersona());
    assertEquals("30-12345678-9", arcos.getDocumento());
    assertEquals("Arcos Plateados S.A.", arcos.getNombreRazonSocial());
    assertEquals("contacto@empresa.com", arcos.getEmail());
    assertEquals("+54 11 4444-4444", arcos.getTelefono());
    assertEquals("Metalurgica", arcos.getRubro());
    assertEquals(TipoJuridica.EMPRESA, arcos.getTipoJuridico());
    assertEquals("2", arcos.getId());
  }

  @Test
  void seLanzaUnaExcepcionSiNoSePuedeLeerElArchivoEspeficado(){
    String rutaInvalida = "cualquierCosa";
    ArchivoCsv importador = new ArchivoCsv(rutaInvalida);
    assertThrows(NoSePudoLeerElArchivo.class, importador::importarInstancias);
  }
}
