package datosBaseDeDatos.CargaDeDatos;

import Bienes.SubCategoria.SubCategoria;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import Necesidades.Necesidad;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import utils.FabricaDeEntidadesBeneficiarias;
import utils.FabricaDeNecesidades;

public class CargarNecesidades extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CargarNecesidades().ejecutar();
  }

  @Override
  public void ejecutar() {
    super.ejecutar();
    cargar();
  }

  public void cargar() {
    new CargarCategoriasYSubCategorias().cargar();
    SubCategoria remera = SubCategoriasRepository.getInstance().findByNombre("remera");
    EntidadBeneficiaria entidadConNecesidades = FabricaDeEntidadesBeneficiarias.entidadSinNecesidades();
    FabricaDeNecesidades.nNecesidadesVigentesDe(remera, 20).forEach(entidadConNecesidades::agregarNecesidad);
    withTransaction(() -> {
      BeneficiariosRepository.getInstance().registrar(entidadConNecesidades);
    });
  }

}
