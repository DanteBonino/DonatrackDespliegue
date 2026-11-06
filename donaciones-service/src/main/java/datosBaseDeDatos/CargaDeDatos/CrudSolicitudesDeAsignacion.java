package datosBaseDeDatos.CargaDeDatos;

import static utils.FabricaDeDatos.unDonanteDePrueba;

import Bienes.SubCategoria.SubCategoria;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Injector.Injector;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import utils.FabricaDeBienes;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeEntidadesBeneficiarias;
import utils.FechasUtils;

public class CrudSolicitudesDeAsignacion extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CrudSolicitudesDeAsignacion().ejecutar();
  }

  public void ejecutar() {
    super.ejecutar();
    cargarEscenario(DonacionesRepository.getInstance());
  }

  public void cargarEscenario(DonacionesRepository donacionesRepository) {
    BeneficiariosRepository beneficiariosRepository = BeneficiariosRepository.getInstance();
    DonantesRepository donantesRepository = DonantesRepository.getInstance();
    SubCategoria pantalon = SubCategoriasRepository.getInstance().findByNombre("pantalon");
    DonacionIndependiente donacion = FabricaDeDonacionesIndependientes.donacionConBienUnitario(FabricaDeBienes.unBienNuevo(pantalon));
    List<EntidadBeneficiaria> candidatas = FabricaDeEntidadesBeneficiarias.crearCandidatasPara(11, donacion);
    withTransaction(() -> {
      beneficiariosRepository.registrarInstancias(candidatas);
      donantesRepository.registrar(donacion.getDonante());
      donacionesRepository.registrar(donacion);
    });
  }
}