package ImportadorDesdeArchivo;

import DTOs.DonanteDto;
import ImportadorDesdeArchivo.Archivos.Archivo;
import Repositories.Donantes.DonantesRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class ActualizadorDeInstanciasSegunArchivo implements WithSimplePersistenceUnit {
  private Archivo archivo;

  public ActualizadorDeInstanciasSegunArchivo(Archivo archivo) {
    this.archivo = archivo;
  }

  public void crearOActualizarInstanciasSegunArchivo() {
     this.archivo.importarInstancias()
         .forEach(this::registrarNuevoORefrescar);

  }

  private void registrarNuevoORefrescar(DonanteDto unDonanteImportado) {
    var nuevoPotencialDonante = unDonanteImportado.crearInstancia();
    DonantesRepository donantesRepository = DonantesRepository.getInstance();
    withTransaction(() -> {
      donantesRepository.getInstancias().stream()
          .filter(nuevoPotencialDonante::esIgualA)
          .findFirst()
          .ifPresentOrElse(
              (donanteEncontrado) -> donanteEncontrado.refrescar(unDonanteImportado),
              () -> donantesRepository.registrar(nuevoPotencialDonante)
          );
    });
  }
}
