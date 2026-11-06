package db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.FabricaDeDirecciones.calleCualquiera;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.DonanteJuridicoBuilder;
import Donantes.DonanteJuridico.TipoJuridica;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConLimpiezaDeBaseDeDatos;

public class DonantePersistenciaTest implements ConLimpiezaDeBaseDeDatos {
  private Donante dante;
  private Donante donaTrack;

  @BeforeEach
  void setUp() {
    dante = new DonanteHumanoBuilder()
        .conNombreCompleto(new NombreCompleto("dante", "bonino"))
        .conContacto(new DatosDeContactoBuilder()
            .conTelefono("1122334455")
            .conMail("dante@sendgrid.com")
            .build()
        )
        .conCalle(calleCualquiera())
        .conDocumento("1234567")
        .conFechaDeNacimiento(LocalDate.now())
        .conGenero(Genero.MASCULINO)
        .build();

    donaTrack = new DonanteJuridicoBuilder()
        .conRubro("donaciones")
        .conCuit("1122334455")
        .conRazonSocial("donar")
        .conTipoJuridico(TipoJuridica.INSTITUCION)
        .conContacto(
            new DatosDeContactoBuilder()
                .conMail("dona@gmail.com")
                .conTelefono("1122334455")
                .build()
        )
        .build();
    withTransaction(() -> {
      entityManager().persist(dante);
      entityManager().persist(donaTrack);
    });
  }

  @Test
  void danteDebeTenerUnIdNoNulo() {
    assertNotNull(dante.getId());
  }

  @Test
  void donatrackDebeTenerUnIdNoNulo() {
    assertNotNull(donaTrack.getId());
  }

}
