package datosBaseDeDatos.CargaDeDatos;

import Bienes.Categoria.Categoria;
import Bienes.SubCategoria.SubCategoria;
import Repositories.Categorias.CategoriasRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import utils.FabricaDeCategorias;
import utils.FabricaDeSubCategorias;

public class CargarCategoriasYSubCategorias extends ModificadorBaseDeDatos implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new CargarCategoriasYSubCategorias().ejecutar();
  }

  @Override
  public void ejecutar() {
    super.ejecutar();
    cargar();
  }

  public void cargar() {
    Categoria alimento = FabricaDeCategorias.getCategoriaByNombreConExcepcion("alimento");
    Categoria vestimenta = FabricaDeCategorias.getCategoriaByNombreConExcepcion("vestimenta");
    Categoria mobiliario = FabricaDeCategorias.getCategoriaByNombreConExcepcion("mobiliario");
    SubCategoria remera = FabricaDeSubCategorias.getSubCategoriaNoPerecedera("remera", vestimenta);
    SubCategoria pantalon = FabricaDeSubCategorias.getSubCategoriaNoPerecedera("pantalon", vestimenta);
    SubCategoria campera = FabricaDeSubCategorias.getSubCategoriaNoPerecedera("campera", vestimenta);
    SubCategoria arroz  = FabricaDeSubCategorias.getSubCategoriaPerecedera("arroz", alimento);
    SubCategoria fideosSecos  = FabricaDeSubCategorias.getSubCategoriaPerecedera("fideossecos", alimento);
    SubCategoria sopa  = FabricaDeSubCategorias.getSubCategoriaPerecedera("sopa", alimento);
    SubCategoria puerta = FabricaDeSubCategorias.getSubCategoriaNoPerecedera("puerta", mobiliario);
    SubCategoria sillon = FabricaDeSubCategorias.getSubCategoriaNoPerecedera("sillon", mobiliario);
    SubCategoria mesa = FabricaDeSubCategorias.getSubCategoriaNoPerecedera("mesa", mobiliario);


    withTransaction(() -> {
        CategoriasRepository.getInstance().registrarInstancias( List.of(alimento, vestimenta, mobiliario));
      SubCategoriasRepository.getInstance().registrarInstancias(List.of(remera, pantalon, campera, arroz, fideosSecos, sopa, puerta, sillon, mesa));
    });
  }
}
