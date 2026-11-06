package datosBaseDeDatos.CargaDeDatos;

import static utils.FabricaDeDatos.unDonanteDePrueba;
import static utils.FabricaDeDirecciones.calleCualquiera;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.DonanteJuridicoBuilder;
import Donantes.DonanteJuridico.TipoJuridica;
import Repositories.Donantes.DonantesRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDate;
import java.util.List;

public class CrudDonantes extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CrudDonantes().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    cargarEscenario();
  }

  public void cargarEscenario() {
    DonantesRepository donantesRepository = DonantesRepository.getInstance();
    Donante gus = new DonanteHumanoBuilder()
        .conNombreCompleto(new NombreCompleto("gustavo", "ekiel"))
        .conDocumento("11223344")
        .conCalle(calleCualquiera())
        .conGenero(Genero.MASCULINO)
        .conContacto(
            new DatosDeContactoBuilder()
                .conMail("gus@gmail.com")
                .conTelefono("1122445566")
                .build()
        )
        .conFechaDeNacimiento(LocalDate.of(1987, 10, 2))
        .build();
    Donante arcor = new DonanteJuridicoBuilder()
        .conContacto(new DatosDeContactoBuilder()
            .conMail("empleado1@gmail.com")
            .conTelefono("90290929")
            .build())
        .conRazonSocial("vender golosinas")
        .conCuit("11255666")
        .conRubro("alimenticio")
        .conTipoJuridico(TipoJuridica.EMPRESA)
        .build();
    List<Donante> donantes = List.of(unDonanteDePrueba(), gus, arcor);
    withTransaction(() -> {
      donantesRepository.registrarInstancias(donantes);
    });
  }

}