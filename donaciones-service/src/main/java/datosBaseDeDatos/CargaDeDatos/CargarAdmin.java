package datosBaseDeDatos.CargaDeDatos;

import Contacto.DatosDeContactoBuilder;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.UsuariosRepository;
import Usuario.Administrador;
import UsuarioFinal.UsuarioAdministrador;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class CargarAdmin extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {

  public static void main(String[] args) {
    new CargarAdmin().ejecutar();
  }

  @Override
  public void ejecutar() {
    super.ejecutar();
    cargar();
  }

  public void cargar() {
    AdministradoresRepository adminsRepo =
        AdministradoresRepository.getInstance();

    String email = "admin@donatrack.com";
    String password = "admin123";

    // Crear el Administrador con sus datos de contacto
    Administrador admin = new Administrador(
        new DatosDeContactoBuilder()
            .conMail(email)
            .conTelefono("1122334455")
            .build()
    );
    UsuarioAdministrador usuario = new UsuarioAdministrador(
       "admin1",
        "sarasa",
        admin
    );

    withTransaction(() -> {
      adminsRepo.registrar(admin);
      UsuariosRepository.getInstance().registrar(usuario);
    });
  }
}