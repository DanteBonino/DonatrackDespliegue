package datosBaseDeDatos.CargaDeDatos;

import static utils.FabricaDeDirecciones.calleCualquiera;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Repositories.Donantes.DonantesRepository;
import Repositories.UsuariosRepository;
import UsuarioFinal.UsuarioDonante;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import java.time.LocalDate;

public class CargarDonante extends ModificadorBaseDeDatos {
  public static void main(String[] args) {
    new CargarDonante().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    Donante dante = new DonanteHumanoBuilder()
        .conNombreCompleto(new NombreCompleto("dante", "bonino"))
        .conDocumento("11223344")
        .conCalle(calleCualquiera())
        .conGenero(Genero.MASCULINO)
        .conContacto(
            new DatosDeContactoBuilder()
                .conMail("dante@gmail.com")
                .conTelefono("1122445566")
                .build()
        )
        .conFechaDeNacimiento(LocalDate.of(1987, 10, 2))
        .build();
    UsuarioDonante cuentaDeDante = new UsuarioDonante("dante123", "sarasa", dante);
    withTransaction(() -> {
      DonantesRepository.getInstance().registrar(dante);
      UsuariosRepository.getInstance().registrar(cuentaDeDante);
    });
  }
}
