package datosBaseDeDatos.CargaDeDatos;

import static utils.FabricaDeDirecciones.calleCualquiera;

import Contacto.DatosDeContactoBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.UsuariosRepository;
import UsuarioFinal.UsuarioBeneficiario;
import UsuarioFinal.UsuarioDonante;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import java.time.LocalDate;
import utils.FabricaDeDirecciones;
import utils.FabricaDeEntidadesBeneficiarias;

public class CargarBeneficiario extends ModificadorBaseDeDatos {
  public static void main(String[] args) {
    new CargarBeneficiario().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    EntidadBeneficiaria sancorSeguros = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Sancor Seguros")
        .conContacto(
            new DatosDeContactoBuilder()
                .conMail("dante@gmail.com")
                .conTelefono("1122445566")
                .build()
        )
        .conDireccion(FabricaDeDirecciones.direccionCualquiera())
        .build();
    UsuarioBeneficiario cuentaDeDante = new UsuarioBeneficiario("sancorSeguros", "sarasa", sancorSeguros);
    withTransaction(() -> {
      BeneficiariosRepository.getInstance().registrar(sancorSeguros);
      UsuariosRepository.getInstance().registrar(cuentaDeDante);
    });
  }
}